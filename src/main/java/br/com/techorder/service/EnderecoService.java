package br.com.techorder.service;

import br.com.techorder.dao.ClienteDAO;
import br.com.techorder.dao.EnderecoDAO;
import br.com.techorder.database.Transacao;
import br.com.techorder.dto.CepResponse;
import br.com.techorder.dto.EnderecoRequest;
import br.com.techorder.dto.EnderecoResponse;
import br.com.techorder.dto.ViaCepResponse;
import br.com.techorder.exception.ApiException;
import br.com.techorder.model.Cliente;
import br.com.techorder.model.Endereco;
import br.com.techorder.util.Validacao;

import java.util.List;

public class EnderecoService {

  private final ClienteDAO clienteDAO;
  private final EnderecoDAO enderecoDAO;
  private final ViaCepClient viaCep;

  public EnderecoService(ClienteDAO clienteDAO, EnderecoDAO enderecoDAO, ViaCepClient viaCep) {
    this.clienteDAO = clienteDAO;
    this.enderecoDAO = enderecoDAO;
    this.viaCep = viaCep;
  }

  /** RF04: consulta de endereço por CEP via API externa. */
  public CepResponse consultarCep(String cep) {
    return CepResponse.de(viaCep.consultar(normalizar(cep)));
  }

  /** Cadastra o endereço do cliente: logradouro, bairro, cidade e UF vêm do ViaCEP. */
  public EnderecoResponse cadastrar(Long idCliente, EnderecoRequest req) {
    String cep = normalizar(req.cep());
    String numero = Validacao.texto(req.numero(), "numero", 20);
    ViaCepResponse dados = viaCep.consultar(cep);

    return Transacao.executar(conn -> {
      Cliente cliente = clienteDAO.buscarPorId(conn, idCliente);
      if (cliente == null) {
        throw ApiException.naoEncontrado("Cliente não encontrado: " + idCliente);
      }

      Endereco endereco = new Endereco();
      endereco.setCliente(cliente);
      endereco.setCep(cep.substring(0, 5) + "-" + cep.substring(5));
      endereco.setLogradouro(vazioSeNulo(dados.logradouro()));
      endereco.setNumero(numero);
      endereco.setComplemento(req.complemento() != null ? req.complemento() : dados.complemento());
      endereco.setBairro(vazioSeNulo(dados.bairro()));
      endereco.setCidade(vazioSeNulo(dados.localidade()));
      endereco.setEstado(vazioSeNulo(dados.uf()));

      return EnderecoResponse.de(enderecoDAO.salvar(conn, endereco));
    });
  }

  public List<EnderecoResponse> listarPorCliente(Long idCliente) {
    return Transacao.executar(conn -> {
      if (clienteDAO.buscarPorId(conn, idCliente) == null) {
        throw ApiException.naoEncontrado("Cliente não encontrado: " + idCliente);
      }
      return enderecoDAO.listarPorCliente(conn, idCliente).stream().map(EnderecoResponse::de).toList();
    });
  }

  private String normalizar(String cep) {
    String digitos = cep == null ? "" : cep.replaceAll("\\D", "");
    if (digitos.length() != 8) {
      throw ApiException.requisicaoInvalida("CEP inválido: informe 8 dígitos (ex.: 48110000 ou 48110-000)");
    }
    return digitos;
  }

  private String vazioSeNulo(String valor) {
    return valor == null ? "" : valor;
  }
}
