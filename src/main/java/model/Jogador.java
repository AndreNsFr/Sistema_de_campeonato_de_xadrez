package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class Jogador {
    private UUID id;
    private String nome;
    private int vitorias;
    private int derrotas;
    private int empates;
    private int pontuacao;
    // vai ser posto a lista de partidas depois
    ArrayList<Partida> historico;

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getVitorias() {
        return vitorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getEmpates() {
        return empates;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public ArrayList<Partida> getHistorico() {
        return historico;
    }

    public Jogador(String newName){
        this.nome = newName;
        this.id = UUID.randomUUID();
        this.vitorias = 0;
        this.empates = 0;
        this.pontuacao = 200;
        this.historico = new ArrayList<>();
    }

    public void registrarVitoria(){
        this.vitorias++;
        //não sei como funciona o esquema de pontuação
        this.pontuacao += 100;
    };

    public void registrarDerrota(){
        this.derrotas++;
        //não sei como funciona o esquema de pontuação
        if((this.pontuacao - 100) > 0){
            this.pontuacao -= 100;
        }else{
            this.pontuacao = 0;
        };

    }

    // METODO AINDA NÃO FEITO, PRECISA-SE DA CLASSE "PARTIDA PARA FUNCIOANR"
    public void adicionarPartida(Partida partida){
        this.historico.add(partida);
    }

    public double getTaxaVitoria(){
        return  (double) (this.vitorias + this.derrotas + this.empates) / 10;
    }

    public int getWinStreak(){
        Collections.reverse(this.historico);
        int count = 0;
        for (Partida partida : historico){
            if(partida.getVencedor().id == this.id){
                count++;
            }else{
                return count;
            }
        }
        return 0;
    }

    public void debug(){
        System.out.println("Id:" + this.id);
        System.out.println("Nome:" + this.nome);
        System.out.println("Pontuação:" + this.pontuacao);
        System.out.println("Vitórias:" + this.vitorias);
        System.out.println("Derrotas:" + this.derrotas);
        System.out.println("Empates:" + this.empates);

        for(Object i : this.historico){
            System.out.println("é para parecer aqui as partida");
        }
    }
}
