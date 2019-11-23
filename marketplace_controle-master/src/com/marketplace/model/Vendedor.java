package com.marketplace.model;


public class Vendedor extends Pessoa{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vendedor")
//	private List<Preco> precos;  // precos relacionados ao vendedor
//	
//	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "vendedor")
//	private List<Anuncio> anuncios; 
//	
	public Vendedor() {
		super();
	}
	public Vendedor(String cpf, String senha) { // para o login
		super(cpf, null, null, senha, null);
	}
//	public Vendedor(String cpf, String nome, String telefone, String senha, Float creditos) {
//		super(cpf, nome, telefone, senha, creditos);
//	}
//	public Vendedor(List<Preco> precos, List<Anuncio> anuncios) {
//		super();
//		this.precos = precos;
//		this.anuncios = anuncios;
//	}
//
//	public List<Preco> getPrecos() {
//		return precos;
//	}
//
//	public void setPreco(Preco preco) {
//		this.precos.add(preco);
//	}
//
//	public List<Anuncio> getAnuncios() {
//		return anuncios;
//	}
//
//	public void setAnuncio(Anuncio anuncio) {
//		this.anuncios.add(anuncio);
//	}
	

}