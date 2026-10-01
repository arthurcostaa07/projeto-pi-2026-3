# Farmatech — Trabalho Avaliativo 3ª Unidade

## Base utilizada
Projeto da 2ª unidade: sistema de gerenciamento de medicamentos e categorias.

A implementação mantém o padrão do Play Framework utilizado no projeto e aplica autenticação por sessão, interceptor e autorização por perfil.

## Requisitos da atividade

1. Diagrama de classes com foco nos relacionamentos.
2. Regras de validação da funcionalidade principal.
3. Login com autenticação por interceptador.
4. Demonstração prática.
5. Projeto versionado com Git e publicado em repositório público no GitHub.

## Funcionalidades implementadas

### Autenticação
- Login com `Login.form()`.
- Autenticação em `Usuario.existeUsuario(login, senha)`.
- Sessão `usuarioLogado`.
- Sessão `perfilUsuario`.
- Interceptor `Seguranca`.
- Logout com `session.clear()`.

### Autorização
- Perfil `ADMIN`.
- Perfil `ATENDENTE`.
- Exclusão de medicamentos e categorias restrita ao `ADMIN`.
- A regra é aplicada no método da ação com a anotação `@Administrador` e validada pelo interceptor.
- O botão de remover também fica oculto para o perfil `ATENDENTE`.

### Histórico
- O histórico dos últimos 5 medicamentos cadastrados pelo usuário é armazenado na sessão.
- Nenhuma tabela ou entidade nova é criada para o histórico.
- O histórico aparece na listagem de medicamentos.

### Validações
- Nome do medicamento obrigatório.
- Descrição obrigatória.
- Preço obrigatório e maior ou igual a zero.
- Estoque obrigatório e maior ou igual a zero.
- Categoria obrigatória.
- Nome da categoria obrigatório.
- Não permite categoria ativa duplicada.

## Contas para demonstração

| Perfil | Login | Senha |
|---|---|---|
| ADMIN | admin | 123456 |
| ATENDENTE | atendente | 123456 |

## Fluxo do login

```text
form.html
   ↓
Login.autenticar(login, senha)
   ↓
Usuario.existeUsuario(login, senha)
   ↓
Sessão: usuarioLogado + perfilUsuario
   ↓
Application.index()
   ↓
Seguranca verifica a sessão nas páginas protegidas
```
