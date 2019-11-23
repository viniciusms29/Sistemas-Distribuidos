package com.marketplace.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="compra")
public class Compra implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", updatable = false, nullable = false)
	private Integer id;	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "idConsumidor")
	private Consumidor consumidor;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "idProduto")
	private Produto produto;
	private Integer quantidade;
	
	public Compra() {}

	public Compra(Consumidor consumidor, Produto produto, Integer quantidade) {
		this.consumidor = consumidor;
		this.produto = produto;
		this.quantidade = quantidade;
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Consumidor getConsumidor() {
		return consumidor;
	}

	public void setConsumidor(Consumidor consumidor) {
		this.consumidor = consumidor;
	}
	@Override 
	public String toString() {
		return id.toString() + '\t' + produto.getNome() + '\t' + String.valueOf(this.getQuantidade()) + '\n';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Compra other = (Compra) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}



		
}