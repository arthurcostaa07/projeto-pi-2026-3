package models;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Medicamento extends Model {

	public String nome;
	public String descricao;
	public BigDecimal preco;
	public Integer qtEstoque;
	public boolean ativo;

	@ManyToOne
	public Categoria categoria;

}