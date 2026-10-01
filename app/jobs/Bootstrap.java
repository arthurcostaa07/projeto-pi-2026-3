package jobs;

import models.Perfil;
import models.Usuario;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() throws Exception {

        Perfil admin = Perfil.find(
            "nome = ?1",
            "ADMIN"
        ).first();

        if (admin == null) {
            admin = new Perfil();
            admin.nome = "ADMIN";
            admin.save();
        }

        Perfil atendente = Perfil.find(
            "nome = ?1",
            "ATENDENTE"
        ).first();

        if (atendente == null) {
            atendente = new Perfil();
            atendente.nome = "ATENDENTE";
            atendente.save();
        }

        Usuario adminUsuario = Usuario.find(
            "login = ?1",
            "admin"
        ).first();

        if (adminUsuario == null) {
            adminUsuario = new Usuario();
            adminUsuario.nome = "Administrador";
            adminUsuario.email = "admin@farmatech.com";
            adminUsuario.login = "admin";
            adminUsuario.senha = "123456";
            adminUsuario.perfil = admin;
            adminUsuario.save();
        } else if (adminUsuario.perfil == null) {
            adminUsuario.perfil = admin;
            adminUsuario.save();
        }

        Usuario atendenteUsuario = Usuario.find(
            "login = ?1",
            "atendente"
        ).first();

        if (atendenteUsuario == null) {
            atendenteUsuario = new Usuario();
            atendenteUsuario.nome = "Atendente";
            atendenteUsuario.email = "atendente@farmatech.com";
            atendenteUsuario.login = "atendente";
            atendenteUsuario.senha = "123456";
            atendenteUsuario.perfil = atendente;
            atendenteUsuario.save();
        } else if (atendenteUsuario.perfil == null) {
            atendenteUsuario.perfil = atendente;
            atendenteUsuario.save();
        }

        System.out.println("=================================");
        System.out.println("FARMATECH - USUÁRIOS INICIAIS");
        System.out.println("ADMIN     -> admin / 123456");
        System.out.println("ATENDENTE -> atendente / 123456");
        System.out.println("=================================");
    }
}
