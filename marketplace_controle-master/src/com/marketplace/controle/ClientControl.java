package com.marketplace.controle;

import java.util.List;

import org.jgroups.util.RspList;

import com.marketplace.model.Anuncio;
import com.marketplace.model.Compra;
import com.marketplace.model.Consumidor;
import com.marketplace.model.Mensagem;
import com.marketplace.model.Pergunta;
import com.marketplace.model.Produto;
import com.marketplace.model.Vendedor;


public class ClientControl {
			
		
	private ControlDespachante despachante;
	private Consumidor consumidor;
	private Utilidade utilidade;
	/**
     * metodo contrutor do cliente
     * @param desp despachante de mensagem para o servidor service
     */
	public ClientControl(ControlDespachante desp, Consumidor consumidor) {
		this.despachante = desp;
		this.consumidor = consumidor;
		this.utilidade = new Utilidade();
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
			System.out.println(resp.toString());
			return ((Anuncio)resp.getFirst()).toString(); 
		} catch (Exception e) {
			e.printStackTrace();
			return "erro";
		}
	}
	
	/*Minhas compras return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String minhasCompras() {
		List<Compra> resp;
		try {
			resp = (List<Compra>) despachante.enviaAnycast(
					ControleMensager.banco, new Mensagem(consumidor, RequestEnum.MINHAS_COMPRAS), false
					).getFirst();
			return resp.toString();
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	/**
     * metodo para finalizar a compra
     * @param consumidor cliente que quer realizar a compra
     * @param idPreco id do preco atrelado ao produto a ser comprado
     * @return true se a finalizacao foi bem sucedida, false caso contrario
	 * @throws Exception 
     */
	public Boolean finalizarCompra(Integer idProduto, Integer quantidade) throws Exception {
		Produto resp;
		resp = (Produto) despachante.enviaAnycast(
				ControleMensager.banco, new Mensagem(idProduto, RequestEnum.PEGAR_PRODUTO), false).getFirst();
		
		if (resp == null) return false;
		if ((consumidor.getCreditos() >= (quantidade * resp.getPreco())) && (quantidade <= resp.getQuantidade())) {
			
			resp.setQuantidade(resp.getQuantidade()-quantidade);
			Compra compra = new Compra(consumidor, resp, quantidade);
			compra.getConsumidor().setCreditos(consumidor.getCreditos()-quantidade*resp.getPreco());
			Vendedor vendedor = resp.getVendedor();
			vendedor.setCreditos(vendedor.getCreditos() + quantidade * resp.getPreco());
			
			
			List<Object> ver = despachante.enviaAnycast(
					ControleMensager.banco, new Mensagem(compra, RequestEnum.INSERIR), true).getResults();
			ver.addAll(despachante.enviaAnycast(
					ControleMensager.banco, new Mensagem(resp, RequestEnum.ATUALIZAR_BANCO), true).getResults());
			
			System.out.println(ver);
			if(!ver.contains(false)) {
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
			
		}
		return false;
			
	}
	
	public Boolean realizarPergunta(Pergunta pergunta) {
		Boolean resp;
		pergunta.setConsumidor(this.consumidor);
		try {
			resp = (Boolean) despachante.enviaAnycast(
					ControleMensager.banco,
					new Mensagem(pergunta, RequestEnum.INSERIR),
					true
					).getFirst();
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public Float consultarCreditos() {
		return consumidor.getCreditos();
	}
	
	public Boolean atualizarConsumidor() {
		// TODO realizar atualizacao do vendedor
		try {
			this.consumidor =  (Consumidor) despachante.enviaAnycast(
					ControleMensager.banco, 
					new Mensagem(this.consumidor, RequestEnum.ATUALIZAR), false).getFirst();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean login() {
		String senha = this.consumidor.getSenha();
		// 1 -  retirar a mascara do cpf se ela existir
		this.consumidor.setCpf(this.utilidade.retiraMAS(this.consumidor.getCpf()));
		try {
			this.consumidor = (Consumidor) despachante.enviaAnycast(
					ControleMensager.banco, 
					new Mensagem(this.consumidor, RequestEnum.LOGIN_CONSUMIDOR), false).getFirst();
			if (senha.equals(this.consumidor.getSenha()))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	@SuppressWarnings("unchecked")
	public String visualizarPergunta(Produto produto) {
		Pergunta pergunta = new Pergunta();
		pergunta.setProduto(produto);
		pergunta.setConsumidor(this.consumidor);
		List<Pergunta> perguntas = null;
		try {
			perguntas = (List<Pergunta>) despachante.enviaAnycast(ControleMensager.banco, 
					new Mensagem(pergunta, RequestEnum.VISUALIZAR_PERGUNTA), 
					false).getFirst();
			return (perguntas != null) ? perguntas.toString() : "Pergunta nao encontrada"; 
		} catch (Exception e) {
			e.printStackTrace();
			return "Pergunta nao encotrada.";
		}
	}
	
	public Boolean cadastrarConsumidor() {
		
		// retirando a mascara do cpf
		this.consumidor.setCpf(this.utilidade.retiraMAS(this.consumidor.getCpf()));
		// 1 -  verificar cpf se e um cpf valido
		if (!this.utilidade.verificaCPF(consumidor.getCpf())) {
			return false;
		}
		
		// 2 -  requisito do trabalho, consumidor inicia com 1000 de creditos
		this.consumidor.setCreditos(Float.valueOf(1000));
		
		try {
			List<Object> rsp =  despachante.enviaAnycast(
					ControleMensager.banco, 
					new Mensagem(this.consumidor, RequestEnum.CADASTRAR_CLIENTE), true).getResults();
			
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
		System.out.println(consumidor);
		return String.valueOf(this.consumidor.getId());
	}
	
}
