package com.marketplace.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.hibernate.query.Query;

import com.marketplace.model.Pergunta;
import com.marketplace.utils.GenericoDAO;

public class PerguntaDAO extends GenericoDAO<Pergunta, Integer> {

	public PerguntaDAO() {
		super(Pergunta.class);
	}
	
	public List<Pergunta> findPerguntaIdProdutoCpfVendedor(Integer idProduto, String cpf) {
		String sql = "Select a from Pergunta a, Pessoa u, Produto p " + 
	"where a.produto.id = :idProduto and p.vendedor.id = u.id and u.cpf = :cpf GROUP BY a.id ";
		EntityManager em = this.em;
		try {
			Query<Pergunta> q = (Query<Pergunta>) em.createQuery(sql, Pergunta.class);
			q.setParameter("idProduto", idProduto);
			q.setParameter("cpf",cpf);
			return   q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	public List<Pergunta> findPerguntaIdProdutoIdConsumidor(Integer idProduto, Integer idConsumidor) {
		String sql = "Select a from Pergunta a " + 
	"where a.produto.id = :idProduto and a.consumidor.id = :idConsumidor  GROUP BY a.id ";
		EntityManager em = this.em;
		try {
			Query<Pergunta> q = (Query<Pergunta>) em.createQuery(sql, Pergunta.class);
			q.setParameter("idProduto", idProduto);
			q.setParameter("idConsumidor",idConsumidor);
			return   q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}
