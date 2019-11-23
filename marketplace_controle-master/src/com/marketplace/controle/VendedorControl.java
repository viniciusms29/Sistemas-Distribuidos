package com.marketplace.controle;

import java.util.ArrayList;
import java.util.List;

import org.jgroups.util.RspList;

import com.marketplace.model.Anuncio;
import com.marketplace.model.Mensagem;
import com.marketplace.model.Pergunta;
import com.marketplace.model.Produto;
import com.marketplace.model.Vendedor;

public class VendedorControl {
	
	private ControlDespachante despachante;
	private Vendedor vendedor;
	private List<Pergunta> perguntas = null;
	private Utilidade utilidade;
	/**
     * metodo contrutor do vendedor
     * @param desp despachante de mensagem para o servidor service
     */
	public VendedorControl(ControlDespachante desp, Vendedor vendedor) {
		despachante = desp;
		this.vendedor = vendedor;
		this.utilidade = new Utilidade();
	}
	
	/**
	 * @throws Exception 
     * metodo de anuncio de produtos do vendedor
     * @param preco: preco do produto
     * @return: true se o anuncio foi bem sucedido, false caso contrario
	 * @throws  
     */
	public Boolean anunciarProduto(Produto produto) throws Exception  {
		List<Object>  resp;
		// setando o vendedor no preco
		produto.setVendedor(vendedor);
		
		// 1 - consultar se  o produto ja tem pelo nome
		 resp = despachante.enviaAnycast(
				 ControleMensager.banco, 
				 new Mensagem(
							new String(produto.getNome()),
							RequestEnum.CONSULTAR
							), true).getResults();
		
		if (!resp.contains(false)) {
			// se sim
			// aumentar a quantidade e criar a tabela de preco referenciando ao vendedor
			Anuncio anuncio;	
			anuncio = (Anuncio) despachante.enviaAnycast(
					ControleMensager.banco, new Mensagem(produto.getNome(), RequestEnum.PESQUISAR), false
					).getFirst();
			List<Produto> produtos = anuncio.getProdutos();
			produtos.add(produto);
			anuncio.setProdutos(produtos);
			resp.addAll(despachante.enviaAnycast(ControleMensager.banco, 
					new Mensagem(anuncio, RequestEnum.ATUALIZAR_BANCO), 
					true).getResults()
			);
			
			if(!resp.contains(false)) { 	// operacao foi bem sucedida em todos os bancos entao commit
				despachante.enviaAnycast(
						 ControleMensager.banco, 
						 new Mensagem(
									null,
									RequestEnum.COMMIT
									), true);
				return true;
			}
			else {  						// alguem nao salvou entao rollback
				despachante.enviaAnycast(
						 ControleMensager.banco, 
						 new Mensagem(
									null,
									RequestEnum.ROLLBACK
									), true);
				return false;
			}
			
		}else {
			
			if (!resp.contains(true)) { // ninguem respondeu que tem o produto
				// se nao
				Anuncio anuncio = new Anuncio();
				List<Produto> produtos = new ArrayList<>();
				produtos.add(produto);
				anuncio.setNome(produto.getNome());
				anuncio.setProdutos(produtos);
				
				resp.addAll(despachante.enviaAnycast(
						 ControleMensager.banco, 
						 new Mensagem(
									anuncio,
									RequestEnum.INSERIR
									), true).getResults()
						);
				
				if(!resp.contains(false)) { // operacao foi bem sucedida em todos os bancos entao commit
					despachante.enviaAnycast(
							 ControleMensager.banco, 
							 new Mensagem(
										null,
										RequestEnum.COMMIT
										), true);
					
				}
				else {  					// alguem nao salvou entao rollback
					despachante.enviaAnycast(
							 ControleMensager.banco, 
							 new Mensagem(
										null,
										RequestEnum.ROLLBACK
										), true);
					return false;
				}
				return true;
			}
			
		}
		
		return false;
		
	}
	
	/**
     * metodo para consulta dos creditos do vendedor
     * @param cpf: cpf do vendedor
     * @return : creditos que o vendedor possui 
     */
	public Float consultarCreditos() {
		return this.vendedor.getCreditos();
	}
	
	public Boolean atualizarVendedor() {
		// TODO realizar atualizacao do vendedor
		try {
			this.vendedor =  (Vendedor) despachante.enviaAnycast(
					ControleMensager.banco, 
					new Mensagem(this.vendedor, RequestEnum.ATUALIZAR), false).getFirst();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String visualizarPergunta(Produto produto) {
		Pergunta pergunta = new Pergunta();
		produto.setVendedor(this.vendedor);
		pergunta.setProduto(produto);
		
		try {
			this.perguntas = (List<Pergunta>) despachante.enviaAnycast(ControleMensager.banco, 
					new Mensagem(pergunta, RequestEnum.VISUALIZAR_PERGUNTA), 
					false).getFirst();
			return (this.perguntas != null) ? this.perguntas.toString() : "Pergunta nao encontrada"; 
		} catch (Exception e) {
			e.printStackTrace();
			return "Pergunta nao encotrada.";
		}
	}
	
	public Boolean responderPergunta(Pergunta pergunta) {
		for (Pergunta prg : this.perguntas) {
			if(prg.getId().equals(pergunta.getId())) {
				prg.setResposta(pergunta.getResposta());
				Mensagem msg = new Mensagem(prg, RequestEnum.ATUALIZAR_BANCO);
				try {
					despachante.enviaAnycast(ControleMensager.banco, msg, true);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	
	public Boolean login() {
		this.vendedor.setCpf(this.utilidade.retiraMAS(this.vendedor.getCpf()));
		String senha = this.vendedor.getSenha();
		try {
			this.vendedor =  (Vendedor) despachante.enviaAnycast(
					ControleMensager.banco, 
					new Mensagem(this.vendedor, RequestEnum.LOGIN_VENDEDOR), false).getFirst();
			if (senha.equals(this.vendedor.getSenha()))
				return true;
			else 
				return false; // senha incorreta
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	public Boolean cadastrarVendedor() {
		this.vendedor.setCpf(this.utilidade.retiraMAS(this.vendedor.getCpf()));
		// 1 -  verificar cpf se e um cpf valido
		if (!this.utilidade.verificaCPF(this.vendedor.getCpf())) {
			return false;
		}
		try {
			List<Object> rsp =  despachante.enviaAnycast(
					ControleMensager.banco, 
					new Mensagem(this.vendedor, RequestEnum.CADASTRAR_VENDEDOR), true).getResults();
			
			if(!rsp.contains(false)) {
				despachante.enviaAnycast(
						 ControleMensager.banco, 
						 new Mensagem(
									null,
									RequestEnum.COMMIT
									), true);
				return true;
			}else { // operacao foi mal sucedida em algum dos bancos
				despachante.enviaAnycast(
						 ControleMensager.banco, 
						 new Mensagem(
									null,
									RequestEnum.ROLLBACK
									), true);
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	/* Tranca para unicidade da utilizacao do usuario
	 * 
	 */
	public String getTranca() {
		return String.valueOf(this.vendedor.hashCode());
	}
	
	/**
     * metodo de busca de produtos por nome
     * @param busca busca a ser realizada
     * @return anuncio com todos os produtos
     */
	public String consultarProdutosNome(String busca){
		RspList<Object> resp;
		try {
			resp =  despachante.enviaAnycast(
					ControleMensager.banco, new Mensagem(busca, RequestEnum.PESQUISAR), false);
			if(resp == null) return "Nenhum anuncio encontrado";
			return resp.getFirst().toString(); 
		} catch (Exception e) {
			e.printStackTrace();
			return "erro";
		}
	}
}
