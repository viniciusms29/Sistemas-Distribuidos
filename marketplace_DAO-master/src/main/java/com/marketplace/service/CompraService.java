package com.marketplace.service;

import java.util.List;

import com.marketplace.dao.CompraDAO;
import com.marketplace.model.Compra;
import com.marketplace.utils.InterfaceService;
import com.marketplace.utils.ServiceException;

public class CompraService implements InterfaceService<Compra, Integer> {

	private CompraDAO dao = new CompraDAO();

	public void save(Compra compra) throws ServiceException {

		this.dao.save(compra);
		
	}

	public void update(Compra obj) throws ServiceException {
		this.dao.update(obj, obj.getId());

	}

	public void delete(Integer id) throws ServiceException {
		this.dao.delete(id);

	}

	public List<Compra> findAll() {
		return this.dao.findAll();
	}

	public List<Compra> findByConsumidorID(Integer id) {
		return this.dao.findByConsumidorID(id);
	}

	public Compra findID(Integer id) throws ServiceException {

		return this.findID(id);
	}

}
