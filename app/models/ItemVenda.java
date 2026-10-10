package models;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class ItemVenda extends Model {

    @Required
    @ManyToOne
    public Venda venda;

    @Required
    @ManyToOne
    public Medicamento medicamento;

    @Required
    @Min(1)
    public Integer quantidade;

    @Required
    @Min(0)
    public BigDecimal precoUnitario;

    @Required
    @Min(0)
    public BigDecimal subtotal;
}
