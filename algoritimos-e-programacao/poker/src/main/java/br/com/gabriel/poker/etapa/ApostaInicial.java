package br.com.gabriel.poker.etapa;

import br.com.gabriel.poker.Jogo;
import br.com.gabriel.poker.comunicacao.Comunicador;

public class ApostaInicial implements Etapa {

	private static final int APOSTA_BIG_BLIND = 10;
	private Comunicador comunicador;
	private boolean isCompleta;

	public ApostaInicial(Comunicador comunicador) {
		this.comunicador = comunicador;
	}

	@Override
	public boolean isCompelta() {
		return isCompleta;
	}

	@Override
	public void executar(Jogo jogo) {
		var bigBlind = jogo.getBigBlind();
		var smallBlind = jogo.getSmallBlind();

		jogo.apostar(bigBlind, APOSTA_BIG_BLIND);
		jogo.apostar(smallBlind, APOSTA_BIG_BLIND / 2);
		comunicador.comunicar("Apostas iniciais feitas. O pote está com " + jogo.getPote() + " fichas");

		isCompleta = Boolean.TRUE;
	}

	@Override
	public String getNome() {
		return "Apostas iniciais";
	}
}
