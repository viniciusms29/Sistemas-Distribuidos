package com.marketplace.model;


public class Consumidor extends Pessoa {
//	/**
//	 * 
//	 */
	private static final long serialVersionUID = 1L;
//	
//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "consumidor")
//	private List<Compra> listaCompras; 
//	
	public Consumidor() {
		super();
	}
	public Consumidor(String cpf, String senha) {
		super(cpf, null, null, senha, null);
	}
//	
//	public Consumidor(String cpf, String nome, String telefone, String senha, Float creditos) {
//		super(cpf, nome, telefone, senha, creditos);
//	}
//
//	public List<Compra> getListaCompras() {
//		return listaCompras;
//	}
//
//	public void setCompra(Compra compra) {
//		this.listaCompras.add(compra);
//	}
//	public void setListaCompra(List<Compra> compras) {
//		this.listaCompras = compras;
//	}
	
}