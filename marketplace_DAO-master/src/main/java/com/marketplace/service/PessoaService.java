package com.marketplace.service;

import java.util.List;

import com.marketplace.dao.PessoaDAO;
import com.marketplace.model.Pessoa;
import com.marketplace.utils.InterfaceService;
import com.marketplace.utils.ServiceException;

public class PessoaService implements InterfaceService<Pessoa, Integer> {

	private PessoaDAO dao = new PessoaDAO();


	public void save(Pessoa objeto) throws ServiceException {
	
		dao.save(objeto);

	}

	public void update(Pessoa obj) throws ServiceException {
		dao.update(obj, obj.getId());

	}
	
	public Boolean commitBanco() {
		return dao.commitBanco();
	}
	
	public Boolean rollbackBanco() {
		return dao.rollbackBanco();
	}
	
	public void delete(Integer id) throws ServiceException {
		dao.delete(id);

	}

	public List<Pessoa> findAll() {
		return dao.findAll();
	}

	public Pessoa findID(Integer id) throws ServiceException {
		return dao.findID(id);
	}
	
	public Pessoa findCpf(String cpf) {
		return dao.findCpf(cpf);
	}


}