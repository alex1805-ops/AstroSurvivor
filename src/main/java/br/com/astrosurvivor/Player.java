package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;

public class Player {

        private Node node;

        private Spatial nave;

        // =========================================
        // MOVIMENTAÇÃO
        // =========================================

        private float velocidade = 5f;

        private int nivelVelocidade = 0;

        // =========================================
        // VIDA
        // =========================================

        private int vidaMax = 100;
        private int vidaAtual = vidaMax;

        private int nivelVidaMaxima = 0;

        // =========================================
        // REGENERAÇÃO
        // =========================================

        private int nivelRegeneracao = 0;
        private float tempoRegeneracao = 0f;

        // =========================================
        // ESCUDO
        // =========================================

        private int escudos = 0;
        private int nivelEscudo = 0;
        private float tempoEscudo = 0f;

        // =========================================
        // XP
        // =========================================

        private int nivel = 1;
        private int xp = 0;

        // =========================================
        // MULTI-DISPARO
        //
        // 0 = frente
        // 1 = frente + trás
        // 2 = frente + trás + esquerda + direita
        // 3 = niveis anteriores com 2 tiros para frente
        // =========================================

        private int nivelMultiDisparo = 0;

        // =========================================
        // CONSTRUTOR
        // =========================================

        public Player(AssetManager assetManager) {

                node = new Node("Player");

                // Carrega o modelo 3D da nave
                nave = assetManager.loadModel("Models/AstroSurvivor/astro_survivor_ship.obj");

                nave.rotate(0, (float) Math.PI, 0);
                // Ajusta o tamanho da nave
                nave.scale(0.8f);

                nave.depthFirstTraversal(spatial -> {
                        if(spatial instanceof Geometry) {
                                Geometry geometria = (Geometry) spatial;

                                geometria.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
                        }
                });

                //adiciona a nave ao jogador
                node.attachChild(nave);
        }

        // =========================================
        // MOVIMENTO
        // =========================================

        public Vector3f mover(
                        Vector3f direcao,
                        float tpf) {

                Vector3f movimento = direcao.mult(
                                velocidade * tpf);

                node.move(movimento);

                return movimento;
        }

        // =========================================
        // DANO
        // =========================================

        public void receberDano(int dano) {

                if (!estaVivo()) {
                        return;
                }

                // ESCUDO
                if (escudos > 0) {

                        escudos--;

                        System.out.println("ESCUDO BLOQUEOU O DANO!");

                        System.out.println("Escudos restantes: " + escudos);
                        return;
                }

                vidaAtual -= dano;

                if (vidaAtual < 0) {
                        vidaAtual = 0;
                }

                System.out.println("Vida atual: " + vidaAtual + "/" + vidaMax);
        }

        // =========================================
        // CURA
        // =========================================

        public void curar(int quantidade) {

                vidaAtual += quantidade;

                if (vidaAtual > vidaMax) {
                        vidaAtual = vidaMax;
                }
        }

        // =========================================
        // VIDA MÁXIMA
        // =========================================

        public void aumentarVidaMaxima(int quantidade) {
                if (nivelVidaMaxima >= 5) {
                        System.out.println("VIDA MÁXIMA JÁ ESTÁ NO NÍVEL MÁXIMO!");
                        return;
                }

                nivelVidaMaxima++;

                vidaMax += quantidade;

                curar(quantidade);

                System.out.println("VIDA MÁXIMA NíVEL " + nivelVidaMaxima);

                System.out.println("Vida máxima: " + vidaMax);
                System.out.println("Vida atual: " + vidaAtual);
        }

        // =========================================
        // REGENERAÇÃO
        // =========================================

        public void aumentarRegeneracao() {

                if (nivelRegeneracao >= 4) {
                        System.out.println("REGENERAÇÃO JÁ ESTÁ NO NÍVEL MÁXIMO!");
                        return;
                }
                nivelRegeneracao++;

                // CURA IMEDIATA DE 30
                curar(30);

                // REINICIA O CONTADOR DO NOVO NÍVEL
                tempoRegeneracao = 0f;

                System.out.println("Regeneração nível " + nivelRegeneracao + " ativada!");

                System.out.println("+30 de vida!");
        }

        public void atualizarRegeneração(float tpf) {
                if (nivelRegeneracao <= 0) {
                        return;
                }

                if (vidaAtual >= vidaMax) {
                        tempoRegeneracao = 0f;
                        return;
                }

                tempoRegeneracao += tpf;

                float intervalo;

                switch (nivelRegeneracao) {
                        case 1:
                                intervalo = 60f;
                                break;

                        case 2:
                                intervalo = 45f;
                                break;

                        case 3:
                                intervalo = 30f;
                                break;

                        case 4:
                                intervalo = 15f;
                                break;

                        default:
                                return;
                }

                if (tempoRegeneracao >= intervalo) {
                        curar(10);
                        tempoRegeneracao = 0f;

                        System.out.println("+10 DE VIDA POR REGENERAÇÃO!");
                }
        }

        // =========================================
        // ESCUDO
        // =========================================

        public void adicionarEscudo() {
                if(nivelEscudo >= 4){
                        System.out.println("ESCUDO JÁ ESTÁ NO NíVEL MÁXIMO!");
                        return;
                }

                nivelEscudo++;

                int escudosGanhos;

                switch (nivelEscudo) {
                        case 1:
                                escudosGanhos = 3;
                                break;
                        
                        case 2:
                                escudosGanhos = 3;
                                break;
                        
                        case 3:
                                escudosGanhos = 3;
                                break;

                        case 4:
                                escudosGanhos = 3;
                                break;

                        default:
                                return;
                }

                escudos += escudosGanhos;

                tempoEscudo = 0f;

                System.out.println("ESCUDO NÍVEL " + nivelEscudo);

                System.out.println("+" + escudosGanhos + " ESCUDOS!");

                System.out.println("Escudos atuais: " + escudos);
        }

        // =========================================
        // VELOCIDADE
        // =========================================

        public void aumentarVelocidade(float quantidade) {

                if(nivelVelocidade >= 5) {
                        System.out.println("VELOCIDADE JÁ ESTÁ NO NÍVEL MÁXIMO!");
                        return;
                }

                velocidade += quantidade;

                System.out.println("Velocidade: " + velocidade);
        }

        // =========================================
        // MULTI-DISPARO
        // =========================================

        public void aumentarNivelMultiDisparo() {

                if (nivelMultiDisparo < 4) {

                        nivelMultiDisparo++;

                        System.out.println(
                                        "Multi-disparo nível: "
                                                        + nivelMultiDisparo);

                } else {

                        System.out.println(
                                        "Multi-disparo já está no máximo!");
                }
        }

        // =========================================
        // XP
        // =========================================

        public boolean adicionarXP(
                        int quantidade) {

                xp += quantidade;

                boolean subiuDeNivel = false;

                while (xp >= getXpNecessario()) {

                        xp -= getXpNecessario();

                        nivel++;

                        subiuDeNivel = true;

                        System.out.println(
                                        "LEVEL UP!");

                        System.out.println(
                                        "Novo nível: "
                                                        + nivel);
                }

                return subiuDeNivel;
        }

        // =========================================
        // AUMENTO DE ESCUDO AUTOMATICO
        // =========================================
        public void atualizarEscudo(float tpf){
                if(nivelEscudo <= 0){
                        return;
                }

                tempoEscudo += tpf;

                float intervalo;

                switch (nivelEscudo) {
                        case 1:
                                intervalo = 60f; 
                                break;
                
                        case 2:
                                intervalo = 45f;
                                break;

                        case 3:
                                intervalo = 30f;
                                break;

                        case 4:
                                intervalo = 15f;
                                break;

                        default:
                                return;
                }

                if(tempoEscudo >= intervalo){
                        escudos++;

                        tempoEscudo = 0f;

                        System.out.println("+1 ESCUDO!");

                        System.out.println("Escudos atuais: " + escudos);
                }
        }

        // =========================================
        // XP NECESSÁRIO
        // =========================================

        public int getXpNecessario() {

                return 100 + (nivel - 1) * 50;
        }

        // =========================================
        // GETTERS
        // =========================================

        public int getVida() {
                return vidaAtual;
        }

        public int getVidaMax() {
                return vidaMax;
        }

        public boolean estaVivo() {
                return vidaAtual > 0;
        }

        public int getEscudos() {
                return escudos;
        }

        public int getNivel() {
                return nivel;
        }

        public int getXp() {
                return xp;
        }

        public float getVelocidade() {
                return velocidade;
        }

        public int getNivelMultiDisparo() {
                return nivelMultiDisparo;
        }

        public Node getNode() {
                return node;
        }

        public void setPosition(Vector3f position) {
                node.setLocalTranslation(position);
        }

        public Vector3f getPosition() {
                return node.getLocalTranslation();
        }

        public int getNivelVidaMaxima() {
                return nivelVidaMaxima;
        }

        public int getNivelVelocidade() {
                return nivelVelocidade;
        }

        public int getNivelEscudo() {
                return nivelEscudo;
        }

        public int getNivelRegeneracao() {
                return nivelRegeneracao;
        }

        // =========================================
        // RESET VIDA
        // =========================================

        public void resetarVida() {

                vidaAtual = vidaMax;
        }

        public void resetarProgresso() {

                // Vida
                vidaMax = 100;
                vidaAtual = vidaMax;
                nivelVidaMaxima = 0;

                // Regeneração
                nivelRegeneracao = 0;
                tempoRegeneracao = 0f;

                // Escudos
                escudos = 0;
                nivelEscudo = 0;
                tempoEscudo = 0f;

                // XP e nível
                nivel = 1;
                xp = 0;

                // Velocidade
                velocidade = 5f;
                nivelVelocidade = 0;

                // Multi-disparo
                nivelMultiDisparo = 0;

                System.out.println("PROGRESSO DO JOGADOR RESETADO!");
        }
}