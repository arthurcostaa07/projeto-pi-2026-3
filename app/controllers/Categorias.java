package controllers;

import java.util.List;

import models.Categoria;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Categorias extends Controller {

  
    @Administrador
    public static void form() {
        render();
    }

    
    @Administrador
    public static void salvar(@Valid Categoria categoria) {
if (validation.hasErrors()) {
	params.flash();
	validation.keep();
	form();
	return;
	
}
	if (categoria == null) {
		flash.error("Categoria invalida");
		form();
		return;
	}
	


        categoria.nome = categoria.nome.trim();

        List<Categoria> categorias = Categoria.findAll();

        for (Categoria c : categorias) {

            if (c.nome != null
                    && c.nome.equalsIgnoreCase(categoria.nome)
                    && (c.ativa == null || c.ativa)) {

                flash.error("Esta categoria já está cadastrada.");
                form();
                return;
            }
        }

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

    
    @Administrador
    public static void editar(Long id) {

        if (id == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        Categoria categoria = Categoria.findById(id);

        if (categoria == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        render(categoria);
    }

    
    @Administrador
    public static void atualizar(Categoria categoria) {

        if (categoria == null) {
            flash.error("Categoria inválida.");
            listar();
            return;
        }

        if (categoria.id == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        if (categoria.nome == null || categoria.nome.trim().isEmpty()) {
            flash.error("Informe o nome da categoria.");
            editar(categoria.id);
            return;
        }

        categoria.nome = categoria.nome.trim();

        Categoria categoriaExistente = Categoria.findById(categoria.id);

        if (categoriaExistente == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        List<Categoria> categorias = Categoria.findAll();

        for (Categoria c : categorias) {

            if (!c.id.equals(categoria.id)
                    && c.nome != null
                    && c.nome.equalsIgnoreCase(categoria.nome)
                    && (c.ativa == null || c.ativa)) {

                flash.error("Já existe outra categoria com este nome.");
                editar(categoria.id);
                return;
            }
        }

        categoria.ativa = true;

        categoria.save();

        flash.success("Categoria atualizada com sucesso.");

        listar();
    }

  
    @Administrador
    public static void remover(Long id) {

        if (id == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        Categoria categoria = Categoria.findById(id);

        if (categoria == null) {
            flash.error("Categoria não encontrada.");
            listar();
            return;
        }

        if (categoria.ativa != null && !categoria.ativa) {
            flash.error("Esta categoria já foi removida.");
            listar();
            return;
        }

        categoria.ativa = false;
        categoria.save();

        flash.success("Categoria removida com sucesso.");

        listar();
    }
}