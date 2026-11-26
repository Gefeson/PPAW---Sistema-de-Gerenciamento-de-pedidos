package br.com.techorder;

import br.com.techorder.controller.ClienteController;
import br.com.techorder.controller.EnderecoController;
import br.com.techorder.controller.FuncionarioController;
import br.com.techorder.controller.PedidoController;
import br.com.techorder.controller.ProdutoController;
import br.com.techorder.dao.ClienteDAO;
import br.com.techorder.dao.EnderecoDAO;
import br.com.techorder.dao.FuncionarioDAO;
import br.com.techorder.dao.ItemPedidoDAO;
import br.com.techorder.dao.PedidoDAO;
import br.com.techorder.dao.ProdutoDAO;
import br.com.techorder.database.DatabaseInitializer;
import br.com.techorder.http.Resposta;
import br.com.techorder.http.Router;
import br.com.techorder.service.AutenticacaoService;
import br.com.techorder.service.ClienteService;
import br.com.techorder.service.EmailDisponibilidade;
import br.com.techorder.service.EnderecoService;
import br.com.techorder.service.FuncionarioService;
import br.com.techorder.service.PedidoService;
import br.com.techorder.service.ProdutoService;
import br.com.techorder.service.ViaCepClient;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.inicializar();

            // DAOs
            ClienteDAO clienteDAO = new ClienteDAO();
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            ProdutoDAO produtoDAO = new ProdutoDAO();
            EnderecoDAO enderecoDAO = new EnderecoDAO();
            PedidoDAO pedidoDAO = new PedidoDAO(new ItemPedidoDAO());

            // Services (injeção de dependência manual)
            EmailDisponibilidade emails = new EmailDisponibilidade(clienteDAO, funcionarioDAO);
            AutenticacaoService auth = new AutenticacaoService(clienteDAO, funcionarioDAO);
            ClienteService clienteService = new ClienteService(clienteDAO, emails);
            FuncionarioService funcionarioService = new FuncionarioService(funcionarioDAO, emails);
            ProdutoService produtoService = new ProdutoService(produtoDAO);
            EnderecoService enderecoService = new EnderecoService(clienteDAO, enderecoDAO, new ViaCepClient());
            PedidoService pedidoService = new PedidoService(pedidoDAO, produtoDAO, clienteDAO, enderecoDAO);

            funcionarioService.garantirAdministradorInicial();

            // Controllers + rotas
            Router router = new Router();
            router.get("/", r -> Resposta.ok(Map.of("aplicacao", "TechOrder", "status", "online")));
            new ClienteController(clienteService, auth).registrar(router);
            new FuncionarioController(funcionarioService, auth).registrar(router);
            new ProdutoController(produtoService, auth).registrar(router);
            new EnderecoController(enderecoService, auth).registrar(router);
            new PedidoController(pedidoService, auth).registrar(router);

            int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
            servidor.createContext("/", router);
            servidor.setExecutor(Executors.newFixedThreadPool(8));
            servidor.start();

            System.out.println("TechOrder no ar: http://localhost:" + porta);

        } catch (Exception e) {
            System.err.println("Falha ao iniciar o TechOrder: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
