package com.marketplace.model;

import java.io.Serializable;


public abstract class Pessoa implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	
	private String cpf;
	private String nome;
	private String telefone;
	private String senha;
	private Float creditos;
	
	public Pessoa() {}
	
	public Pessoa(String cpf, String nome, String telefone, String senha, Float creditos) {
		this.cpf = cpf;
		this.nome = nome;
		this.telefone = telefone;
		this.senha = senha;
		this.creditos = creditos;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Float getCreditos() {
		return creditos;
	}

	public void setCreditos(Float creditos) {
		this.creditos = creditos;
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
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pessoa [id=" + id + ", cpf=" + cpf + ", nome=" + nome + ", telefone=" + telefone + ", senha=" + senha
				+ ", creditos=" + creditos + "]";
	}
	
}
