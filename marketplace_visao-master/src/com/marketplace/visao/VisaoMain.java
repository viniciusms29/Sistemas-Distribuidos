package com.marketplace.visao;

import java.util.Scanner;

import com.marketplace.controle.ControlRequest;


public class VisaoMain {
	
	private static ControlRequest request;
	private static Scanner entrada;
	
	public static void main(String[] args) {
		try {
			iniciar_Cliente();
		} catch (Exception e) {
			
			System.out.println("Erro tente novamente mais tarde");
		} finally {
			request.fecharConexoes();
		}
		System.out.println("Bye-Bye");
		return;
	}
	
	private static void iniciar_Cliente() throws Exception{
		entrada = new Scanner(System.in);
		request = new ControlRequest();
		request.pesquisaServidores();
		request.estabelecerConexao();
		
		// Menu principal
		System.out.print(      "\n\t =================================\n" +
							   "\t| 1 - Login Cliente               |\n" +
							   "\t| 2 - Login Vendedor              |\n" +
							   "\t| 3 - Novo Cadastro Cliente       |\n" +
                			   "\t| 4 - Novo Cadastro Vendedor      |\n" +
							   "\t| 0 - Sair                        |\n" +
                			   "\t =================================\n"
							   );
		
        int opc = entrada.nextInt();
        boolean flag = true;
        while(flag) {
	        switch(opc) {
				case 0: System.exit(0);
						break;
						
				case 1: if (entrarCliente('c'))  {
							inicialCliente();
							flag = false;
						}
						else System.out.println("Erro no login: cpf do usuario ou senha esta incorreto");
						break;
				case 2: if (entrarCliente('v')) {
							inicialVendedor(); 
							flag = false;
						}
						else System.out.println("Erro no login: cpf do usuario ou senha esta incorreto");
						break;		
				case 3: if (cadastrar_Cliente('c')) inicialCliente();
						else System.out.println("Erro ao cadastrar Cliente");
						break;
				case 4: if (cadastrar_Cliente('v')) inicialVendedor();
						else System.out.println("Erro ao cadastrar Vendedor");
						break;
				default: System.out.println("\nOpcao invalida."); 
					 	break;
			}
        }
	}

	private static boolean entrarCliente(char tipo) {
		System.out.println("Informe o cpf: ");
		String cpf = entrada.next();
		System.out.println("Informe a senha: ");
	    String senha = entrada.next();
	    boolean tentativa;
	    
	    if(tipo == 'v') {
	    	tentativa = request.login(cpf, senha, tipo);
	    }else {
	    	tentativa = request.login(cpf, senha, tipo);
	    }
	    return tentativa;
	    
	}
	
	private static boolean cadastrar_Cliente(char tipo) {
        String obs = "\t*****************************************************************\n" +
        			 "\t* Informe os dados necessarios abaixo para concluir o cadastro. *\n" +
        			 "\t*****************************************************************\n\n";
        System.out.print(obs + "Nome: ");
        String nome = entrada.nextLine(); // limpa o buffer
        nome = entrada.nextLine(); 
        System.out.print("Cpf: ");
        String cpf = entrada.next();
        
        int tentativas = 1;
        String senha;
        String confsenha;

        do{
        	System.out.print("Senha: ");
        	senha = entrada.next();
        	System.out.print("Confirmar Senha: ");
        	confsenha = entrada.next();

            if(tentativas == 3 && !(senha.equals(confsenha))) {
            	System.out.print("\nDeseja abortar o cadastro? (s/n) ");
                String resp = entrada.next();

                if((resp.toLowerCase()).equals(String.valueOf('s'))) {
                    return false; // retorna ao cli inicial
                }else {
                    tentativas = 1; // reseta as tentativas
                }
            }

            tentativas++;
        }while(!(senha.equals(confsenha)) && tentativas <= 3);
        
        if (tentativas > 3) return false;
        
        if(tipo == 'v') {
        	
        	if(! request.cadastroVendedor(cpf, nome, senha)
        	) {
        		return false; // erro ao cadastrar
        	}
        	else return true;
        }else {
        	if(! request.cadastroCliente(
        			cpf, nome, senha
        			)
        	) {
        		return false; // erro ao cadastrar
        	}
        	else return true;
        	
        }
	}
	
	private static void inicialCliente() {
        while(true) {
			String info = "\n ------------------------------------------------------------------------------- " +
	                 	  "\n|\t 1 -  Consultar Creditos\t\t|\n" +
	                 	  "\n|\t 2 -  Pesquisar produto por nome \t\t|\n" +
	                 	  "\n|\t 3 -  Comprar produto \t\t|\n" +
	                 	  "\n|\t 4 -  Consultar compras \t\t|\n" +
	                 	  "\n|\t 5 -  Consultar todos os produtos \t\t|\n" +
	                 	  "\n|\t 6 -  Fazer pergunta sobre um produto \t\t|\n" +
	                 	  "\n|\t 7 -  Ver minhas perguntas \t\t|\n" +
	                 	  "\n|\t 0 -  Sair \t\t|\n" +
	                 	  " --------------------------------------------------------------------------------- \n";
			
	        System.out.println(info);
	        switch(entrada.nextInt()) {
		        case 1: // Consultar creditos
		        	System.out.print("Creditos: ");
		        	System.out.println(request.consultarCreditos());
		        	break;
		        case 2: // Pesquisar produto
		        	System.out.println("Nome do produto: ");
		        	String pesquisa = "";
		        	entrada.nextLine(); // limpar buffer
		        	pesquisa = entrada.nextLine();
		        	System.out.print(request.pesquisarProduto(pesquisa));
		        	break;
		        case 3: // Comprar produto
		        	System.out.println("Informe o id do produto desejado: ");
		        	int idproduto = entrada.nextInt();
		        	System.out.println("Informe a quantidade desejada: ");
		        	int quantidade = entrada.nextInt();
		        	if(request.comprarProduto(idproduto, quantidade))
		        			System.out.println("Compra realizada com sucesso");
		        	else	System.out.println("Nao foi possivel realizar sua compra");
		        	break;
		        case 4:  // Consultar Compras
		        	System.out.println(request.consultarCompras());
		        	break;
		        case 5:
		        	System.out.println(request.pesquisarProduto(""));
		        	break;
		        case 6:
		        	System.out.println("Pergunta: ");
		        	entrada.nextLine(); // limpar buffer
		        	String pergunta = entrada.nextLine();
		        	System.out.println("Informe o id do produto: ");
		        	int idProduto = entrada.nextInt();
		        	if(request.realizarPergunta(pergunta, idProduto)) 
		        		System.out.println("Pergunta realizada");
		        	else System.out.println("Erro ao realizar pergunta");
		        	break;
		        case 7:
		        	System.out.println("Informe o id do produto");
		        	System.out.println(request.visualizarPergunta(entrada.nextInt()));
		        	break;
		        case 0:
		        	request.fecharConexoes();
		        	return;
		        default: System.out.println("Opcao invalida");
	        }
        }
        
	}
	
	private static void inicialVendedor() {
        while(true) {
			String info = "\n ------------------------------------------------------------------------------- " +
	                 	  "\n|\t 1 -  Consultar Creditos\t\t|" +
	                 	  "\n|\t 2 -  Pesquisar produto por nome \t\t|" +
	                 	  "\n|\t 3 -  Anunciar produto \t\t|" +
	                 	  "\n|\t 4 -  Visualizar perguntas \t\t|" +
	                 	  "\n|\t 0 -  Sair \t\t|\n" +
	                 	  "\n--------------------------------------------------------------------------------- \n";
			
	        System.out.println(info);
	        switch(entrada.nextInt()) {
		        case 1: // Consultar creditos
		        	System.out.print("Creditos: ");
		        	System.out.print(request.consultarCreditos());
		        	break;
		        case 2: // TODO Pesquisar produto
		        	System.out.print(request.pesquisarProduto(entrada.next()));
		        	break;
		        case 3: 
		        	System.out.println("Nome produto: ");
		        	entrada.nextLine(); //limpar buffer
		        	String nome = entrada.nextLine();
		        	System.out.println("Preco produto: ");
		        	Float valor = entrada.nextFloat();
		        	System.out.println("Quantidade produto: ");
		        	Integer qnt = entrada.nextInt();
		        	
		        	if(request.anunciarProduto(nome, valor, qnt)) System.out.println("Anunciado com sucesso");
		        	else System.out.println("Erro ao anunciar produto");
		        	
		        	break;
		        case 4:	
	        		System.out.println("Informe o id do produto");
		        	int idProduto = entrada.nextInt();
		        	System.out.println(request.visualizarPergunta(idProduto));
		        	System.out.println("\n ------------------------------------------------------------------------------- \n"
		        			            + "| 1 - Responder     |\n"
		        			            + "| 2 - Nao responder |\n "
		        			            + "------------------------------------------------------------------------------- \n");
		        	int rsp = entrada.nextInt();
		        	if (rsp == 1) {
		        		System.out.println("Informe o id da pergunta, para a resposta");
		        		rsp = entrada.nextInt();
		        		entrada.nextLine();
		        		request.responderPergunta(rsp, entrada.nextLine());
		        		
		        	}
		        	break;
		        case 0:
		        	entrada.close();
		        	request.fecharConexoes();
		        default: System.out.println("Opcao invalida");
	        }
        }
	}
	
}
