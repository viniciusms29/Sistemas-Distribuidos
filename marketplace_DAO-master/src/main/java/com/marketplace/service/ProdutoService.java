package com.marketplace.service;

import java.util.List;

import com.marketplace.dao.ProdutoDAO;
import com.marketplace.model.Produto;
import com.marketplace.utils.InterfaceService;
import com.marketplace.utils.ServiceException;

public class ProdutoService implements InterfaceService<Produto, Integer> {

	private ProdutoDAO dao = new ProdutoDAO();

	public void save(Produto produto) throws ServiceException {
		
		this.dao.save(produto);

	}

	public void update(Produto obj) throws ServiceException {
		this.dao.update(obj, obj.getId());

	}

	public void delete(Integer id) throws ServiceException {
		this.delete(id);

	}

	public List<Produto> findAll() {

		return this.findAll();
	}

	public Produto findID(Integer id) throws ServiceException {

		return this.dao.findID(id);
	}

}
