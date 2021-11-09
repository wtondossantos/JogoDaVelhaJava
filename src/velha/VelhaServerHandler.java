package velha;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;


/**
 * Classe que implementa o manipulador de sockets do Servidor do Jogo da Velha. 
 */
public class VelhaServerHandler extends IoHandlerAdapter {
	
	/** Indica se o Modo Debug esta' ativado. */
	public static final boolean DEBUG_ENABLED = true;

	/** Classe que representa a conexao de um cliente. */
	private class VelhaClientConnection {
		/** Objeto de sessao do cliente */
		private IoSession session = null;
		/** Id do Jogador */
		private char id = VelhaEngine.JOGADOR_VAZIO;
		/* setters e getters */
		public void setSession(IoSession session) {
			this.session = session;
		}
		public IoSession getSession() {
			return session;
		}
		public void setId(char id) {
			this.id = id;
		}
		public char getId() {
			return id;
		}
	}

	/** Lista de clientes conectados - 0, 1 ou 2 */
	private List<VelhaClientConnection> clients =
				new ArrayList<VelhaClientConnection>();

	/** Indice na lista de clientes, correspondente ao jogador da vez
	 *  -1, 0 ou 1 */
	private int jogadorDaVez = -1;
	
	/** Logica do Jogo da Velha. */
	private VelhaEngine velhaEngine = new VelhaEngine();
	/** Parser de XML do Jogo da Velha. */
	private VelhaParser velhaParser = new VelhaParser();
	/** Bean (POJO) com a representacao do XML do Jogo da Velha. */
	private VelhaBean   velhaBean   = new VelhaBean();
	
	/**
	 * Evento disparado quando uma sessao (conexao) e' aberta.
	 * @param session Referencia para objeto que representa a sessao
	 */
    @Override
    public void sessionOpened(IoSession session) {

    	int clientsSizeBefore = clients.size(); 
    	int clientsSizeAfter  = clientsSizeBefore; 
    	
    	VelhaClientConnection client = null;

		if (clientsSizeBefore == 2) {
    		/* entrou um terceiro jogador, ele sera' derrubado */
			
			if (DEBUG_ENABLED) {
				System.out.println("Conexao com "
						+ session.getRemoteAddress().toString()
						+ " finalizada.");
			}
			
			session.close(false);
            return;
            
    	} else { /* entrou algum jogador */

    		/* guarda a referencia da sessao */
    		client = new VelhaClientConnection();
    		client.setSession(session);
    		
    		/* sorteia um Id de jogador (X ou O) */
    		char idJogador = 
    			(new Random().nextInt(2) == 0) ?
    					VelhaEngine.JOGADOR_X : VelhaEngine.JOGADOR_O;
    		
    		/* configura o Id do jogador */
    		switch (clientsSizeBefore) {
    			case 0:
    				client.setId(idJogador);
    				break;  
    			case 1:
    				client.setId(
    						VelhaEngine.getOponente(clients.get(0).getId()));
    				break;  
    		}
    		/* adiciona cliente 'a lista */
    		clients.add(client);
    		clientsSizeAfter = clients.size();

			if (DEBUG_ENABLED) {
				System.out.println("Jogador " + String.valueOf(client.getId())
						+ " conectou ("	+ getClientAddress(client) + ").");
			}
        	
    		/* envia um XML ao cliente, com status AGUARDE */
    		enviarStatusJogo(client, VelhaEngine.STATUS_AGUARDE);
    		
    	}
    	
    	if (clientsSizeBefore == 1 &&
    		clientsSizeAfter  == 2) { /* entrou o segundo jogador */

    		/* sorteia quem comeca o jogo */
    		jogadorDaVez = new Random().nextInt(2);
    		client = clients.get(jogadorDaVez);
    		
			if (DEBUG_ENABLED) {
				System.out.println("Jogador " + String.valueOf(client.getId())
						+ " inicia o jogo.");
			}
			
    		/* envia um XML ao cliente, com status JOGUE */
    		enviarStatusJogo(client, VelhaEngine.STATUS_JOGUE);
    	}
    }
    
	/**
	 * Evento disparado quando uma sessao (conexao) e' fechada.
	 * @param session Referencia para objeto que representa a sessao
	 */
    @Override
    public void sessionClosed(IoSession session) {

    	int clientsSizeBefore = clients.size(); 
    	int clientsSizeAfter  = clientsSizeBefore;
    	
		VelhaClientConnection client = null;
		
    	/* algum cliente desconectou */
    	int idx = getIdxBySessao(session);
    	
    	if (idx != -1) {
    		
    		client = clients.get(idx);
    		
			if (DEBUG_ENABLED) {
				System.out.println("Jogador " + String.valueOf(client.getId())
						+ " desconectou (" + getClientAddress(client) + ").");
			}
	    	/* remove da lista */
    		clients.remove(client);
    		clientsSizeAfter = clients.size(); 
    	}
    	
		/* um jogador saiu mas ficou o outro */
   		if (clientsSizeBefore == 2 &&
   			clientsSizeAfter  == 1) {
   			
   			client = clients.get(0);
    		/* envia um XML ao cliente, com status WO */
    		enviarStatusJogo(client, VelhaEngine.STATUS_WO);
    		/* fecha a conexao */
    		client.getSession().close(true);
    			
   		} else if (clientsSizeBefore == 1 &&
   				   clientsSizeAfter  == 0) {
   			/* o ultimo jogador saiu, nao tem mais ninguem pra jogar */
   			/* o servidor fica ocioso esperando alguem conectar */
			if (DEBUG_ENABLED) System.out.println("Aguardando conexoes...");
   		}
   		
   		/* se um dos jogadores desconectou, reinicia status do jogo */
   		if (clientsSizeAfter < 2) {
   			jogadorDaVez = -1;
   			velhaEngine.limparTabuleiro();
   			velhaBean.limpar();
   		}
   	}

	/**
	 * Evento disparado quando uma mensagem e' recebida.
	 * @param session Referencia para objeto que representa a sessao
	 * @param message Mensagem recebida
	 */
    @Override
    public void messageReceived(IoSession session, Object message) {
        /* processa a msg recebida */
        processarMensagem(session, message.toString());
    }
	
    /**
     * Processa uma mensagem recebida, realizando as acoes correspondentes.
     * @param session Sessao do cliente que enviou a mensagem
     * @param message Mensagem em formato texto
     */
    private void processarMensagem(IoSession session, String message) {

    	/* se nao tem 2 jogadores conectados, ignora mensagem */
    	if (clients.size() != 2) { return; }
		/* se e' a vez de nenhum jogador, ignora mensagem */
		if (jogadorDaVez != 0 && jogadorDaVez != 1) { return; }

		VelhaClientConnection client = null;
		
		/* procura na lista de conexoes */
		int idx = getIdxBySessao(session);
		
		/* se nao esta' na lista, ignora mensagem */
		if (idx == -1) { return; }

        if (DEBUG_ENABLED) {
        	client = clients.get(idx);
			System.out.println("  Jogador " + String.valueOf(client.getId())
					+ " (" + getClientAddress(client) + "):");
        	System.out.println("    <= XML recebido: " + message.toString());
        }
        
		/* verifica se jogador esta' na vez certa */
		if (jogadorDaVez == idx) {
			/* ok, jogador na vez certa */

			/* converte o XML da mensagem para um objeto VelhaBean */
			VelhaBean tempBean = velhaParser.getVelhaBean(message);
			
			/* verifica objeto VelhaBean gerado a partir do XML */
			if (verificarVelhaBean(tempBean)) {
				/* se nao houve erros, executa as acoes */
				executarAcaoJogada(tempBean);
				
			} else {
				/* se houve erro, reenvia o XML ao cliente */
				client = clients.get(jogadorDaVez);

				if (DEBUG_ENABLED) {
					System.out.println("Jogador "
							+ String.valueOf(client.getId())
							+ " enviou um XML com problemas.");
				}
				
	    		/* envia um XML ao cliente, com status JOGUE */
	    		enviarStatusJogo(client, VelhaEngine.STATUS_JOGUE);
			}
			
		} else {
			/* errado! nao e' a vez desse jogador */
			/* reenvia o XML ao cliente */
			client = clients.get(idx);
			
			if (DEBUG_ENABLED) {
				System.out.println("Jogador "
						+ String.valueOf(client.getId())
						+ " jogou na vez errada!");
			}

			/* envia um XML ao cliente, com status AGUARDE */
    		enviarStatusJogo(client, VelhaEngine.STATUS_AGUARDE);
		}
    }

    /**
     * Envia um XML ao cliente, com o status especificado.
     * @param client Conexao do cliente
     * @param status Status a ser preenchido no XML
     */
	private void enviarStatusJogo(VelhaClientConnection client, String status) {
		
		/* configura o bean que representa o XML */
		velhaBean.setId(client.getId());
		velhaBean.setStatus(status);
		velhaBean.setJogada(-1);
		velhaBean.copiarTabuleiro(velhaEngine.getTabuleiro());

		/* envia um XML ao cliente (jogador da vez) */
		String velhaXML = velhaParser.getVelhaXML(velhaBean);
		if (client.session.isConnected()) {

			client.session.write(velhaXML);
			
			if (DEBUG_ENABLED) {
				System.out.println("  Jogador "
						+ String.valueOf(client.getId()) 
						+ "(" + getClientAddress(client) + "):");
				System.out.println("    => XML enviado: " + velhaXML);
			}
		}
		
	}

	/**
	 * Executa as acoes da jogada.
	 * @param velha Objeto VelhaBean
	 */
	private void executarAcaoJogada(VelhaBean velha) {

		/* calcula indice do proximo jogador na lista de conexoes */
		int jogadorProximo = -1;
		if      (jogadorDaVez == 0) jogadorProximo = 1;
    	else if (jogadorDaVez == 1) jogadorProximo = 0;
		
		/* conexoes dos jogadores */
		VelhaClientConnection clientVez  = clients.get(jogadorDaVez);
		VelhaClientConnection clientProx = clients.get(jogadorProximo);
		
		/* status dos jogadores */
		String statusVez  = VelhaEngine.STATUS_AGUARDE;
		String statusProx = VelhaEngine.STATUS_JOGUE;

		/* registra jogada */
		velhaEngine.setPosicao(velha.getJogada(), velha.getId());

		if (DEBUG_ENABLED) {
			System.out.println("Jogador "
					+ String.valueOf(clientVez.getId())
					+ " jogou na posicao " + velha.getJogada());
		}
		
		/* verifica se houve empate */
		if (velhaEngine.isEmpate()) {
			/* status dos jogadores */
			statusVez  = VelhaEngine.STATUS_EMPATE;
			statusProx = VelhaEngine.STATUS_EMPATE;

			if (DEBUG_ENABLED) System.out.println("Houve empate!");

		/* verifica se jogador da vez ganhou */
		} else if (velhaEngine.isGanhador(clientVez.getId())) {
			/* status dos jogadores */
			statusVez  = VelhaEngine.STATUS_GANHOU;
			statusProx = VelhaEngine.STATUS_PERDEU;

			if (DEBUG_ENABLED) {
				System.out.println("Jogador "
						+ String.valueOf(clientVez.getId())	+ " ganhou!");
			}
		}
		
		/* configura proximo jogador */
		jogadorDaVez = jogadorProximo;
		
		/* envia um XML ao cliente (jogador da vez) */
		enviarStatusJogo(clientVez, statusVez);
		/* envia um XML ao cliente (proximo jogador) */
		enviarStatusJogo(clientProx, statusProx);
		
		/* verifica se jogo encerrou (game over) */
		if (velhaEngine.isGameOver()) {
			/* fecha conexoes dos clientes */
			clientVez.getSession().close(true);
			clientProx.getSession().close(true);

			if (DEBUG_ENABLED) System.out.println("Game over!");
		}
	}

	/**
	 * Retorna o indice na lista de clientes conectados, a partir de um objeto
	 * sessao. 
	 * @param session Sessao do cliente conectado
	 * @return Indice do cliente na lista, ou -1 se sessao nao esta' na lista.
	 */
	private int getIdxBySessao(IoSession session) {
		/* pesquisa na lista de clientes conectados */
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).session == session) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Verifica se um objeto VelhaBean e' valido.
	 * @param velha Objeto VelhaBean
	 * @return True se objeto e' valido, false se nao.
	 */
	private boolean verificarVelhaBean(VelhaBean velha) {

		/* problema de conversao do XML */
		if (velha == null) return false;

		/* verifica se Id do jogador esta' certo no XML */
		if (velha.getId() != clients.get(jogadorDaVez).getId()) {
			return false;
		}

		/* verifica se jogada e' valida */
		int jogada = velha.getJogada();
		if (jogada < 0
			  || velhaEngine.getPosicao(jogada) != VelhaEngine.JOGADOR_VAZIO) {
				return false;
		}
		return true;
	}
	
	/**
	 * Retorna o endereco IP do cliente remoto.
	 * @param client Conexao do cliente
	 * @return Endereco IP do cliente ou vazio se houve errro.
	 */
	private String getClientAddress(VelhaClientConnection client) {
		String address = "";
		if (client != null && client.getSession() != null
				&& client.getSession().getRemoteAddress() != null) {
			address = client.getSession().getRemoteAddress().toString();
		}
		return address;
	}
	
	/**
	 * Em caso de excecao, este metodo e' disparado.
	 * @param session Referencia para objeto que representa a sessao
	 * @param cause Excecao ocorrida
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {

		if (DEBUG_ENABLED) cause.printStackTrace();
		session.close(true);
	}
}
