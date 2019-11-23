package com.marketplace.controle;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.util.Util;

import com.marketplace.model.Anuncio;
import com.marketplace.model.Compra;
import com.marketplace.model.Consumidor;
import com.marketplace.model.Mensagem;
import com.marketplace.model.Pergunta;
import com.marketplace.model.Produto;
import com.marketplace.model.Vendedor;
import com.marketplace.service.AnuncioService;
import com.marketplace.service.CompraService;
import com.marketplace.service.PerguntaService;
import com.marketplace.service.PessoaService;
import com.marketplace.service.ProdutoService;
import com.marketplace.utils.ServiceException;


public class RequestControl extends ReceiverAdapter implements RequestHandler{
	private static final String NOMEGRUPOSERVICE = "Controle_Service";
	private static final String NOMEGRUPOBANCO = "BANCO";
	private JChannel serviceComunicacao;
	private JChannel banco;
	private CompraService compraServo;
	private AnuncioService anuncioServo;
	private ProdutoService produtoServo;
	private PessoaService pessoaServo;
	private PerguntaService perguntaServo;
	private MessageDispatcher despachante;
	private final List<Mensagem> state = new ArrayList<Mensagem>();
	
	public void start() throws Exception {

		compraServo = new CompraService();
		produtoServo = new ProdutoService();
		pessoaServo = new PessoaService();
		anuncioServo = new AnuncioService();
		perguntaServo = new PerguntaService();
		

		
		// instanciando canal de comunicacao com as configuracoes do arquivo.xml
		serviceComunicacao = new JChannel("xml-configs/configUDP.xml");
		banco = new JChannel();
		despachante = new MessageDispatcher(serviceComunicacao, this);
		
		banco.setReceiver(this);
		serviceComunicacao.setReceiver(this);
		banco.connect(NOMEGRUPOBANCO);
        banco.getState(null, 10000);
		serviceComunicacao.connect(NOMEGRUPOSERVICE);

		
		this.mainLoop();
		serviceComunicacao.close();
		banco.close();
	}
	
	
	public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }
	
	private void mainLoop() throws InterruptedException {
		while (true) {
			Thread.sleep(30000);
		}
	}

	public Object handle(Message msg)  {
		Mensagem msr = (Mensagem) SerializationUtils.deserialize(msg.getBuffer());
		System.out.println("CONSULTANDO TIPO");
		switch (msr.getTipo()) {
			case CONSULTAR_TIPO:
				System.out.println("CONSULTANDO TIPO");
				return "BANCO";
			case INSERIR:
				msr.getObjeto();
				try {
					if (msr.getObjeto() instanceof Produto)
						produtoServo.save((Produto) msr.getObjeto());
					else if (msr.getObjeto() instanceof Vendedor)
						pessoaServo.save((Vendedor) msr.getObjeto());
					else if (msr.getObjeto() instanceof Consumidor)
						pessoaServo.save((Consumidor) msr.getObjeto());
					else if (msr.getObjeto() instanceof Compra)
						compraServo.save((Compra) msr.getObjeto());
					else if (msr.getObjeto() instanceof Anuncio)
						anuncioServo.save((Anuncio) msr.getObjeto());
					else if (msr.getObjeto() instanceof Pergunta)
						perguntaServo.save((Pergunta) msr.getObjeto());
					// Adicionando ao stado do banco
					synchronized (state) {
						state.add(msr);
					}
					return true;
				}
				catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			case CADASTRAR_CLIENTE:
				try {
					pessoaServo.save(((Consumidor) msr.getObjeto()));
					// Adicionando ao stado do banco
					synchronized (state) {
						state.add(msr);
					}
					return true;
				}catch (Exception e) {
					return false;
				}
			case CADASTRAR_VENDEDOR:
				try {
					pessoaServo.save((Vendedor) msr.getObjeto());
					// Adicionando ao stado do banco
					synchronized (state) {
						state.add(msr);
					}
					return true;
				}catch (Exception e){
					return false;
				}
			case LOGIN_VENDEDOR:
				return pessoaServo.findCpf(((Vendedor) msr.getObjeto()).getCpf());
			case LOGIN_CONSUMIDOR:
				return pessoaServo.findCpf(((Consumidor) msr.getObjeto()).getCpf());
			case PESQUISAR:
				String pesquisa = (String)msr.getObjeto();
				if (pesquisa.equals(""))
					return anuncioServo.findAll().toString();
				else return anuncioServo.findByName(pesquisa);
			case PEGAR_PRODUTO:
			try {
				return produtoServo.findID((Integer) msr.getObjeto());
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			case ATUALIZAR_BANCO:
				try {
					if (msr.getObjeto() instanceof Produto)
						produtoServo.update((Produto) msr.getObjeto());
					else if (msr.getObjeto() instanceof Vendedor)
						pessoaServo.update((Vendedor) msr.getObjeto());
					else if (msr.getObjeto() instanceof Consumidor)
						pessoaServo.update((Consumidor) msr.getObjeto());
					else if (msr.getObjeto() instanceof Compra)
						compraServo.update((Compra) msr.getObjeto());
					else if (msr.getObjeto() instanceof Anuncio)
						anuncioServo.update((Anuncio) msr.getObjeto());
					else if (msr.getObjeto() instanceof Pergunta)
						perguntaServo.update((Pergunta) msr.getObjeto());
					// Adicionando ao stado do banco
					synchronized (state) {
						state.add(msr);
					}
					
					return true;
				}catch (Exception e) {
					return false;
				}
			case MINHAS_COMPRAS:
				return compraServo.findByConsumidorID(((Consumidor) msr.getObjeto()).getId());
			case CONSULTAR:
				if(anuncioServo.findByName((String) msr.getObjeto()) != null) return true;
				else return false;
			case ATUALIZAR:
				if (msr.getObjeto() instanceof Vendedor)
					return pessoaServo.findCpf(((Vendedor) msr.getObjeto()).getCpf());
				else
					return pessoaServo.findCpf(((Consumidor) msr.getObjeto()).getCpf());
			case VISUALIZAR_PERGUNTA:
				return perguntaServo.findPergunta((Pergunta) msr.getObjeto());
			
			case COMMIT:
				synchronized (state) {
					state.add(msr);
				}
				return pessoaServo.commitBanco();
			
			case ROLLBACK:
				synchronized (state) {
					state.add(msr);
				}
				return pessoaServo.rollbackBanco();
			default:
				return true;
		}

	}

	
	
	public void getState(OutputStream output) throws Exception {
	    System.out.println("ola get");

		synchronized(state) {
	        Util.objectToStream(state, new DataOutputStream(output));
	    }
	}

	@SuppressWarnings("unchecked")
	public void setState(InputStream input) throws Exception {
	    List<Mensagem> msgList;
	    System.out.println("ola set");
	    msgList = (List<Mensagem>) Util.objectFromStream(new DataInputStream(input));
	    synchronized(state) {
	        state.clear();
	        state.addAll(msgList);
	    }
	    for (Mensagem mensagem : msgList) {
	    	System.out.println();
			this.synchronizedState(mensagem);
		}
	}
	public void synchronizedState(Mensagem msr) throws ServiceException {
		System.out.println(msr.getTipo().toString());
		switch (msr.getTipo()) {
			case INSERIR:
				if (msr.getObjeto() instanceof Produto)
					produtoServo.save((Produto) msr.getObjeto());
				else if (msr.getObjeto() instanceof Vendedor)
					pessoaServo.save((Vendedor) msr.getObjeto());
				else if (msr.getObjeto() instanceof Consumidor)
					pessoaServo.save((Consumidor) msr.getObjeto());
				else if (msr.getObjeto() instanceof Compra)
					compraServo.save((Compra) msr.getObjeto());
				else if (msr.getObjeto() instanceof Anuncio)
					anuncioServo.save((Anuncio) msr.getObjeto());
				else if (msr.getObjeto() instanceof Pergunta)
					perguntaServo.save((Pergunta) msr.getObjeto());
				break;
			case CADASTRAR_CLIENTE:
				System.out.println(((Consumidor) msr.getObjeto()).toString());
				pessoaServo.save(((Consumidor) msr.getObjeto()));
				break;
			case CADASTRAR_VENDEDOR:
				System.out.println(((Vendedor) msr.getObjeto()).toString());
				pessoaServo.save((Vendedor) msr.getObjeto());
				break;
			case ATUALIZAR_BANCO:
				if (msr.getObjeto() instanceof Produto)
					produtoServo.update((Produto) msr.getObjeto());
				else if (msr.getObjeto() instanceof Vendedor)
					pessoaServo.update((Vendedor) msr.getObjeto());
				else if (msr.getObjeto() instanceof Consumidor)
					pessoaServo.update((Consumidor) msr.getObjeto());
				else if (msr.getObjeto() instanceof Compra)
					compraServo.update((Compra) msr.getObjeto());
				else if (msr.getObjeto() instanceof Anuncio)
					anuncioServo.update((Anuncio) msr.getObjeto());
				else if (msr.getObjeto() instanceof Pergunta)
					perguntaServo.update((Pergunta) msr.getObjeto());
				break;
			case COMMIT:
				pessoaServo.commitBanco();
				break;
			case ROLLBACK:
				pessoaServo.rollbackBanco();
				break;
		}
	}
	
	

}
