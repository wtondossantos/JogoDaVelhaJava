package velha;

/**
 * Classe Bean (POJO) que representa um elemento JogoDaVelha do XML. 
 */
public class VelhaBean{

	/** Id do jogador. */
	private char id;
	/** Status do jogo. */
	private String status;
	/** Posicao da jogada (0..8) ou -1 para nenhuma. */
	private int jogada;
	/** Estado do tabuleiro (char[0]..char[8]). */
	private char[] tabuleiro = new char[9];
	
	/** Construtor default. */
	public VelhaBean() {
		limpar();
	}
	
	/** Limpa todos atributos. */
	public void limpar() {
		id = VelhaEngine.JOGADOR_VAZIO;
		status = VelhaEngine.STATUS_AGUARDE;
		jogada = -1;
		for (int pos = 0; pos < tabuleiro.length; pos++) {
			tabuleiro[pos] = VelhaEngine.JOGADOR_VAZIO;
		}
	}

	/**
	 * Retorna uma posicao no tabuleiro.
	 * @param pos Posicao (de 0 a 8)
	 * @return Jogador marcado na posicao ou 0 se nao houver. 
	 */
	public char getPosicao(int pos) {
		return tabuleiro[pos];
	}

	/**
	 * Altera uma posicao no tabuleiro.
	 * @param pos Posicao (de 0 a 8)
	 * @param jogador Jogador a ser marcado na posicao ('X'|'O') ou
	 *   0 para vazio
	 */
	public void setPosicao(int pos, char jogador) {
		tabuleiro[pos] = jogador;
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
	 * Retorna o tabuleiro.
	 * @return Vetor que representa o tabuleiro.
	 */
	public char[] getTabuleiro() {
		return tabuleiro;
	}

	/**
	 * Retorna o Id do jogador.
	 * @return Id do jogador.
	 */
	public char getId() {
		return id;
	}

	/**
	 * Altera o Id do jogador.
	 * @param id Id do jogador.
	 */
	public void setId(char id) {
		this.id = id;
	}

	/**
	 * Retorna o Status do jogo.
	 * @return Status do jogo.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Altera o Status do jogo.
	 * @param status Status do jogo.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Retorna a posicao da nova jogada.
	 * @return Posicao da jogada.
	 */
	public int getJogada() {
		return jogada;
	}

	/**
	 * Altera a posicao da nova jogada.
	 * @param jogada Posicao da jogada.
	 */
	public void setJogada(int jogada) {
		this.jogada = jogada;
	}

}
