package model;

import Enums.ResultadoPartida;
import Exeptions.PartidaJaFinalizadaExeption;

import java.time.LocalDateTime;

import static Enums.ResultadoPartida.BRANCAS_VENCERAM;
import static Enums.ResultadoPartida.PRETAS_VENCERAM;

public class Partida {
    private Jogador jogadorBrancas;
    private Jogador jogadorPretas;
    private ResultadoPartida resultado;
    private LocalDateTime data;
    private boolean finalizada;

    public Partida(Jogador newBrancas, Jogador NewPretas){
        this.jogadorBrancas = newBrancas;
        this.jogadorPretas = NewPretas;
        this.data = LocalDateTime.now();
        this.finalizada = false;
    }

    public void finalizarPartida(ResultadoPartida resultado){
        if(!this.finalizada){
            this.finalizada = true;
        }
        throw new PartidaJaFinalizadaExeption("Essa partida já foi finalizada");
    }

    public void exibirResumo(){
        System.out.println("///////////////////////////////////////////////////");
        System.out.println("jogador brancas:" + this.jogadorBrancas.getNome());
        System.out.println("Jogador pretas:" + this.jogadorPretas.getNome());
        System.out.println("Data da partida:" + this.data);
        System.out.println("Resultado:" + this.resultado.toString());
        System.out.println("Finalizada:"+this.finalizada);
        System.out.println("///////////////////////////////////////////////////");
    }

    public Jogador getVencedor(){
        if(this.resultado == BRANCAS_VENCERAM) {
            return this.jogadorBrancas;
        } else if (this.resultado == PRETAS_VENCERAM) {
            return this.jogadorPretas;
        }
        return null;
    }

}
