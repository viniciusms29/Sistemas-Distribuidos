package com.marketplace.principal;

import javax.persistence.EntityManager;

import com.marketplace.controle.RequestControl;


public class Principal {

	protected static EntityManager em;
	
	
	
	public static void main(String[] args)  {
		try {
    		RequestControl msg = new RequestControl();
    		msg.start();
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    }
}
