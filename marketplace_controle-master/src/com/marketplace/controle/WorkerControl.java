package com.marketplace.controle;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

import org.jgroups.blocks.locking.LockService;

import com.marketplace.model.Compra;
import com.marketplace.model.Consumidor;
import com.marketplace.model.Mensagem;
import com.marketplace.model.Pergunta;
import com.marketplace.model.Produto;
import com.marketplace.model.Vendedor;

public class WorkerControl implements Callable<Integer>{
	private int worker_number;
	private ServerSocket server;
	private Socket client;
	private Object usuario;
	private ControlDespachante despachante;
	private ObjectInputStream entrada;
	private ObjectOutputStream saida;
	private LockService lock_service;
	private Lock lock;
	

	
	public WorkerControl(int worker_number, ServerSocket server, ControlDespachante despachante, LockService lock_service) {
		this.server = server;
		this.worker_number = worker_number;
		this.despachante = despachante;
		this.lock_service = lock_service;
	}
	
	@Override
	public Integer call() throws Exception{

		// testando se o porto ja esta amarrado ao ip
		if (!server.isBound()) {
			server.bind(new InetSocketAddress(worker_number));
		}
		client = server.accept();
		
		System.out.println("Conexao estebelecida");
		entrada = new ObjectInputStream(client.getInputStream());
		saida   = new ObjectOutputStream(client.getOutputStream());
		// Verificar se usuario e vendedor ou consumidor
		
		while(true) {
			Object ver = entrada.readObject();
			Mensagem msg;
			int tentativas = 0;
			if (ver instanceof Mensagem) {
				msg = (Mensagem) ver;
			
				if (msg.getTipo() == RequestEnum.LOGIN_CONSUMIDOR) {
					usuario = new ClientControl(despachante, (Consumidor) msg.getObjeto());
					this.loopLoginClient();
					break;
				}else if (msg.getTipo() == RequestEnum.LOGIN_VENDEDOR){
					// instanciacao do controle para o vendedor
					usuario = new VendedorControl(despachante, (Vendedor) msg.getObjeto());
					this.loopLoginVendedor();
					break;
				}
				else if(msg.getTipo() == RequestEnum.CADASTRAR_CLIENTE) {
					usuario = new ClientControl(despachante, (Consumidor) msg.getObjeto());
					Boolean resposta = ((ClientControl) usuario).cadastrarConsumidor();
					saida.writeBoolean(resposta);
					saida.flush();
					if (resposta)
						this.loopLoginClient();
					else tentativas += 1;
				} else if(msg.getTipo() == RequestEnum.CADASTRAR_VENDEDOR) {
					usuario = new VendedorControl(despachante, (Vendedor) msg.getObjeto());
					Boolean resposta = ((VendedorControl) usuario).cadastrarVendedor();
					saida.writeBoolean(resposta);
					saida.flush();
					if(resposta)
						this.loopLoginVendedor();
					else tentativas += 1;
				}
				if(tentativas > 3) {
					break;
				}
			}
		}	
		// instanciar  a classe do usuario enviando o despachante de mensagem do servico
		
		// fechando as conexoes 
		entrada.close();
		saida.close();
		server.close();
		
		return worker_number;
	}
	
	// mantem a conexao do vendedor aberta
	private void loopLoginVendedor() throws Exception {
		Mensagem requisicao;
		VendedorControl usuario = ((VendedorControl) this.usuario);
		int tentativas = 0;
		while (!usuario.login() && tentativas <= 3) tentativas += 1;
		lock = this.lock_service.getLock(usuario.getTranca());
		if(tentativas <= 3 && lock.tryLock()) {				
			lock.lock();
			saida.writeBoolean(true); // login realizado
			saida.flush();
		}else {
			saida.writeBoolean(false);
			saida.flush();
			return; // o erro de login causa o fechamento da conexao
		}
		try {
			while(true) {	
				//Requisicao do vendedor
				requisicao = (Mensagem) entrada.readObject(); 
				switch (requisicao.getTipo()) {
					case COMPRAR:
						break;
					case CONSULTAR:
						saida.writeFloat(
								usuario.consultarCreditos()
								);
						saida.flush();
						break;
					case PESQUISAR:
						saida.writeUTF(
								usuario.consultarProdutosNome((String)requisicao.getObjeto())
								);
						saida.flush();
						break;
					case ANUNCIAR: // Operacao de escrita
						Produto produto = (Produto) requisicao.getObjeto();	
						saida.writeBoolean(usuario.anunciarProduto( produto ));
						saida.flush();
						break;
					case VISUALIZAR_PERGUNTA:
						Produto produtoPergunta = (Produto) requisicao.getObjeto();
						saida.writeUTF(usuario.visualizarPergunta(produtoPergunta));
						saida.flush();
						break;
					case RESPOSTA_PRODUTO:
						Pergunta pergunta = (Pergunta) requisicao.getObjeto();
						saida.writeBoolean(usuario.responderPergunta(pergunta));
						saida.flush();
						break;
					default:
						continue;
				}
				usuario.atualizarVendedor();
			}
		} catch (Exception e) {
			throw e; 
		} finally { // sempre liberar tranca
			entrada.close();
			saida.close();
			server.close();
			lock.unlock();
		}
	}
	
	// Date data_atual = (Date)entrada.readObject(); exemplo leitura de objetos
	// mantem a conexao do consumidor aberta 
	private void loopLoginClient() throws  Exception{
		Mensagem requisicao;
		ClientControl usuario = ((ClientControl) this.usuario);
		int tentativas = 0;
		while (!usuario.login() && tentativas <= 3) tentativas += 1;
		lock = this.lock_service.getLock(usuario.getTranca());
		if(tentativas <= 3 && lock.tryLock()) {				
			lock.lock();
			saida.writeBoolean(true); // login realizado
			saida.flush();
		}else {
			saida.writeBoolean(false);
			saida.flush();
			return; // o erro de login causa o fechamento da conexao
		}
		
		try {
			while(true) {
				//Requisicao do cliente
				requisicao = (Mensagem) entrada.readObject(); 
				switch (requisicao.getTipo()) {
					case COMPRAR:
						Compra compra = (Compra) requisicao.getObjeto();
						Lock lckCompra = this.lock_service.getLock(String.valueOf(compra.getProduto().getId()));
						lckCompra.lock(); // tranca para realizar compra
						try {
							saida.writeBoolean(
									usuario.finalizarCompra(compra.getProduto().getId(), compra.getQuantidade())
									);
							saida.flush();
						}finally {
							lckCompra.unlock();
						} // liberando traba de compra
						break;
					case CONSULTAR:
						System.out.println(usuario.consultarCreditos());
						saida.writeFloat(
								usuario.consultarCreditos()
								);
						saida.flush();
						break;
					case PESQUISAR:
						saida.writeUTF(
								usuario.consultarProdutosNome((String)requisicao.getObjeto())
								);
						saida.flush();
						break;
					case MINHAS_COMPRAS:
						saida.writeUTF(
								usuario.minhasCompras()
								);
						saida.flush();
						break;
					case PERGUNTA_PRODUTO:
						Pergunta pergunta = (Pergunta) requisicao.getObjeto();
						saida.writeBoolean(usuario.realizarPergunta(pergunta));
						saida.flush();
						break;
					case VISUALIZAR_PERGUNTA:
						Produto produtoPergunta = (Produto) requisicao.getObjeto();
						saida.writeUTF(usuario.visualizarPergunta(produtoPergunta));
						saida.flush();
						break;
					default:
						continue;
				}
				while(!usuario.atualizarConsumidor());
			}
		} catch (Exception e) {
			throw e; 
		} finally {
			entrada.close();
			saida.close();
			server.close();
			lock.unlock();
		}
		
	}
	
	
}
