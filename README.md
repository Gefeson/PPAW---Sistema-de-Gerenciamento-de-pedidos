# Sistema de Gerenciamento de Pedidos para Lojas de Informática

## Descrição do Projeto

O Sistema de gerenciamento de pedidos para lojas de informática foi pensado par facilitar o controle de clientes,
produtos, estoque e pedidos, permitindo que funcionários e clientes realizem operações de maneira dinâmica e organizada.

O sistema terá dois perfis principais de acesso: Cliente e Funcionário, cada um com permissões específicas. 
E os funcionários poderão possuir diferentes níveis de autorização.

## Objetivo
Desenvolver uma aplicação Web utilizando Java e Spring Boot, com arquitetura MVC e comunicação por meio de uma API REST,
permitindo o gerenciamento de pedidos, clientes, produtos e controle de estoque de lojas de informática.

## Equipe
- Fabrício Conceição de Jesus
- Gefeson da Silva Tavares
- Leonardo Pereira da Conceiçao

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
