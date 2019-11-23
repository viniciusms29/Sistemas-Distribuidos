package com.marketplace.controle;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.SerializationUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.RspList;

import com.marketplace.model.Mensagem;

public class ControleMensager extends ReceiverAdapter implements RequestHandler{
	
	
	private static final String NOMEGRUPOCONTROLE = "Visao_Controle";
	private static final String NOMEGRUPOSERVICE = "Controle_Service";
	private static final int MAXNUMWORKERS = 10;
	private static final int NUMTHREADS = 10;
	protected static String srcIp;

	private JChannel controleComunicacao; // canal de comunicacao para dentro do grupo controle
	private JChannel serviceComunicacao; // canal de comunicacao para o grupo service
	private ControlDespachante  despachante_controle; // despachante para dentro do grupo controle
	private ControlDespachante despachante_service; // despachante para o proximo grupo (service)
    private LockService lock_service;
	private List<Address> membrosGrupo;
	protected static Collection<Address> banco;
	private static int view_length;
    
	private WorkerControl[] worker;
    private ExecutorService tpes;
    private Boolean[] usando;
    private Future<Integer> futures[];
   
    public static void main(String[] args)  {
		try {
    		ControleMensager msg = new ControleMensager();
    		msg.start();
    	}catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
		}
    }
    
    //instanciacoes de classe
    @SuppressWarnings("unchecked")
	private void instanciaVariaveis() {
    	// instanciando pool de threads para aceitar conexao da visao (usuario)
        tpes =  Executors.newFixedThreadPool(NUMTHREADS);
        worker = new WorkerControl[MAXNUMWORKERS];
        futures = new Future[MAXNUMWORKERS];
        srcIp = Utilidade.getSystemIP();  // ip da maquina na rede
        usando = new Boolean[MAXNUMWORKERS];
        //Preenche o array de uma vez
        Arrays.fill(usando, false);
    }
    
    /*
     * Mudancas na views 
     */
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
        List<Address> membrosGrupoNovo = serviceComunicacao.getView().getMembers();
        membrosGrupo = membrosGrupoNovo;
		pesquisaBancos();
		
    }
    
    
	// configuracoes iniciais 
	public void start() throws Exception {
		
        this.instanciaVariaveis();
        
		// instanciando canal de comunicacao com as configuracoes do arquivo.xml
		controleComunicacao = new JChannel("xml-configs/sequencer.xml");
		serviceComunicacao = new JChannel("xml-configs/configUDP.xml");
		// iniciando a trancas para unicidade em alguns servicos
        lock_service = new LockService(controleComunicacao);
        
		// instanciando despachante, ele que sera o responsavel pelo envio das menssagens para o resto do grupo
		despachante_controle = new ControlDespachante(controleComunicacao, this);
		despachante_service = new ControlDespachante(serviceComunicacao, this);
        
		// setando quem classe que receber as menssagens do grupo
        controleComunicacao.setReceiver(this);
        controleComunicacao.connect(NOMEGRUPOCONTROLE);
        
        // esta instancia nao recebe menssagem para o grupo service
        serviceComunicacao.setReceiver(this);
        serviceComunicacao.connect(NOMEGRUPOSERVICE);
        
        
        
        // Loop onde esperara a conexao
        mainLoop();
        
        controleComunicacao.close();
		
	}
	
	private void mainLoop() throws InterruptedException {
		// instanciando  classes auxiliares do cluster
		// Address meuEnderecoControle = controleComunicacao.getAddress();
		// Address meuEnderecoService  = serviceComunicacao.getAddress();
		
		membrosGrupo = serviceComunicacao.getView().getMembers();
		pesquisaBancos();
		/*
		 * Obtendo o tamanho do grupo
		 */
		view_length = membrosGrupo.size();
		while(true) {
			if (view_length < membrosGrupo.size()/2) {
				System.out.println("Irei fechar o grupo que estou e menor do que a metade anterior");
				controleComunicacao.close();
				serviceComunicacao.close();
			}
			// fica verificando seus workers caso algum deles falhe ou termine
			
						
			for(int i=0; i<MAXNUMWORKERS; i++) {
				int resp;
				
				if(!usando[i])
						continue;
				if(futures[i].isDone()) {
					try {
						resp = futures[i].get();
						usando[i] = false;
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {

						e.printStackTrace();
					} catch (Exception e){  

						e.printStackTrace();
					}finally {
						usando[i] = false;
					}
				}
				if(futures[i].isCancelled()) {
					//TODO obter causa
					usando[i] = false;
				}
			}
			Thread.sleep(5000);
			
		}
		
	}
	
	@Override
	public Object handle(Message msg) throws Exception {
		Mensagem msr = (Mensagem) SerializationUtils.deserialize(msg.getBuffer());
		

		if (msr.getTipo() == RequestEnum.GET) {
			System.out.println("Recebi uma conexao, tentando estabelecer...");
			
			membrosGrupo = serviceComunicacao.getView().getMembers();
			System.out.println(membrosGrupo.toString());
			if(! pesquisaBancos()) return null; // nao encontrei nenhum banco
			
			for(int i=0; i<MAXNUMWORKERS; i++) {	
				if(!usando[i]) {
					
					ServerSocket server = new ServerSocket(0);
					worker[i] = new WorkerControl(server.getLocalPort(), server, despachante_service, lock_service);
					System.out.printf("Worker %s aguardando conexao...", i);
					futures[i] = tpes.submit(worker[i]);
					usando[i] = true;
					System.out.println(srcIp);
					return srcIp + ":" + server.getLocalPort();
				}
			}
		}else if(msr.getTipo() == RequestEnum.CONSULTAR_TIPO) {
			System.out.println("recebi consulta de tipo");
			return "CONTROLE";
		}
		
		
		return null;
	}
	
	public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }
	
	
	public <T> Boolean pesquisaBancos() {
		RspList<T> resp;
		System.out.println("CONSULTANDO MEMBROS");
		resp = despachante_service.enviaMulticast(
					new Mensagem(null, RequestEnum.CONSULTAR_TIPO), true
					);
		System.out.println(resp);
		banco = new ArrayList<Address>();
		for (Address adr : membrosGrupo) {
			if (((String)resp.getValue(adr)).contains("BANCO")) {
				synchronized (banco) {
					banco.add(adr);
				}
			}		
		}
		System.out.println(banco);
		if(banco.isEmpty()) return false;
		else              return true;
	}
	
}
