
package controllers;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Medicamento;
import models.Venda;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Application extends Controller {

    public static void index() {

     
        long totalMedicamentos = Medicamento.count("ativo = true");

        List<Medicamento> estoqueBaixo = Medicamento.find(
            "ativo = true and qtEstoque <= 5 order by qtEstoque asc"
        ).fetch();

        long totalEstoqueBaixo = estoqueBaixo.size();

        long totalSemEstoque = Medicamento.count(
            "ativo = true and qtEstoque = 0"
        );

        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.HOUR_OF_DAY, 0);
        calendario.set(Calendar.MINUTE, 0);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);

        Date inicioDia = calendario.getTime();

        calendario.add(Calendar.DAY_OF_MONTH, 1);
        Date inicioProximoDia = calendario.getTime();

        List<Venda> vendasHoje = Venda.find(
            "data >= ?1 and data < ?2 and status = ?3 order by data desc",
            inicioDia,
            inicioProximoDia,
            "FINALIZADA"
        ).fetch();

        long quantidadeVendasHoje = vendasHoje.size();

        BigDecimal faturamentoHoje = BigDecimal.ZERO;

        for (Venda venda : vendasHoje) {
            if (venda.total != null) {
                faturamentoHoje = faturamentoHoje.add(venda.total);
            }
        }

        render(
            totalMedicamentos,
            estoqueBaixo,
            totalEstoqueBaixo,
            totalSemEstoque,
            quantidadeVendasHoje,
            faturamentoHoje
        );
    }
}
