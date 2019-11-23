package com.marketplace.utils;
import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;




public class GenericoDAO<Objeto, I extends Serializable> {

	protected EntityManager em;
	private Class<Objeto> classe;
	
	protected GenericoDAO() {
		em = Conexao.getConnection();
		
	}

	protected GenericoDAO(Class<Objeto> classe) {
		this();
		this.classe = classe;
	}

	public void save(Objeto objeto) throws ServiceException {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		
		em.merge(objeto);
	
	}

	public void update(Objeto obj, Integer id) {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		
		em.getReference(obj.getClass(), id);
		em.merge(obj);
	
	}

	public Boolean commitBanco() {
		em.getTransaction().commit();
		return true;
	}
	
	public Boolean rollbackBanco() {
		em.getTransaction().rollback();
		return true;
	}
	public void delete(I id) {
		
		Objeto objeto = findID(id);
		em.getTransaction().begin();
		Objeto objeto2 = em.merge(objeto);
		em.remove(objeto2);
		em.flush();
		em.getTransaction().commit();
	}

	public List<Objeto> findAll() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Objeto> query = builder.createQuery(classe);
		query.from(classe);
		return em.createQuery(query).getResultList();
	}

	public Objeto findID(I id) {
		return em.find(classe, id);
	}
}
