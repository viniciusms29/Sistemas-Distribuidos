package com.marketplace.model;

import java.io.Serializable;

import com.marketplace.controle.RequestEnum;


public class Mensagem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object objeto;
	private RequestEnum tipo;
	
	public Mensagem(Object objeto, RequestEnum tipo) {
		this.objeto = objeto;
		this.tipo = tipo;
	}

	public Object getObjeto() {
		return objeto;
	}

	public RequestEnum getTipo() {
		return tipo;
	}
	
	
}
