package com.marketplace.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.marketplace.model.Anuncio;
import com.marketplace.utils.GenericoDAO;

public class AnuncioDAO extends GenericoDAO<Anuncio, Integer>{
	
	public AnuncioDAO() {
		super(Anuncio.class);
	}
	
	public Anuncio findByName(String nome) {
		String sql = "Select a from Anuncio a " + "where a.nome = :nome";
		EntityManager em = this.em;
		try {
			Anuncio anuncio = em.createQuery(sql, Anuncio.class).setParameter("nome", nome).getSingleResult();
			return anuncio;
		} catch (NoResultException e) {
			return null;
		}

	}

}
