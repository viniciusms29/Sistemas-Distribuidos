package com.marketplace.service;

import java.util.List;

import com.marketplace.dao.PerguntaDAO;
import com.marketplace.model.Pergunta;
import com.marketplace.utils.InterfaceService;
import com.marketplace.utils.ServiceException;

public class PerguntaService implements InterfaceService<Pergunta, Integer> {
	
	private PerguntaDAO dao = new PerguntaDAO();

	public void save(Pergunta objeto) throws ServiceException {
		this.dao.save(objeto);
		
	}
	
	public List<Pergunta> findPergunta(Pergunta perg) {
		if (perg.getProduto().getVendedor() != null) 
			return dao.findPerguntaIdProdutoCpfVendedor(
						perg.getProduto().getId(), perg.getProduto().getVendedor().getCpf()
						);
		else 
			return dao.findPerguntaIdProdutoIdConsumidor(
					perg.getProduto().getId(), perg.getConsumidor().getId()
					);
	}
	
	public void update(Pergunta obj) throws ServiceException {
		this.dao.update(obj, obj.getId());
	}

	public void delete(Integer id) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	public List<Pergunta> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Pergunta findID(Integer id) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
