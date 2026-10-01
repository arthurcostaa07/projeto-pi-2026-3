package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Usuario extends Model {

    public String login;
    public String senha;
    public String nome;
    public String email;

    @ManyToOne
    public Perfil perfil;

    public Usuario() {
    }

    public static boolean existeUsuario(String login, String senha) {

        Usuario usuario = Usuario.find(
            "login = ?1 and senha = ?2",
            login,
            senha
        ).first();

        if (usuario == null) {
            return false;
        } else {
            return true;
        }
    }
}
