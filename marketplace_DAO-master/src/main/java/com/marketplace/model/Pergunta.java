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
@Table(name="perguntas")
public class Pergunta implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", updatable = false, nullable = false)
	private Integer id;	
	@ManyToOne
	@JoinColumn(name = "idConsumidor")
	private Consumidor consumidor;
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="idProduto")
	private Produto produto;
	private String pergunta;
	private String resposta;
	
	public Pergunta() {}
	
	public Pergunta(Integer id, Consumidor consumidor, String pergunta, String resposta) {
		super();
		this.id = id;
		this.consumidor = consumidor;
		this.pergunta = pergunta;
		this.resposta = resposta;
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
	public String getPergunta() {
		return pergunta;
	}
	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pergunta other = (Pergunta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		if (resposta != null)
			return "Perguntas [id=" + id + ", consumidor=" + consumidor + ", pergunta=" + pergunta + ", resposta="
					+ resposta + "]";
		else 
			return "Perguntas [id=" + id + ", consumidor=" + consumidor + ", pergunta=" + pergunta +  "]";
	}

}
