package controllers;

import models.Usuario;
import play.mvc.Before;
import play.mvc.Controller;

public class Seguranca extends Controller {

    @Before
    static void verificarAutenticacao() {

        if (!session.contains("usuarioLogado")) {
            flash.error("Você precisa estar logado para acessar o sistema.");
            Login.form();
            return;
        }

        String login = session.get("usuarioLogado");

        Usuario usuario = Usuario.find(
            "login = ?1",
            login
        ).first();

        if (usuario == null || usuario.perfil == null) {
            session.clear();
            flash.error("Sessão inválida. Faça login novamente.");
            Login.form();
            return;
        }

        session.put("perfilUsuario", usuario.perfil.nome);

        Administrador adminAnnotation = getActionAnnotation(Administrador.class);

        if (adminAnnotation != null && !"ADMIN".equals(usuario.perfil.nome)) {
            forbidden("Acesso restrito aos administradores do sistema.");
            return;
        }
    }
}
