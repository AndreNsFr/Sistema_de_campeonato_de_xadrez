import Enums.ResultadoPartida;
import model.Jogador;
import model.Partida;
import model.Torneio;

public class Main {
    public static void main(String[] args) {
        Jogador jogador_branca = new Jogador("André");
        Jogador jogador_preta = new Jogador("Mateus");

        Partida partida = new Partida(jogador_branca,jogador_preta);

        jogador_preta.adicionarPartida(partida);
        jogador_branca.adicionarPartida(partida);

        partida.finalizarPartida(ResultadoPartida.BRANCAS_VENCERAM);

        Torneio torneio = new Torneio("Torneio do balacobaco");
        torneio.adicionarJogador(jogador_preta);
        torneio.adicionarJogador(jogador_branca);
        torneio.registarPartida(partida);
        torneio.getCampeao().resumo();
        torneio.encerrar();


    }
}
