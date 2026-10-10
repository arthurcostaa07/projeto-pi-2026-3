
package controllers;

import java.math.BigDecimal;
import java.util.List;

import models.ItemVenda;
import models.Medicamento;
import models.Usuario;
import models.Venda;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Vendas extends Controller {

    public static void listar() {
        String login = session.get("usuarioLogado");
        String perfil = session.get("perfilUsuario");

        List<Venda> vendas;

        if ("ADMIN".equals(perfil)) {
            vendas = Venda.find("order by data desc").fetch();
        } else {
            Usuario atendente = Usuario.find("byLogin", login).first();

            if (atendente == null) {
                session.clear();
                flash.error("Usuário não encontrado. Faça login novamente.");
                Login.form();
                return;
            }

            vendas = Venda.find(
                "byAtendente order by data desc", atendente
            ).fetch();
        }

        render(vendas);
    }

    public static void form(Long vendaId) {
        Venda venda = null;
        List<ItemVenda> itens = null;

        if (vendaId != null) {
            venda = Venda.findById(vendaId);

            if (venda == null) {
                flash.error("Venda não encontrada.");
                listar();
                return;
            }

            if (!podeAlterar(venda)) {
                forbidden("Você não tem permissão para acessar esta venda.");
                return;
            }

            if (!"EM_ANDAMENTO".equals(venda.status)) {
                flash.error("Esta venda já foi finalizada.");
                detalhes(venda.id);
                return;
            }

            itens = ItemVenda.find("byVenda", venda).fetch();
        }

        List<Medicamento> medicamentos = Medicamento.find(
            "ativo = true and qtEstoque > 0 order by nome"
        ).fetch();

        render(venda, medicamentos, itens);
    }

    @Transactional
    public static void iniciarVenda() {
        String login = session.get("usuarioLogado");

        if (login == null) {
            flash.error("Faça login para registrar uma venda.");
            Login.form();
            return;
        }

        Usuario atendente = Usuario.find("byLogin", login).first();

        if (atendente == null) {
            session.clear();
            flash.error("Usuário não encontrado. Faça login novamente.");
            Login.form();
            return;
        }

        Venda venda = new Venda();
        venda.atendente = atendente;
        venda.total = BigDecimal.ZERO;
        venda.status = "EM_ANDAMENTO";
        venda.save();

        flash.success("Venda iniciada. Adicione os medicamentos.");
        form(venda.id);
    }

    @Transactional
    public static void adicionarItem(
        Long vendaId, Long medicamentoId, Integer quantidade
    ) {
        Venda venda = Venda.findById(vendaId);
        Medicamento medicamento = Medicamento.findById(medicamentoId);

        if (venda == null || medicamento == null) {
            flash.error("Venda ou medicamento não encontrado.");
            listar();
            return;
        }

        if (!podeAlterar(venda)) {
            forbidden("Você não tem permissão para alterar esta venda.");
            return;
        }

        if (!"EM_ANDAMENTO".equals(venda.status)) {
            flash.error("Esta venda não está aberta.");
            listar();
            return;
        }

        if (quantidade == null || quantidade < 1) {
            flash.error("Informe uma quantidade maior que zero.");
            form(venda.id);
            return;
        }

        if (!medicamento.ativo || medicamento.qtEstoque == null
                || medicamento.preco == null || medicamento.qtEstoque < 1) {
            flash.error("Medicamento indisponível.");
            form(venda.id);
            return;
        }

        ItemVenda item = ItemVenda.find(
            "venda = ?1 and medicamento = ?2", venda, medicamento
        ).first();

        int quantidadeAtual = item == null ? 0 : item.quantidade;
        int novaQuantidade = quantidadeAtual + quantidade;

        if (novaQuantidade > medicamento.qtEstoque) {
            flash.error(
                "Quantidade indisponível. Estoque atual: "
                + medicamento.qtEstoque + "."
            );
            form(venda.id);
            return;
        }

        if (item == null) {
            item = new ItemVenda();
            item.venda = venda;
            item.medicamento = medicamento;
            item.precoUnitario = medicamento.preco;
        }

        item.quantidade = novaQuantidade;
        item.subtotal = item.precoUnitario.multiply(
            BigDecimal.valueOf(novaQuantidade)
        );
        item.save();

        recalcularTotal(venda);

        flash.success("Medicamento adicionado ao carrinho.");
        form(venda.id);
    }

    @Transactional
    public static void removerItem(Long itemId) {
        ItemVenda item = ItemVenda.findById(itemId);

        if (item == null || item.venda == null) {
            flash.error("Item não encontrado.");
            listar();
            return;
        }

        Venda venda = item.venda;

        if (!podeAlterar(venda)
                || !"EM_ANDAMENTO".equals(venda.status)) {
            forbidden("Você não tem permissão para alterar este item.");
            return;
        }

        item.delete();
        recalcularTotal(venda);

        flash.success("Item removido do carrinho.");
        form(venda.id);
    }

    @Transactional
    public static void finalizarVenda(
        Long vendaId, String formaPagamento
    ) {
        Venda venda = Venda.findById(vendaId);

        if (venda == null) {
            flash.error("Venda não encontrada.");
            listar();
            return;
        }

        if (!podeAlterar(venda)) {
            forbidden("Você não tem permissão para finalizar esta venda.");
            return;
        }

        if (!"EM_ANDAMENTO".equals(venda.status)) {
            flash.error("Esta venda já foi finalizada.");
            listar();
            return;
        }

        if (!pagamentoValido(formaPagamento)) {
            flash.error("Selecione uma forma de pagamento válida.");
            form(venda.id);
            return;
        }

        List<ItemVenda> itens = ItemVenda.find("byVenda", venda).fetch();

        if (itens.isEmpty()) {
            flash.error(
                "Adicione pelo menos um medicamento antes de finalizar."
            );
            form(venda.id);
            return;
        }

        for (ItemVenda item : itens) {
            Medicamento medicamento = Medicamento.findById(
                item.medicamento.id
            );

            int quantidadeTotal = 0;

            for (ItemVenda outro : itens) {
                if (outro.medicamento != null
                        && outro.medicamento.id.equals(item.medicamento.id)) {
                    quantidadeTotal += outro.quantidade;
                }
            }

            if (medicamento == null || !medicamento.ativo
                    || medicamento.qtEstoque == null
                    || medicamento.qtEstoque < quantidadeTotal) {
                flash.error(
                    "Estoque insuficiente para finalizar a venda. "
                    + "Confira o carrinho."
                );
                form(venda.id);
                return;
            }
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenda item : itens) {
            total = total.add(
                item.precoUnitario.multiply(
                    BigDecimal.valueOf(item.quantidade)
                )
            );
        }

        for (ItemVenda item : itens) {
            Medicamento medicamento = Medicamento.findById(
                item.medicamento.id
            );

            medicamento.qtEstoque -= item.quantidade;
            medicamento.save();

            item.subtotal = item.precoUnitario.multiply(
                BigDecimal.valueOf(item.quantidade)
            );
            item.save();
        }

        venda.total = total;
        venda.formaPagamento = formaPagamento;
        venda.status = "FINALIZADA";
        venda.save();

        flash.success("Venda finalizada com sucesso!");
        detalhes(venda.id);
    }

    public static void detalhes(Long vendaId) {
        Venda venda = Venda.findById(vendaId);

        if (venda == null) {
            flash.error("Venda não encontrada.");
            listar();
            return;
        }

        if (!podeConsultar(venda)) {
            forbidden("Você não tem permissão para consultar esta venda.");
            return;
        }

        List<ItemVenda> itens = ItemVenda.find("byVenda", venda).fetch();

        boolean podeExcluir =
            "ADMIN".equals(session.get("perfilUsuario"))
            || ("EM_ANDAMENTO".equals(venda.status) && podeAlterar(venda));

        render(venda, itens, podeExcluir);
    }

    @Transactional
    public static void excluir(Long vendaId) {
        Venda venda = Venda.findById(vendaId);

        if (venda == null) {
            flash.error("Venda não encontrada.");
            listar();
            return;
        }

        boolean admin = "ADMIN".equals(session.get("perfilUsuario"));
        boolean propriaVenda = podeAlterar(venda);
        boolean finalizada = "FINALIZADA".equals(venda.status);

        if (!admin && !(propriaVenda && !finalizada)) {
            forbidden("Você não tem permissão para excluir esta venda.");
            return;
        }

        List<ItemVenda> itens = ItemVenda.find("byVenda", venda).fetch();

        if (finalizada) {
            for (ItemVenda item : itens) {
                if (item.medicamento != null) {
                    Medicamento medicamento = Medicamento.findById(
                        item.medicamento.id
                    );

                    if (medicamento != null) {
                        if (medicamento.qtEstoque == null) {
                            medicamento.qtEstoque = 0;
                        }

                        medicamento.qtEstoque += item.quantidade;
                        medicamento.save();
                    }
                }
            }
        }

        for (ItemVenda item : itens) {
            item.delete();
        }

        venda.delete();

        if (finalizada) {
            flash.success(
                "Venda excluída e estoque restaurado."
            );
        } else {
            flash.success("Venda em andamento excluída.");
        }

        listar();
    }

    private static void recalcularTotal(Venda venda) {
        List<ItemVenda> itens = ItemVenda.find("byVenda", venda).fetch();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenda item : itens) {
            total = total.add(
                item.precoUnitario.multiply(
                    BigDecimal.valueOf(item.quantidade)
                )
            );
        }

        venda.total = total;
        venda.save();
    }

    private static boolean pagamentoValido(String formaPagamento) {
        return "Dinheiro".equals(formaPagamento)
            || "Pix".equals(formaPagamento)
            || "Cartão de débito".equals(formaPagamento)
            || "Cartão de crédito".equals(formaPagamento);
    }

    private static boolean podeAlterar(Venda venda) {
        String login = session.get("usuarioLogado");

        return login != null && venda != null
            && venda.atendente != null
            && login.equals(venda.atendente.login);
    }

    private static boolean podeConsultar(Venda venda) {
        return "ADMIN".equals(session.get("perfilUsuario"))
            || podeAlterar(venda);
    }
}
