package com.marketplace.service;

import java.util.List;

import com.marketplace.dao.AnuncioDAO;
import com.marketplace.model.Anuncio;
import com.marketplace.utils.InterfaceService;
import com.marketplace.utils.ServiceException;

public class AnuncioService implements InterfaceService<Anuncio, Integer>{
	
	private AnuncioDAO dao = new AnuncioDAO();
	

	public void save(Anuncio objeto) throws ServiceException {
		this.dao.save(objeto);
		
	}
	
	
	public Anuncio findByName(String nome) {
		return dao.findByName(nome);
	}
	
	public void update(Anuncio obj) throws ServiceException {
		this.dao.update(obj, obj.getId());
		
	}

	public void delete(Integer id) throws ServiceException {
		this.dao.delete(id);
	}

	public List<Anuncio> findAll() {
		return this.dao.findAll();
	}

	public Anuncio findID(Integer id) throws ServiceException {
		
		return this.dao.findID(id);
	}
	
	

}
