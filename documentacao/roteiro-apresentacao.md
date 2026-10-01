# Roteiro da apresentação — até 15 minutos

## 1. Introdução — 1 min
- Apresentar o Farmatech.
- Explicar que o sistema gerencia medicamentos e categorias.
- Dizer que a nova unidade acrescenta autenticação e autorização.

## 2. Diagrama de classes — 2 min
- Mostrar `Usuario` relacionado a `Perfil`.
- Mostrar `Medicamento` relacionado a `Categoria` por `@ManyToOne`.
- Explicar que o relacionamento da 2ª unidade foi mantido.

## 3. Regras de validação — 2 min
- Medicamento precisa de nome.
- Descrição é obrigatória.
- Preço não pode ser negativo.
- Estoque não pode ser negativo.
- Categoria é obrigatória.
- Categoria duplicada não é aceita.

## 4. Autenticação e interceptor — 3 min
- Abrir o sistema sem sessão.
- Mostrar o bloqueio.
- Abrir o login.
- Entrar com `admin / 123456`.
- Mostrar `usuarioLogado` e `perfilUsuario` na interface.
- Explicar `Seguranca` e `@With(Seguranca.class)`.

## 5. Autorização — 2 min
- Entrar como `atendente / 123456`.
- Mostrar que o botão Remover não aparece.
- Tentar acessar diretamente a ação de remoção e mostrar que o interceptor bloqueia.
- Entrar como `admin / 123456` e mostrar a remoção lógica.

## 6. Histórico — 2 min
- Cadastrar dois ou mais medicamentos.
- Voltar para a listagem.
- Mostrar a lista dos últimos medicamentos cadastrados pelo usuário.
- Explicar que o histórico está na sessão e não cria tabela nova.

## 7. Encerramento — 1 min
- Mostrar que o logout limpa a sessão.
- Destacar que o projeto mantém Play, MVC, JPA, H2 e Bootstrap.
