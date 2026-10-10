package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class MovimentacaoEstoque extends Model {

    @Required
    @ManyToOne
    public Medicamento medicamento;

    @ManyToOne
    public Usuario usuario;

    @ManyToOne
    public Venda venda;

    @Required
    public String tipo;

    @Required
    @Min(1)
    public Integer quantidade;

    @Temporal(TemporalType.TIMESTAMP)
    public Date data;

    public String observacao;

    public MovimentacaoEstoque() {
        this.data = new Date();
    }
}