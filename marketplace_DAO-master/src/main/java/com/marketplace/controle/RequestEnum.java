package com.marketplace.controle;

public enum RequestEnum {
	ANUNCIAR, 			// anunciar produto 
	COMPRAR,  			// comprar produto
	MINHAS_COMPRAS,     // consultar compras do consumidor
	CONSULTAR,			// consultar produto
	PESQUISAR, 			// pesquisar produto
	INSERIR, 			// inserir produto
	ATUALIZAR,  		// atualizar vendedor ou consumidor
	ATUALIZAR_BANCO,
	GET,                // obter conexao
	CONSULTAR_TIPO,      // obter os tipos do menbros do cluster
	CADASTRAR_CLIENTE,   // cadastro de cliente
	CADASTRAR_VENDEDOR,   
	PEGAR_PRODUTO,       // obter produto do banco abatendo sua quantidade
	DEVOLVER_PRODUTO,    // devolver produto ao banco
	LOGIN_VENDEDOR,
	LOGIN_CONSUMIDOR,
	PERGUNTA_PRODUTO,
	RESPOSTA_PRODUTO,
	VISUALIZAR_PERGUNTA,
	INICIAR_TRANSACAO,   // Para controle no banco
	COMMIT,
	ROLLBACK;
}


