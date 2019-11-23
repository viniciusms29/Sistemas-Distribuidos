package com.marketplace.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name="anuncio")
public class Anuncio implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", updatable = false, nullable = false)
	private Integer id;
	private String nome;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private  List<Produto> produtos;	
	
	
	public Anuncio() {}
	
	
	
	public String getNome() {
		return nome;
	}



	public void setNome(String nome) {
		this.nome = nome;
	}



	public List<Produto> getProdutos() {
		return produtos;
	}



	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}



	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		String retorno = this.getNome();
		retorno += '\n';
		for (Produto produto : produtos) {
			retorno += "id:" + produto.getId() + "| qnt:" + produto.getQuantidade() + "| Preco:" + String.valueOf(produto.getPreco());
			retorno += '\n';
		}
		return retorno;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Anuncio other = (Anuncio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
