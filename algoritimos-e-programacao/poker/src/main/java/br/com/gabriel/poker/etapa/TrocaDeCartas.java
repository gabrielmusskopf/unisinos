package br.com.gabriel.poker.etapa;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import br.com.gabriel.poker.Carta;
import br.com.gabriel.poker.Configuracoes;
import br.com.gabriel.poker.Controle;
import br.com.gabriel.poker.Jogo;
import br.com.gabriel.poker.comunicacao.Comunicador;
import br.com.gabriel.poker.comunicacao.Cor;
import br.com.gabriel.poker.jogador.Jogador;
import br.com.gabriel.poker.util.Aleatorio;

public class TrocaDeCartas implements Etapa {

	private static final String NAO_NUMEROS_REXEG = "[^\\d]";
	private final Comunicador comunicador;
	private boolean isCompleta;

	public TrocaDeCartas (Comunicador comunicador) {
		this.comunicador = comunicador;
	}

	@Override
	public boolean isCompelta () {
		return isCompleta;
	}

	@Override
	public void executar (Jogo jogo) {
		if (!jogo.isDeveExecutarRodadas()) return;

		for (int i = Jogo.POSICAO_BIG_BLIND; i < jogo.getJogadoresRestantes().size(); i++) {
			var jogador = jogo.getJogadoresRestantes().get(i);
			trocarCartas(jogo, jogador);
		}

		var dealer = jogo.getDealer();
		trocarCartas(jogo, dealer);

		isCompleta = Boolean.TRUE;
	}

	private void trocarCartas (Jogo jogo, Jogador jogador) {
		List<Carta> cartasParaTrocar;

		if (Controle.isPlayer(jogador)) {
			cartasParaTrocar = buscarQuantidadeTrocadaPeloJogador(jogador);
		} else {
			var quantidadeTrocada = Aleatorio.buscarEntre(0, Jogo.QUANTIDADE_CARTAS_INICIAIS);
			comunicador.comunicar("{0} irá trocar {1} {2}", jogador.getNome(), quantidadeTrocada, (quantidadeTrocada == 1 ? " carta." : " cartas."));
			cartasParaTrocar = IntStream.range(0, quantidadeTrocada).unordered()
					.mapToObj(i -> jogador.getCartas().get(i))
					.toList();
		}

		if (cartasParaTrocar.isEmpty())
			return;

		var cartasNovas = jogo.getBaralho().comprar(cartasParaTrocar.size());
		jogador.trocarCartas(cartasParaTrocar, cartasNovas);

		if (Controle.isPlayer(jogador))
			comunicador.comunicar(Cor.AZUL, jogador.visualizarCartas() + "\n");
	}

	private List<Carta> buscarQuantidadeTrocadaPeloJogador (Jogador jogador) {

		final var cartas = Configuracoes.SCANNER.nextLine()
				.trim()
				.split(" ");

		if (cartas[0].equals(""))
			return Collections.emptyList();

		return Arrays.stream(cartas)
				.map(numero -> numero.replaceAll(NAO_NUMEROS_REXEG, ""))
				.filter(numero -> !numero.equals(""))
				.map(Integer::valueOf)
				.map(numero -> jogador.getCartas().stream()
						.filter(carta -> carta.getNumero().equals(numero))
						.toList())
				.flatMap(Collection::stream)
				.toList();

	}

	@Override
	public String getNome () {
		return "Troca de cartas";
	}

}
