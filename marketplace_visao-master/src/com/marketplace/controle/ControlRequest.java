package com.marketplace.controle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.util.RspList;

import com.marketplace.model.Compra;
import com.marketplace.model.Consumidor;
import com.marketplace.model.Mensagem;
import com.marketplace.model.Pergunta;
import com.marketplace.model.Produto;
import com.marketplace.model.Vendedor;


public class ControlRequest implements RequestHandler {
	private ControlDespachante despachante;
	private JChannel controleComunicacao;
	private static final String NOMEGRUPOCONTROLE = "Visao_Controle";
	private List<Address> membrosGrupo;
	private Collection<Address> srv;
	private Socket servidor;
	private ObjectInputStream entrada;
	private ObjectOutputStream saida;
	
	public ControlRequest() {
		try {
			this.controleComunicacao = new JChannel("xml-configs/sequencer.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.despachante = new ControlDespachante(this.controleComunicacao, this);
		
		try {
			controleComunicacao.connect(NOMEGRUPOCONTROLE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		membrosGrupo = controleComunicacao.getView().getMembers();
	}
	
	//-- CONEXAO AREA --//
	/* conecta com algum dos servidores encontrados
	 * 
	 */
	public  Boolean estabelecerConexao() {
		RspList<Object> resp = null;
		try {
			resp = despachante.enviaAnycast(
					srv,
					new Mensagem(null, RequestEnum.GET)
					);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] ipPorto = ((String)resp.getFirst()).split(":");
		try {
			servidor = new Socket(ipPorto[0], Integer.parseInt(ipPorto[1]));
			saida   = new ObjectOutputStream(servidor.getOutputStream());
			entrada = new ObjectInputStream(servidor.getInputStream());
			
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
	/* Pesquisa quais sao os servidores no grupo
	 * 
	 */
	public <T> Boolean pesquisaServidores() {
		RspList<T> resp;
		
		
		resp = despachante.enviaMulticast(
					new Mensagem(null, RequestEnum.CONSULTAR_TIPO), true
					);
		
		srv = new ArrayList<Address>();
		for (Address adr : membrosGrupo) {
			
			if (((String)resp.getValue(adr)).contains("CONTROLE")) {
				srv.add(adr);
			}
			
		}
		if(srv.isEmpty()) return false;
		else              return true;
	}
	
	//-- CADASTRO AREA --//
	/*
	 * 
	 */
	public Boolean cadastroCliente(String cpf, String nome, String senha) {
		Consumidor consumidor = new Consumidor();
		consumidor.setCpf(cpf);
		consumidor.setNome(nome);
		consumidor.setSenha(senha);
		
		Mensagem msg = new Mensagem(consumidor, RequestEnum.CADASTRAR_CLIENTE);
		if(!servidor.isConnected()) {
			if(!this.pesquisaServidores()) return false;
			else  this.estabelecerConexao();
		}
		try {
			saida.writeObject(msg);
		    saida.flush();
			return (entrada.readBoolean());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
	
	
	
	/*
	 * 
	 */
	public Boolean cadastroVendedor(String cpf, String nome, String senha) {
		Vendedor vendedor = new Vendedor();
		vendedor.setCpf(cpf);
		vendedor.setNome(nome);
		vendedor.setSenha(senha);
		vendedor.setCreditos(0f);
		
		Mensagem msg = new Mensagem(vendedor, RequestEnum.CADASTRAR_VENDEDOR);

		if(!servidor.isConnected()) {
			if(!this.pesquisaServidores()) return false;
			else  this.estabelecerConexao();
		}
		try {
			saida.writeObject(msg);
		    saida.flush();
			return (entrada.readBoolean());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}
	
	
	//-- CLIENTE REQUISICAO AREA --//
	
	public Float consultarCreditos() {
		Mensagem msg = new Mensagem(null, RequestEnum.CONSULTAR);
		
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readFloat();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0f;
		}
	}
	
	public String pesquisarProduto(String nomeProduto) {
		Mensagem msg = new Mensagem(nomeProduto, RequestEnum.PESQUISAR);
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public Boolean comprarProduto(Integer idProduto, Integer quantidade) {
		Produto produto = new Produto();
		produto.setId(idProduto);
		Compra compra = new Compra();
		compra.setProduto(produto);
		compra.setQuantidade(quantidade);
		
		Mensagem msg = new Mensagem(compra, RequestEnum.COMPRAR);
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}
	
	public String consultarCompras() {
		Mensagem msg = new Mensagem(null, RequestEnum.MINHAS_COMPRAS);
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readUTF();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
		
	}
	public Boolean realizarPergunta(String perguntaString, Integer idProduto) {
		Produto produto = new Produto();
		produto.setId(idProduto);
		Pergunta pergunta = new Pergunta();
		pergunta.setPergunta(perguntaString);
		pergunta.setProduto(produto);
		Mensagem msg = new Mensagem(pergunta, RequestEnum.PERGUNTA_PRODUTO);
		
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readBoolean();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	//-- VENDEDOR REQUISICAO AREA --//
	public Boolean anunciarProduto(String nome, Float valor, Integer quantidade) {
		Produto produto = new Produto();
		produto.setNome(nome);
		produto.setPreco(valor);
		produto.setQuantidade(quantidade);
		Mensagem msg = new Mensagem(produto, RequestEnum.ANUNCIAR);
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readBoolean();
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public String visualizarPergunta(Integer idProduto) {
		Produto produto = new Produto();
		produto.setId(idProduto);
		Mensagem msg = new Mensagem(produto, RequestEnum.VISUALIZAR_PERGUNTA);
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readUTF();
		} catch (Exception e) {
			e.printStackTrace();
			return "Nao encontrado";
		}
	}
	
	public Boolean responderPergunta(Integer idPergunta, String resposta) {
		Pergunta prg = new Pergunta();
		prg.setId(idPergunta);
		prg.setResposta(resposta);
		Mensagem msg = new Mensagem(prg, RequestEnum.RESPOSTA_PRODUTO);
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readBoolean();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/* Login
	 * 
	 */
	public Boolean login(String cpf, String senha, char tipo) {
		Mensagem msg;
		if (tipo == 'v') {
			Vendedor pessoa = new Vendedor(cpf, senha);
			msg = new Mensagem(
					pessoa, RequestEnum.LOGIN_VENDEDOR
					);
		}
		else { 
			Consumidor pessoa = new Consumidor(cpf, senha);
			msg = new Mensagem(
					pessoa, RequestEnum.LOGIN_CONSUMIDOR
					);
		}
		try {
			saida.writeObject(msg);
			saida.flush();
			return entrada.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/* 
	 * fechamento das conexoes abertas pelo usuario
	 */
	public void fecharConexoes() {
		try {
			entrada.close();
			saida.close();
			servidor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 */
	@Override
	public Object handle(Message arg0) throws Exception {
		return "VISAO";
	}
	
	
	
}
