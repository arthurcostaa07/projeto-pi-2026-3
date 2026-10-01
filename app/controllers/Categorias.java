package controllers;

import java.util.List;

import models.Categoria;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Categorias extends Controller {

    public static void form() {
        render();
    }

    public static void salvar(Categoria categoria) {

        if (categoria == null || categoria.nome == null || categoria.nome.trim().isEmpty()) {
            flash.error("Informe o nome da categoria.");
            form();
            return;
        }

        List<Categoria> categorias = Categoria.findAll();

        for (Categoria c : categorias) {
            if (c.nome != null && c.nome.equalsIgnoreCase(categoria.nome.trim()) &&
                (c.ativa == null || c.ativa)) {

                flash.error("Esta categoria já está cadastrada.");
                form();
                return;
            }
        }

        categoria.nome = categoria.nome.trim();
        categoria.ativa = true;
        categoria.save();

        flash.success("Categoria cadastrada com sucesso.");
        listar();
    }

    public static void listar() {
        List<Categoria> categorias = Categoria.find(
            "ativa is null or ativa = true"
        ).fetch();

        render(categorias);
    }

    public static void editar(Long id) {
        Categoria categoria = Categoria.findById(id);

        if (categoria == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        render(categoria);
    }

    public static void atualizar(Categoria categoria) {

        if (categoria == null || categoria.nome == null || categoria.nome.trim().isEmpty()) {
            flash.error("Informe o nome da categoria.");
            editar(categoria == null ? null : categoria.id);
            return;
        }

        List<Categoria> categorias = Categoria.findAll();

        for (Categoria c : categorias) {
            if (!c.id.equals(categoria.id) &&
                c.nome != null &&
                c.nome.equalsIgnoreCase(categoria.nome.trim()) &&
                (c.ativa == null || c.ativa)) {

                flash.error("Já existe outra categoria com este nome.");
                editar(categoria.id);
                return;
            }
        }

        categoria.nome = categoria.nome.trim();
        categoria.ativa = true;
        categoria.save();

        flash.success("Categoria atualizada com sucesso.");
        listar();
    }

    @Administrador
    public static void remover(Long id) {

        Categoria categoria = Categoria.findById(id);

        if (categoria == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        categoria.ativa = false;
        categoria.save();

        flash.success("Categoria removida com sucesso.");
        listar();
    }
}
