package controllers;

import models.Usuario;
import play.data.validation.Required;
import play.mvc.Controller;

public class Login extends Controller {

    public static void form() {
        render();
    }

    public static void autenticar(
        @Required String login,
        @Required String senha
    ) {

        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            form();
            return;
        }

        if (!Usuario.existeUsuario(login, senha)) {
            flash.error("Usuário ou senha inválidos. Tente novamente!");
            form();
            return;
        }

        Usuario usuario = Usuario.find(
            "login = ?1 and senha = ?2",
            login,
            senha
        ).first();

        session.put("usuarioLogado", usuario.login);
        session.put("perfilUsuario", usuario.perfil.nome);
        session.put("historicoMedicamentos", "");

        flash.success("Login realizado com sucesso!");

        Application.index();
    }

    public static void sair() {
        session.clear();
        flash.success("Sessão encerrada com sucesso!");
        form();
    }
}