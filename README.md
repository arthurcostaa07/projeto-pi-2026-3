# Farmatech

Sistema web de gerenciamento de medicamentos e categorias desenvolvido com **Java + Play Framework 1.x + JPA + H2 + Bootstrap**.

## Trabalho da 3ª Unidade

O projeto foi evoluído para incluir:

- autenticação por login e senha;
- sessão de usuário;
- interceptor de segurança;
- autorização por perfil;
- logout;
- usuário logado visível nas páginas;
- exclusão restrita ao perfil ADMIN;
- histórico dos últimos medicamentos cadastrados pelo usuário na sessão;
- regras de validação;
- diagrama de classes e documentação para apresentação.

## Executar

Abra o projeto no Eclipse/ambiente utilizado nas aulas e execute o Play Framework.

Acesse:

`http://localhost:9000/login/form`

## Usuários de teste

- ADMIN: `admin` / `123456`
- ATENDENTE: `atendente` / `123456`

## Estrutura principal

- `app/models` — entidades JPA
- `app/controllers` — regras e ações
- `app/views` — templates Play/HTML
- `app/jobs/Bootstrap.java` — dados iniciais
- `documentacao` — requisitos, diagrama e roteiro

## Padrão ensinado

O login segue a sequência:

`FORM → AUTENTICAR → Usuario.existeUsuario → SESSÃO → SISTEMA`

A proteção segue:

`@With(Seguranca.class) → @Before → session → autorização`
