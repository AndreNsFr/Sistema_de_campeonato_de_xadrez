package model;

import Enums.ResultadoPartida;
import Exeptions.PartidaJaFinalizadaExeption;

import java.time.LocalDateTime;



public class Partida {
    private Jogador jogadorBrancas;
    private Jogador jogadorPretas;
    private ResultadoPartida resultado;
    private LocalDateTime data;
    private boolean finalizada;

    public Jogador getJogadorPretas() {
        return jogadorPretas;
    }

    public Jogador getJogadorBrancas() {
        return jogadorBrancas;
    }

    public boolean getFinalizada(){
        return this.finalizada;
    }

    public Partida(Jogador newBrancas, Jogador NewPretas){
        this.jogadorBrancas = newBrancas;
        this.jogadorPretas = NewPretas;
        this.data = LocalDateTime.now();
        this.resultado = ResultadoPartida.EM_ANDAMENTO;
        this.finalizada = false;
    }

    public void finalizarPartida(ResultadoPartida resultado){
        if(!this.finalizada && resultado != ResultadoPartida.EM_ANDAMENTO){

            if(resultado == ResultadoPartida.PRETAS_VENCERAM){
                this.jogadorPretas.registrarVitoria();
                this.jogadorBrancas.registrarDerrota();
            } else if (resultado == ResultadoPartida.BRANCAS_VENCERAM) {
                this.jogadorPretas.registrarDerrota();
                this.jogadorBrancas.registrarVitoria();
            }else {
                this.jogadorBrancas.registrarEmpate();
                this.jogadorPretas.registrarEmpate();
            }

            this.resultado = resultado;
            this.finalizada = true;
            return;
        }
        throw new PartidaJaFinalizadaExeption("Essa partida não pode ser finalizada");
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
        if(this.resultado == ResultadoPartida.BRANCAS_VENCERAM) {
            return this.jogadorBrancas;
        } else if (this.resultado == ResultadoPartida.PRETAS_VENCERAM) {
            return this.jogadorPretas;
        }
        return null;
    }



}
