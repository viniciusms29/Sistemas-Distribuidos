package com.marketplace.dao;



import com.marketplace.model.Produto;
import com.marketplace.utils.GenericoDAO;

public class ProdutoDAO extends GenericoDAO<Produto, Integer> {


	public ProdutoDAO() {
		super(Produto.class);
	}



}
