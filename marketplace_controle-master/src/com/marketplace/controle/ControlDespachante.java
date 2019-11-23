package com.marketplace.controle;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang.SerializationUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.Buffer;
import org.jgroups.util.RspList;
import com.marketplace.model.Mensagem;

public class ControlDespachante extends MessageDispatcher{
	
	
	public ControlDespachante(JChannel canal, RequestHandler request) {
		super(canal, request);
	}
	
	
	public <T> RspList<T> enviaMulticast(Mensagem conteudo, Boolean all) {
        byte[] data = SerializationUtils.serialize((Serializable) conteudo); // conteudo da mensagem
        RequestOptions opcoes = new RequestOptions();
        
        if (all) {     
	          opcoes.setMode(ResponseMode.GET_ALL); // espera receber a resposta de TODOS membros (ALL, FIRST, NONE)
	          opcoes.setAnycasting(false);
        }else {
        	opcoes.setMode(ResponseMode.GET_FIRST); 
	          opcoes.setAnycasting(false);	
        }
        RspList<T> respList = null;
		try {
			respList = castMessage(null, new Buffer(data), opcoes);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} //MULTICAST
         
        
        return respList;
    }

	public RspList<Object> enviaAnycast(Collection<Address> grupo, Mensagem conteudo, Boolean all) throws Exception{

        byte[] data = SerializationUtils.serialize((Serializable) conteudo);
        
        
        RequestOptions opcoes = new RequestOptions();
        
        if (all) {     
	          opcoes.setMode(ResponseMode.GET_ALL); // espera receber a resposta de TODOS membros (ALL, FIRST, NONE)
	          opcoes.setAnycasting(true);
        }else {
        	opcoes.setMode(ResponseMode.GET_FIRST); 
	          opcoes.setAnycasting(true);	
        } 
    
        RspList<Object> respList = castMessage(grupo, new Buffer(data), opcoes); //ANYCAST
        System.out.println(respList);
       
        return respList;
    }      
    
    public Object enviaUnicast(Address destino, Mensagem conteudo) throws Exception{

        byte[] data = SerializationUtils.serialize((Serializable) conteudo);
        
        RequestOptions opcoes = new RequestOptions(); 
          opcoes.setMode(ResponseMode.GET_FIRST); // nï¾ƒï½£o espera receber a resposta do destino (ALL, MAJORITY, FIRST, NONE)

        String resp = sendMessage(destino, new Buffer(data), opcoes); //UNICAST

        return resp;
    } 
    
    public Object enviaUnicast(Address destino, Mensagem conteudo, RequestOptions opcoes) throws Exception{
        byte[] data = SerializationUtils.serialize((Serializable) conteudo);
       
        String resp = sendMessage(destino, new Buffer(data), opcoes); //UNICAST
        System.out.println("==> Respostas do membro ao UNICAST:\n" +resp+"\n");

        return resp;
    } 


}
