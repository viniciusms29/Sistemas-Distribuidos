package com.marketplace.dao;

import java.util.List;

import javax.persistence.NoResultException;

import com.marketplace.model.Compra;
import com.marketplace.utils.GenericoDAO;

public class CompraDAO extends GenericoDAO<Compra, Integer>{
	
	public CompraDAO() {
		super(Compra.class);
	}
	
	public List<Compra> findByConsumidorID(Integer idConsumidor) {
		String sql = "Select a from Compra a " + "where a.consumidor.id = :idConsumidor";
		try {
			List<Compra> compras = em.createQuery(
					sql, Compra.class).setParameter("idConsumidor", idConsumidor).getResultList();
			return compras;
		} catch (NoResultException e) {
			return null;
		}
	}
}
