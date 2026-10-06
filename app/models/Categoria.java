package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Categoria extends Model {
    @Required
    public String nome;

    public Boolean ativa;

}