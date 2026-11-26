# Sistema de Gerenciamento de Pedidos para Lojas de Informática

## Descrição do Projeto

O Sistema de gerenciamento de pedidos para lojas de informática e-commerce, foi pensado para facilitar o controle de clientes,
produtos, estoque e pedidos, permitindo que funcionários e clientes realizem operações de maneira dinâmica e organizada.

O sistema terá dois perfis principais de acesso: Cliente e Funcionário, cada um com permissões específicas. 
E os funcionários poderão possuir diferentes níveis de autorização.

## Objetivo
Desenvolver uma aplicação Web utilizando Java, com arquitetura MVC e comunicação por meio de uma API REST,
permitindo o gerenciamento de pedidos, clientes, produtos e controle de estoque de lojas de informática.

## Equipe
- Fabrício Conceição de Jesus
- Gefeson da Silva Tavares
- Leonardo Pereira da Conceição

## API Externa
ViaCEP

O projeto utilizará a API pública ViaCEP para consultar informações de endereço a partir de um CEP.
Os dados retornados serão processados pelo sistema e utilizados no cadastro de endereços dos clientes.

Documentação: https://viacep.com.br/

## Requisitos Funcionais
RF01 – Permitir o cadastro e gerenciamento de clientes.

RF02 – Permitir o cadastro e gerenciamento de funcionários.

RF03 – Permitir o cadastro e gerenciamento de produtos.

RF04 – Permitir a consulta de endereço por CEP utilizando a API ViaCEP.

RF05 – Permitir que clientes e funcionários registrem pedidos.

RF06 – Calcular automaticamente o valor total dos pedidos.

RF07 – Verificar a disponibilidade de produtos no estoque.

RF08 – Realizar a baixa automática do estoque quando um pedido for confirmado.

RF09 – Restabelecer o estoque quando um pedido confirmado for cancelado.

RF10 – Permitir que funcionários confirmem e cancelem pedidos.

RF11 – Permitir que clientes acompanhem seus próprios pedidos.

RF12 – Permitir que funcionários consultem todos os pedidos.

RF13 – Restringir o cadastro de funcionários a funcionários com perfil de administrador.

RF14 – Permitir que clientes realizem pagamentos simulados de pedidos confirmados.

RF15 – Disponibilizar as seguintes formas de pagamento: PIX, cartão de crédito, cartão de débito e boleto.

RF16 – Permitir a consulta dos pagamentos registrados.

RF17 – Impedir que um mesmo pedido receba mais de um pagamento.

## Regras de Negócio
RN01 - O sistema terá dois perfis principais: Cliente e Funcionário.

RN02 - Clientes poderão realizar seu próprio cadastro.

RN03 - Funcionários serão cadastrados por um funcionário com perfil de administrador.

RN04 - Clientes e funcionários poderão registrar pedidos.

RN05 - Somente funcionários poderão confirmar ou cancelar pedidos.

RN06 - A confirmação de um pedido realizará a baixa automática dos produtos no estoque.

RN07 - O cancelamento de um pedido confirmado devolverá os produtos ao estoque.

RN08 - O sistema não permitirá a confirmação de pedidos sem estoque suficiente.

RN09 - Clientes poderão consultar apenas seus próprios pedidos.

RN10 - Funcionários poderão consultar todos os pedidos.

## Pagamentos

O TechOrder possui um módulo de pagamentos simulados, permitindo que clientes realizem o pagamento de pedidos confirmados.

As formas de pagamento disponíveis são:

PIX;
Cartão de crédito;
Cartão de débito;
Boleto.

O sistema registra a forma de pagamento, o valor, o status e a data do pagamento no banco de dados.
A aprovação ocorre automaticamente, pois se trata de uma simulação, sem comunicação com serviços financeiros externos.

## Padrões de Projetos
O projeto utilizará o padrão arquitetural MVC (Model-View-Controller), com disponibilização de funcionalidades por meio de uma API REST.

A organização do backend contemplará:

Controller: recebe e encaminha as requisições.

Service: concentra as regras de negócio.

Repository/DAO: responsável pelo acesso aos dados.

DTO: responsável pela transferência de dados entre as camadas.

Model: representa as entidades e estruturas principais do sistema.

As respostas da aplicação serão disponibilizadas em JSON e poderão ser testadas utilizando o Postman.
