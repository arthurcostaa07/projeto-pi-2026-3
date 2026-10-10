package models;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

@Entity
public class Venda extends Model {

    public Date data;
    public BigDecimal total;
    public String formaPagamento;
    public String status;

    @ManyToOne
    public Usuario atendente;

    public Venda() {
        this.data = new Date();
        this.total = BigDecimal.ZERO;
        this.status = "EM_ANDAMENTO";
    }
}
