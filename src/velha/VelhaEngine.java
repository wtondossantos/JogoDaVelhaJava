package velha;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe que implementa a logica do Jogo da Velha. 
 */
public class VelhaEngine {

	/** Representa o Jogador X. */
	public static final char JOGADOR_X     = 'X';  
	/** Representa o Jogador O. */
	public static final char JOGADOR_O     = 'O';  
	/** Representa um espaco vazio no tabuleiro. */
	public static final char JOGADOR_VAZIO = ' ';  
	
	/** Status: Aguarde a vez de jogar. */  
	public static final String STATUS_AGUARDE = "aguarde"; 
	/** Status: E' a vez de jogar. */  
	public static final String STATUS_JOGUE   = "jogue"  ; 
	/** Status: Jogador ganhou. */  
	public static final String STATUS_GANHOU  = "ganhou" ; 
	/** Status: Jogador perdeu. */  
	public static final String STATUS_PERDEU  = "perdeu" ; 
	/** Status: Houve empate. */  
	public static final String STATUS_EMPATE  = "empate" ; 
	/** Status: Outro jogador abandonou a partida. */  
	public static final String STATUS_WO      = "wo"     ; 
	
	
	/** Posicoes possiveis de formar um trio em linha. */
	private final int TRIOS[][] = {
			{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}
	};
    
	/** Tabuleiro do Jogo da Velha:
	 * [0][1][2]
	 * [3][4][5]
	 * [6][7][8]
	 */
	private char[] tabuleiro = new char[9];
	
	/** Nivel de inteligencia do robot (0 a 10). */
	private int nivel = 10;
	
	/** Construtor default. */
	public VelhaEngine() {
		limparTabuleiro();
	}
	
	/**
	 * Retorna o Nivel de inteligencia do robot.
	 * @return Nivel de inteligencia (0 a 10).
	 */
	public int getNivel() {
		return nivel;
	}

	/**
	 * Configura o Nivel de inteligencia do robot.
	 * @param nivel Nivel de inteligencia (0 a 10).
	 */
	public void setNivel(int nivel) {
		if (nivel >= 0 && nivel <= 10) {
			this.nivel = nivel;
		}
	}

	/**
	 * Retorna qual o oponente do jogador.
	 * @param jogador Jogador a ser retornado seu oponente
	 * @return Oponente do jogador, ou @ref JOGADOR_VAZIO se jogador
	 *   nao e' valido.
	 */
	public static char getOponente(char jogador) {
		switch (jogador) {
			case JOGADOR_X: return JOGADOR_O;
			case JOGADOR_O: return JOGADOR_X;
			default       : return JOGADOR_VAZIO;
		}
	}

	/**
	 * Retorna uma posicao no tabuleiro.
	 * @param pos Posicao (de 0 a 8)
	 * @return Jogador marcado na posicao ou @ref JOGADOR_VAZIO se nao houver. 
	 */
	public char getPosicao(int pos) {
		return tabuleiro[pos];
	}
	
	/**
	 * Altera uma posicao no tabuleiro.
	 * @param pos Posicao (de 0 a 8)
	 * @param jogador Jogador a ser marcado na posicao (@ref JOGADOR_X,
	 *   @ref JOGADOR_O ou @ref JOGADOR_VAZIO)
	 */
	public void setPosicao(int pos, char jogador) {
		if (jogador == JOGADOR_O || jogador == JOGADOR_X) {
			tabuleiro[pos] = jogador;
		}
	}
	
	/** 
	 * Retorna o tabuleiro.
	 * @return Vetor que representa o tabuleiro.
	 */
	public char[] getTabuleiro() {
		return tabuleiro;
	}
	
	/**
	 * Limpa o tabuleiro, marcando todas as posicoes com @ref JOGADOR_VAZIO. 
	 */
	public void limparTabuleiro() {
		for (int pos = 0; pos < tabuleiro.length; pos++) {
			tabuleiro[pos] = JOGADOR_VAZIO;
		}
	}
	
	/**
	 * Copia um vetor para o tabuleiro.
	 * @param from Vetor origem, com a representacao de um tabuleiro
	 *   (9 posicoes).
	 */
	public void copiarTabuleiro(char[] from) {
		if (from != null && from.length == tabuleiro.length) {
			for (int pos = 0; pos < tabuleiro.length; pos++) {
				tabuleiro[pos] = from[pos];
			}
		}
	}
	
	/**
	 * Verifica se o jogo terminou (um jogador ganhou ou deu empate).
	 * @return True se o jogo terminou, false se nao.
	 */
	public boolean isGameOver() {
		return (isCheio() || isGanhador(JOGADOR_X) || isGanhador(JOGADOR_O));
	}
	
	
	/**
	 * Retorna o jogador ganhador.
	 * @return Jogador que ganhou (@ref JOGADOR_X ou @ref JOGADOR_O),
	 *   ou @ref JOGADOR_VAZIO se nenhum jogador ganhou.
	 */
	public char getGanhador() {
		int trio[] = getTrioGanhador();

		if (trio == null || trio.length < 3) {
			return JOGADOR_VAZIO;
		} else {
			return tabuleiro[trio[0]];
		}
	}

	/**
	 * Retorna as tres posicoes das jogadas do ganhador.
	 * @return Vetor com as tres posicoes da jogada ganhadora, ou null se
	 *   nenhum jogador ganhou.
	 */
	public int[] getTrioGanhador() {
		int retorno[] = null;
		int  p[] = new int [3];
		char j[] = new char[3];
		
		for (int pos = 0; pos < TRIOS.length; pos++) {
			for (int p1 = 0; p1 < 3; p1++) {
				p[p1] = TRIOS[pos][p1];
				j[p1] = tabuleiro[p[p1]];
			}

			if (j[0] != JOGADOR_VAZIO && j[0] == j[1] && j[1] == j[2]) {
	        	retorno = new int[3];
				for (int p1 = 0; p1 < 3; p1++) {
					retorno[p1] = p[p1];
				}
	        }
		}
	    return retorno;
	}
	
	/**
	 * Verifica se um jogador ganhou.
	 * @param jogador Jogador a ser verificado (@ref JOGADOR_X ou
	 *   @ref JOGADOR_O)
	 * @return True se o jogador indicado ganhou, false se nao.
	 */
	public boolean isGanhador(char jogador) {
		return (getGanhador() == jogador);
	}
	
	/**
	 * Verifica se houve empate.
	 * @return True se houve empate, false se nao.
	 */
	public boolean isEmpate() {
	    return (isCheio() && getGanhador() == JOGADOR_VAZIO);
	}

	/**
	 * Verifica se o tabuleiro esta' vazio (todas as posicoes marcadas
	 * com @ref JOGADOR_VAZIO).
	 * @return True se tabuleiro esta' vazio, false se nao.
	 */
	public boolean isVazio() {
		return isVazio(0, tabuleiro.length - 1);
	}

	/**
	 * Retorna a proxima jogada para um jogador.
	 * Este metodo contem a "AI" necessaria para um robot de Jogo da Velha.
	 * (Baseado na "Jogada Perfeita" descrita em
	 *   http://pt.wikipedia.org/wiki/Jogo_da_velha#Jogada_perfeita).
	 * @param jogador Jogador a ser verificada a proxima jogada
	 *  (@ref JOGADOR_X ou @ref JOGADOR_O)
	 * @return Posicao no tabuleiro onde deve ser a proxima jogada, ou -1 se
	 *  nao ha' mais posicoes disponiveis no tabuleiro.
	 */
	public int getJogada(char jogador) {
		int jogada = -1;

		/* Jogada Perfeita: passo 1 */
		jogada = getJogadaAtaque(jogador);
		if (jogada >= 0) return jogada;

		/* Jogada Perfeita: passo 2 */
		if (new Random().nextInt(10) >= (10 - nivel)) {
			jogada = getJogadaDefesa(jogador);
			if (jogada >= 0) return jogada;
		}
		
		/* Jogada Perfeita: passo 3 */
		/* 50%, para que o robot nao tente sempre iniciar pelos cantos,
		 * ficando o jogo monotono */
		if (isVazio() && new Random().nextBoolean()) {
			jogada = getJogadaCanto(jogador);
			if (jogada >= 0) return jogada;
		}
		jogada = getJogadaAtaqueTriangulo(jogador);
		if (jogada >= 0) return jogada;

		/* Jogada Perfeita: passo 4 */
		if (new Random().nextInt(10) >= (10 - nivel)) {
			jogada = getJogadaDefesaTriangulo(jogador);
			if (jogada >= 0) return jogada;
		}
		
		/* Jogada Perfeita: passo 5 */
		if (tabuleiro[4] == JOGADOR_VAZIO) {
			jogada = 4; return jogada;
		}
		
		/* Jogada Perfeita: passo 6 */
		jogada = getJogadaCanto(jogador);
		if (jogada >= 0) return jogada;
		
		/* borda: ultima opcao */
		jogada = getJogadaBorda(jogador);
		if (jogada >= 0) return jogada;

		return -1;
	}

	/**
	 * Verifica se o trecho especificado do tabuleiro esta' vazio
	 * 	(as posicoes marcadas com @ref JOGADOR_VAZIO).
	 * @param start Posicao inicial a analisar (0 a 8)
	 * @param end Posicao final a analisar (0 a 8)
	 * @return True se trecho do tabuleiro esta' vazio, false se nao.
	 */
	private boolean isVazio(int start, int end) {
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		
		if (start <  0               ) start = 0;
		if (end   >= tabuleiro.length) end   = tabuleiro.length - 1;
		
		for (int pos = start; pos <= end; pos++) {
			if (tabuleiro[pos] != JOGADOR_VAZIO) return false; 
		}
		return true;
	}
	
	/**
	 * Verifica se o tabuleiro esta' cheio (nenhuma posicao marcada
	 * com @ref JOGADOR_VAZIO).
	 * @return True se tabuleiro esta' cheio, false se nao.
	 */
	private boolean isCheio() {
		for (int pos = 0; pos < tabuleiro.length; pos++) {
			if (tabuleiro[pos] == JOGADOR_VAZIO) return false; 
		}
		return true;
	}
	
	/**
	 * Retorna uma possivel jogada de ataque para o jogador especificado.
	 * A jogada de ataque e' aquela que completa tres posicoes em linha para
	 * que o jogador ganhe o jogo.
	 * @param jogador Jogador a ter a jogada analisada
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 * de ataque possivel.
	 */
	private int getJogadaAtaque(char jogador) {

		List<Integer> jogadasPossiveis = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			if (tabuleiro[i] == JOGADOR_VAZIO) { jogadasPossiveis.add(i); }
		}
		
		List<Integer> boasJogadas =
			getMelhoresJogadas(jogadasPossiveis,
			      new char[]{JOGADOR_VAZIO,jogador,jogador});
		
		if (boasJogadas.size() > 0) { return boasJogadas.get(0); }
		
		return -1;
	}
	
	/**
	 * Retorna uma possivel jogada de defesa para o jogador especificado.
	 * A jogada de defesa e' aquela que bloqueia o oponente de completar
	 * tres posicoes em linha.
	 * @param jogador Jogador a ter a jogada analisada
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 * de defesa possivel.
	 */
	private int getJogadaDefesa(char jogador) {
		return getJogadaAtaque(getOponente(jogador));
	}
	
	/**
	 * Retorna uma possivel jogada de defesa "triangulo" para o jogador
	 * especificado.
	 * A jogada em "triangulo" e' aquela em que o jogador tem duas
	 * possibilidades simultaneas de completar uma linha e ganhar o jogo.
	 * @param jogador Jogador a ter a jogada analisada
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 * de defesa "triangulo" possivel.
	 */
	private int getJogadaDefesaTriangulo(char jogador) {
		if ( (tabuleiro[0] == getOponente(jogador) &&
			  tabuleiro[1] == JOGADOR_VAZIO &&
			  tabuleiro[2] == JOGADOR_VAZIO &&
			  tabuleiro[3] == JOGADOR_VAZIO &&
			  tabuleiro[4] == jogador &&
			  tabuleiro[5] == JOGADOR_VAZIO &&
			  tabuleiro[6] == JOGADOR_VAZIO &&
			  tabuleiro[7] == JOGADOR_VAZIO &&			
			  tabuleiro[8] == getOponente(jogador)) ||
				 
			 (tabuleiro[0] == JOGADOR_VAZIO &&
			  tabuleiro[1] == JOGADOR_VAZIO &&
			  tabuleiro[2] == getOponente(jogador) &&
			  tabuleiro[3] == JOGADOR_VAZIO &&
			  tabuleiro[4] == jogador &&
			  tabuleiro[5] == JOGADOR_VAZIO &&
			  tabuleiro[6] == getOponente(jogador) &&
			  tabuleiro[7] == JOGADOR_VAZIO &&			
			  tabuleiro[8] == JOGADOR_VAZIO) ) {
			
			return getJogadaBorda(jogador);
		}
		
		if (tabuleiro[4] == jogador &&
			tabuleiro[0] == JOGADOR_VAZIO &&
			tabuleiro[2] == JOGADOR_VAZIO &&
			tabuleiro[6] == JOGADOR_VAZIO &&			  
			tabuleiro[8] == JOGADOR_VAZIO) {
			
			if (tabuleiro[1] == getOponente(jogador) &&
				tabuleiro[3] == JOGADOR_VAZIO &&	
				tabuleiro[5] == getOponente(jogador) &&
				tabuleiro[7] == JOGADOR_VAZIO) {
				return 2;
			}
			if (tabuleiro[1] == JOGADOR_VAZIO &&
				tabuleiro[3] == JOGADOR_VAZIO &&	
				tabuleiro[5] == getOponente(jogador) &&
				tabuleiro[7] == getOponente(jogador)) {
				return 8;
			}
			if (tabuleiro[1] == JOGADOR_VAZIO &&
				tabuleiro[3] == getOponente(jogador) &&	
				tabuleiro[5] == JOGADOR_VAZIO &&
				tabuleiro[7] == getOponente(jogador)) {
				return 6;
			}
			if (tabuleiro[1] == getOponente(jogador) &&
				tabuleiro[3] == getOponente(jogador) &&	
				tabuleiro[5] == JOGADOR_VAZIO &&
				tabuleiro[7] == JOGADOR_VAZIO) {
				return 0;
			}
		}
		if (tabuleiro[4] == jogador) {

			if (tabuleiro[0] == getOponente(jogador) &&
				tabuleiro[1] == JOGADOR_VAZIO &&	
				tabuleiro[2] == JOGADOR_VAZIO &&	
				tabuleiro[3] == JOGADOR_VAZIO &&	
				tabuleiro[5] == getOponente(jogador) &&
				tabuleiro[6] == JOGADOR_VAZIO &&
				tabuleiro[7] == JOGADOR_VAZIO &&
				tabuleiro[8] == JOGADOR_VAZIO) {
				return 2;
			}
			if (tabuleiro[0] == JOGADOR_VAZIO &&
				tabuleiro[1] == JOGADOR_VAZIO &&	
				tabuleiro[2] == getOponente(jogador) &&
				tabuleiro[3] == JOGADOR_VAZIO &&	
				tabuleiro[5] == JOGADOR_VAZIO &&
				tabuleiro[6] == JOGADOR_VAZIO &&
				tabuleiro[7] == getOponente(jogador) &&
				tabuleiro[8] == JOGADOR_VAZIO) {
				return 8;
			}
			if (tabuleiro[0] == JOGADOR_VAZIO &&
				tabuleiro[1] == JOGADOR_VAZIO &&	
				tabuleiro[2] == JOGADOR_VAZIO &&
				tabuleiro[3] == getOponente(jogador) &&
				tabuleiro[5] == JOGADOR_VAZIO &&
				tabuleiro[6] == JOGADOR_VAZIO &&
				tabuleiro[7] == JOGADOR_VAZIO &&	
				tabuleiro[8] == getOponente(jogador)) {
				return 6;
			}
			if (tabuleiro[0] == JOGADOR_VAZIO &&
				tabuleiro[1] == getOponente(jogador) &&
				tabuleiro[2] == JOGADOR_VAZIO &&
				tabuleiro[3] == JOGADOR_VAZIO &&	
				tabuleiro[5] == JOGADOR_VAZIO &&
				tabuleiro[6] == getOponente(jogador) &&
				tabuleiro[7] == JOGADOR_VAZIO &&	
				tabuleiro[8] == JOGADOR_VAZIO) {
				return 0;
			}
		}
		
		return -1;
	}
	
	/**
	 * Retorna uma possivel jogada de ataque "triangulo" para o jogador
	 * especificado.
	 * A jogada em "triangulo" e' aquela em que o jogador tem duas
	 * possibilidades simultaneas de completar uma linha e ganhar o jogo.
	 * @param jogador Jogador a ter a jogada analisada
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 * de ataque "triangulo" possivel.
	 */
	private int getJogadaAtaqueTriangulo(char jogador) {
		if (tabuleiro[4] != getOponente(jogador) ||
			tabuleiro[1] != JOGADOR_VAZIO ||
			tabuleiro[3] != JOGADOR_VAZIO ||
			tabuleiro[5] != JOGADOR_VAZIO ||
			tabuleiro[7] != JOGADOR_VAZIO) {
			
			return -1;
		}
		
		if (tabuleiro[0] == jogador && isVazio(1,2) && isVazio(6,8)) {
			return 8;
		}
		
		if (isVazio(0,1) && tabuleiro[2] == jogador && isVazio(6,8)) {
			return 6;
		}
		
		if (isVazio(0,2) && tabuleiro[6] == jogador && isVazio(7,8)) {
			return 2;
		}
				
		if (isVazio(0,2) && isVazio(6,7) && tabuleiro[8] == jogador) {
			return 0;
		}
		return -1;
	}

	/**
	 * Retorna uma possivel jogada no canto para o jogador especificado.
	 * @param jogador Jogador a ter a jogada analisada
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 * no canto possivel.
	 */
	private int getJogadaCanto(char jogador) {
		List<Integer> jogadasPossiveis = new ArrayList<Integer>();
		
		if (tabuleiro[0] == JOGADOR_VAZIO) jogadasPossiveis.add(0);
		if (tabuleiro[2] == JOGADOR_VAZIO) jogadasPossiveis.add(2);
		if (tabuleiro[6] == JOGADOR_VAZIO) jogadasPossiveis.add(6);
		if (tabuleiro[8] == JOGADOR_VAZIO) jogadasPossiveis.add(8);
		
		return escolherMelhorJogada(jogador,jogadasPossiveis);
	}
	
	/**
	 * Retorna uma possivel jogada na borda para o jogador especificado.
	 * @param jogador Jogador a ter a jogada analisada
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 * na borda possivel.
	 */
	private int getJogadaBorda(char jogador) {
		List<Integer> jogadasPossiveis = new ArrayList<Integer>();
		
		if (tabuleiro[1] == JOGADOR_VAZIO) jogadasPossiveis.add(1);
		if (tabuleiro[3] == JOGADOR_VAZIO) jogadasPossiveis.add(3);
		if (tabuleiro[5] == JOGADOR_VAZIO) jogadasPossiveis.add(5);
		if (tabuleiro[7] == JOGADOR_VAZIO) jogadasPossiveis.add(7);
		
		return escolherMelhorJogada(jogador,jogadasPossiveis);
	}

	/**
	 * Retorna uma lista com as melhores jogadas de ataque dentre um conjunto
	 *   especificado de jogadas.
	 * @param jogadasPossiveis Conjunto de jogadas possiveis
	 * @param pattern Padrao de trio [3] de tabuleiro para comparar as
	 *   melhores jogadas
	 * @return Lista com as melhores jogadas de ataque selecionadas.
	 */
	private List<Integer> getMelhoresJogadas(List<Integer> jogadasPossiveis,
											 char[] pattern) {
		
		List<Integer> melhoresJogadas = new ArrayList<Integer>();

		if (pattern.length < 3) { return melhoresJogadas; }

		for (Integer jogada : jogadasPossiveis) {
				
			for (int pos = 0; pos < TRIOS.length; pos++) {
				
				for (int p1 = 0; p1 < 3; p1++) {
					
					if (TRIOS[pos][p1] == jogada) {
					
						int p2 = (p1 == 2) ? 0 : (p1 + 1);
						int p3 = (p2 == 2) ? 0 : (p2 + 1);
					
						if (tabuleiro[TRIOS[pos][p1]] == pattern[0] &&
							tabuleiro[TRIOS[pos][p2]] == pattern[1] &&
							tabuleiro[TRIOS[pos][p3]] == pattern[2]) {
						
							melhoresJogadas.add(TRIOS[pos][p1]);
						}
					}
				}
			}
		}
		return melhoresJogadas;
	}

	/**
	 * Seleciona dentre um conjunto de jogadas de ataque qual e' a melhor.
	 * Caso nao haja uma melhor, sorteia uma. 
	 * @param jogador Jogador a ter a jogada analisada
	 * @param jogadasPossiveis Conjunto de jogadas possiveis
	 * @return Posicao da jogada no tabuleiro, ou -1 se nao ha' uma jogada
	 *   possivel.
	 */
	private int escolherMelhorJogada(char jogador,
			                         List<Integer> jogadasPossiveis) {

		/* nenhuma jogada possivel */
		if (jogadasPossiveis.size() < 1) { return -1; }
		
		/* so' uma jogada possivel */
		if (jogadasPossiveis.size() == 1) { return jogadasPossiveis.get(0); }
		
		/* mais de uma jogada possivel */
		/* verifica quais delas tem chance para formar um trio no futuro */
		List<Integer> boasJogadas = new ArrayList<Integer>();
		
		/* jogadas para formar uma dupla [2] */
		boasJogadas.addAll(getMelhoresJogadas(jogadasPossiveis,
   						      new char[]{JOGADOR_VAZIO,JOGADOR_VAZIO,jogador}));

		boasJogadas.addAll(getMelhoresJogadas(jogadasPossiveis,
		   		              new char[]{JOGADOR_VAZIO,jogador,JOGADOR_VAZIO}));
		
		/* se ha' jogadas, sorteia uma */
		if (boasJogadas.size() > 0) {
			return boasJogadas.get(new Random().nextInt(boasJogadas.size()));
		}
		
		/* jogadas para iniciar um novo trio [1] */
		boasJogadas = getMelhoresJogadas(jogadasPossiveis,
			      new char[]{JOGADOR_VAZIO,JOGADOR_VAZIO,JOGADOR_VAZIO});
	
		/* se ha' jogadas, sorteia uma */
		if (boasJogadas.size() > 0) {
			return boasJogadas.get(new Random().nextInt(boasJogadas.size()));
		}
		
		/* se nao ha' boas jogadas, sorteia qualquer uma das possiveis */
		return
			jogadasPossiveis.get(new Random().nextInt(jogadasPossiveis.size()));
	}
}
