
package controllers;

import java.util.Date;
import java.util.List;

import models.Medicamento;
import models.MovimentacaoEstoque;
import models.Usuario;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Estoque extends Controller {

    public static void listar() {
        List<Medicamento> medicamentos = Medicamento.find(
            "ativo = true order by nome"
        ).fetch();

        render(medicamentos);
    }

    @Administrador
    public static void movimentar() {
        List<Medicamento> medicamentos = Medicamento.find(
            "ativo = true order by nome"
        ).fetch();

        render(medicamentos);
    }

    @Transactional
    @Administrador
    public static void salvarMovimentacao(
            Long medicamentoId,
            String tipo,
            Integer quantidade,
            String observacao) {

        Medicamento medicamento = Medicamento.findById(medicamentoId);

        if (medicamento == null || !medicamento.ativo) {
            flash.error("Selecione um medicamento válido.");
            movimentar();
            return;
        }

        if (quantidade == null || quantidade < 1) {
            flash.error("A quantidade deve ser maior que zero.");
            movimentar();
            return;
        }

        if (!"ENTRADA".equals(tipo) && !"SAIDA".equals(tipo)) {
            flash.error("Selecione entrada ou saída.");
            movimentar();
            return;
        }

        if ("SAIDA".equals(tipo) && quantidade > medicamento.qtEstoque) {
            flash.error("A saída não pode ser maior que o estoque disponível.");
            movimentar();
            return;
        }

        String login = session.get("usuarioLogado");
        Usuario usuario = Usuario.find("byLogin", login).first();

        if (usuario == null) {
            session.clear();
            flash.error("Usuário não encontrado. Faça login novamente.");
            Login.form();
            return;
        }

        if ("ENTRADA".equals(tipo)) {
            medicamento.qtEstoque += quantidade;
        } else {
            medicamento.qtEstoque -= quantidade;
        }

        medicamento.save();

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.medicamento = medicamento;
        movimentacao.usuario = usuario;
        movimentacao.tipo = tipo;
        movimentacao.quantidade = quantidade;
        movimentacao.data = new Date();
        movimentacao.observacao = observacao;
        movimentacao.save();

        flash.success("Movimentação de estoque registrada com sucesso.");
        listar();
    }

    public static void historico() {
        List<MovimentacaoEstoque> movimentacoes =
            MovimentacaoEstoque.find("order by data desc").fetch();

        render(movimentacoes);
    }
}
