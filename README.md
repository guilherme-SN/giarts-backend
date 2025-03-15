# API RESTful para Ateliê de Crochê

Esta API foi desenvolvida para o projeto de um ateliê de crochê, permitindo a exposição e a gestão de produtos feitos à mão. Ela serve como backend para o site do ateliê, sendo responsável pela lógica de negócios e interações com o banco de dados.

## Visão Geral

A API está estruturada como um microsserviço separada do frontend e expõe uma interface para visualizar e gerenciar produtos e eventos do ateliê. As operações da API incluem a criação, leitura, atualização e exclusão de produtos, eventos e usuários, com autenticação e autorização baseadas em tokens JWT para garantir a segurança dos dados.

## Como Rodar Localmente

1. Clone o repositório:
   ```bash
   git clone https://github.com/guilherme-SN/giarts-backend.git
   ```

2. Execute a aplicação:
   ```
   mvn clean spring-boot:run -Dspring-boot.run.arguments="--api.security.token.secret=<chave_secreta> --admin.email=<email_do_admin> --admin.password=<senha_do_admin>"
   ```

3. Realize as requisições com a URL base: [http://localhost:8080/api/](http://localhost:8080/api)

## Tecnologias Usadas

- **Java 21**
- **Spring Boot, Spring Data e Spring Security**
- **MySQL**
- **Junit & Mockito**
- **Flyway** (para migrações de banco de dados)
- **Docker** (para rodar o MySQL)
- **Swagger** (para documentação de API)

## Endpoints

Todos os endpoints da API estão documentados no Swagger, podendo ser acessado localmente em [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)
### Exemplos de Endpoints:

- `GET /products/{productId}` - Obtém um produto pelo ID
- `GET /products` - Lista todos os produtos com paginação
- `POST /products` - Cria um novo produto
- `PUT /products/{productId}` - Atualiza um produto
- `DELETE /products/{productId}` - Exclui um produto

### Autenticação

Alguns endpoints requerem autenticação. Para isso, a API utiliza tokens JWT. Após autenticar-se com sucesso, o token deve ser incluído no cabeçalho `Authorization` nas próximas requisições.

Exemplo de cabeçalho para autenticação:
```
Authorization: Bearer {token}
```

## Testes

A API possui testes unitários e de integração. Os testes utilizam JUnit e Mockito para garantir a qualidade do código. 

Para rodá-los, basta executar o seguinte comando:

```
mvn clean test
```
