package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import play.data.validation.Valid;
import models.Categoria;
import models.Medicamento;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Medicamentos extends Controller {

    public static void form() {

        List<Categoria> categorias = Categoria.find(
            "ativa is null or ativa = true"
        ).fetch();

        render(categorias);
    }

    public static void listar() {

        List<Medicamento> medicamentos = Medicamento.find(
            "ativo = true"
        ).fetch();

        List<String> historicoMedicamentos = obterHistorico();

        render(medicamentos, historicoMedicamentos);
    }

    public static void salvar(@Valid Medicamento medicamento) {

        if (validation.hasErrors()) {

            params.flash();
            validation.keep();

            form();

            return;
        }

        medicamento.nome = medicamento.nome.trim();

        medicamento.descricao = medicamento.descricao.trim();

        medicamento.ativo = true;

        medicamento.save();

        adicionarAoHistorico(medicamento.nome);

        flash.success("Medicamento cadastrado com sucesso.");

        listar();
    }

    public static void editar(Long id) {

        Medicamento medicamento = Medicamento.findById(id);

        if (medicamento == null) {
            flash.error("Medicamento não encontrado.");
            listar();
            return;
        }

        List<Categoria> categorias = Categoria.find(
            "ativa is null or ativa = true"
        ).fetch();

        render(medicamento, categorias);
    }

    public static void atualizar(Medicamento medicamento) {

        if (medicamento == null) {
            flash.error("Não foi possível atualizar o medicamento.");
            listar();
            return;
        }

        if (medicamento.nome == null || medicamento.nome.trim().isEmpty()) {
            flash.error("Informe o nome do medicamento.");
            editar(medicamento.id);
            return;
        }

        if (medicamento.descricao == null || medicamento.descricao.trim().isEmpty()) {
            flash.error("Informe a descrição do medicamento.");
            editar(medicamento.id);
            return;
        }

        if (medicamento.preco == null || medicamento.preco.doubleValue() < 0) {
            flash.error("Informe um preço válido, maior ou igual a zero.");
            editar(medicamento.id);
            return;
        }

        if (medicamento.qtEstoque == null || medicamento.qtEstoque < 0) {
            flash.error("Informe uma quantidade de estoque válida, maior ou igual a zero.");
            editar(medicamento.id);
            return;
        }

        if (medicamento.categoria == null) {
            flash.error("Selecione uma categoria.");
            editar(medicamento.id);
            return;
        }

        medicamento.nome = medicamento.nome.trim();
        medicamento.descricao = medicamento.descricao.trim();
        medicamento.ativo = true;
        medicamento.save();

        flash.success("Medicamento atualizado com sucesso.");
        listar();
    }

    @Administrador
    public static void remover(Long id) {

        Medicamento medicamento = Medicamento.findById(id);

        if (medicamento == null) {
            flash.error("Medicamento não encontrado.");
            listar();
            return;
        }

        medicamento.ativo = false;
        medicamento.save();

        flash.success("Medicamento removido com sucesso.");
        listar();
    }

    public static void pesquisar(String nome) {

        if (nome == null) {
            nome = "";
        }

        List<Medicamento> medicamentos = Medicamento.find(
            "byNomeLikeAndAtivo",
            "%" + nome.trim() + "%",
            true
        ).fetch();

        List<String> historicoMedicamentos = obterHistorico();

        render("Medicamentos/listar.html", medicamentos, historicoMedicamentos);
    }

    private static void adicionarAoHistorico(String nome) {

        String historico = session.get("historicoMedicamentos");

        if (historico == null || historico.trim().isEmpty()) {
            historico = nome;
        } else {
            historico = nome + "|" + historico;
        }

        String[] itens = historico.split("\\|");
        StringBuilder novoHistorico = new StringBuilder();

        int limite = Math.min(itens.length, 5);

        for (int i = 0; i < limite; i++) {
            if (i > 0) {
                novoHistorico.append("|");
            }
            novoHistorico.append(itens[i]);
        }

        session.put("historicoMedicamentos", novoHistorico.toString());
    }

    private static List<String> obterHistorico() {

        String historico = session.get("historicoMedicamentos");
        List<String> resultado = new ArrayList<String>();

        if (historico != null && !historico.trim().isEmpty()) {
            resultado.addAll(Arrays.asList(historico.split("\\|")));
        }

        return resultado;
    }
}
