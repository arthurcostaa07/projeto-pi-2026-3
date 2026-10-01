package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Perfil extends Model {

    public String nome;

    public Perfil() {
    }

    public Perfil(String nome) {
        this.nome = nome;
    }
}