package model;

import Enums.StatusTorneio;

import java.util.ArrayList;
import java.util.TreeMap;

public class Torneio {
    private  String nome;
    private ArrayList<Jogador> jogadores;
    private ArrayList<Partida> partidas;
    private StatusTorneio statusTorneio;

    public Torneio(String newNomeTorneio){
        this.nome = newNomeTorneio;
        this.jogadores = new ArrayList<>();
        this.partidas = new ArrayList<>();
        this.statusTorneio = StatusTorneio.AGUARDANDO;
    }

    public void adicionarJogador(Jogador jogador){
        if(this.statusTorneio == StatusTorneio.AGUARDANDO){
            this.jogadores.add(jogador);
        }
        throw new RuntimeException("O torneio já começou ou acabou");
    }

    public void registarPartida(Partida partida){
        if(
                partida.getFinalizada() &&
                this.jogadores.contains(partida.getJogadorBrancas()) &&
                this.jogadores.contains(partida.getJogadorPretas())
        ){
            this.partidas.add(partida);
        }
        throw new RuntimeException("Partida inválida para registro");
    }


    public Jogador getCampeao(){
        TreeMap<Integer,Jogador> jogadas = new TreeMap<>();
        for(Jogador jogador : this.jogadores){
            jogadas.put(jogador.getVitorias(),jogador);
        }
        return jogadas.get(jogadas.lastKey());
    }





}
