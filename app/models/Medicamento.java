package models;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Medicamento extends Model {

    @Required
    public String nome;

    @Required
    public String descricao;

    @Required
    @Min(0)
    public BigDecimal preco;

    @Required
    @Min(0)
    public Integer qtEstoque;

    public boolean ativo;

    @Required
    @ManyToOne
    public Categoria categoria;

}