package com.marketplace.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.query.Query;

import com.marketplace.model.Pessoa;
import com.marketplace.utils.GenericoDAO;

public class PessoaDAO extends GenericoDAO<Pessoa, Integer> {
	
	public PessoaDAO() {
		super(Pessoa.class);
	}
	
	public Pessoa findCpf(String cpf) {
		em.getTransaction().begin();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Pessoa> query = builder.createQuery(Pessoa.class);
		Root<Pessoa> root = query.from(Pessoa.class);	
		query.select(root).where(builder.equal(root.get("cpf"), cpf));
		Query<Pessoa> q=(Query<Pessoa>) em.createQuery(query);
		Pessoa pessoa = q.getSingleResult();
		em.getTransaction().commit();
		return pessoa;
		
	}

}
