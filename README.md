# 💰 Controle Financeiro Pessoal ------------- Estou usando para aprender spring

API REST para controle de finanças pessoais, desenvolvida com Java e Spring Boot. Permite gerenciar usuários, categorias e lançamentos financeiros (receitas e despesas), com autenticação via JWT.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Security + JWT** (jjwt 0.11.5)
- **Spring Data JPA + Hibernate**
- **MySQL**
- **Bean Validation**
- **JUnit 5 + Mockito** (testes)
- **H2** (banco em memória para testes)

## 📋 Funcionalidades

- Cadastro e autenticação de usuários com JWT
- CRUD completo de categorias (receita/despesa)
- CRUD completo de lançamentos financeiros com paginação
- Filtro de lançamentos por período e por usuário
- Resumo financeiro (total de receitas, despesas e saldo)
- Validação de dados de entrada
- Tratamento global de exceções

## 🧪 Testes

A suite cobre:
- Testes unitários de services (Mockito)
- Testes de slice de controllers (MockMvc)
- Testes de repositório com banco H2 em memória
- Teste de carregamento do contexto Spring
