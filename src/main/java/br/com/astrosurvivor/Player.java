package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class Player {

    private Node node;

    private Geometry corpo;
    private Geometry cabine;
    private Geometry asaEsquerda;
    private Geometry asaDireita;
    private Geometry cauda;

    // =========================================
    // MOVIMENTAÇÃO
    // =========================================

    private float velocidade = 5f;

    // =========================================
    // VIDA
    // =========================================

    private int vidaMax = 100;
    private int vidaAtual = vidaMax;

    // =========================================
    // REGENERAÇÃO
    // =========================================

    private int regeneracao = 0;

    // =========================================
    // ESCUDO
    // =========================================

    private int escudos = 0;

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
    // =========================================

    private int nivelMultiDisparo = 0;

    // =========================================
    // CONSTRUTOR
    // =========================================

    public Player(AssetManager assetManager) {

        node = new Node("Player");

        // =========================================
        // CORPO
        // =========================================

        Box formatoCorpo = new Box(
                0.7f,
                0.3f,
                1.8f
        );

        corpo = new Geometry(
                "Corpo",
                formatoCorpo
        );

        Material materialCorpo = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialCorpo.setColor(
                "Color",
                ColorRGBA.Blue
        );

        corpo.setMaterial(materialCorpo);

        node.attachChild(corpo);

        // =========================================
        // CABINE
        // =========================================

        Box formatoCabine = new Box(
                0.5f,
                0.25f,
                0.6f
        );

        cabine = new Geometry(
                "Cabine",
                formatoCabine
        );

        Material materialCabine = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialCabine.setColor(
                "Color",
                ColorRGBA.Green
        );

        cabine.setMaterial(materialCabine);

        node.attachChild(cabine);

        // =========================================
        // ASA ESQUERDA
        // =========================================

        Box formatoAsaEsquerda = new Box(
                0.8f,
                0.1f,
                0.7f
        );

        asaEsquerda = new Geometry(
                "AsaEsquerda",
                formatoAsaEsquerda
        );

        Material materialAsaEsquerda = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialAsaEsquerda.setColor(
                "Color",
                ColorRGBA.Gray
        );

        asaEsquerda.setMaterial(
                materialAsaEsquerda
        );

        asaEsquerda.setLocalTranslation(
                -1.2f,
                0,
                0
        );

        asaEsquerda.rotate(
                0,
                0.3f,
                0
        );

        node.attachChild(
                asaEsquerda
        );

        // =========================================
        // ASA DIREITA
        // =========================================

        Box formatoAsaDireita = new Box(
                0.8f,
                0.1f,
                0.7f
        );

        asaDireita = new Geometry(
                "AsaDireita",
                formatoAsaDireita
        );

        Material materialAsaDireita = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialAsaDireita.setColor(
                "Color",
                ColorRGBA.White
        );

        asaDireita.setMaterial(
                materialAsaDireita
        );

        asaDireita.setLocalTranslation(
                1.2f,
                0,
                0
        );

        asaDireita.rotate(
                0,
                -0.3f,
                0
        );

        node.attachChild(
                asaDireita
        );

        // =========================================
        // CAUDA
        // =========================================

        Box formatoCauda = new Box(
                0.4f,
                0.5f,
                0.4f
        );

        cauda = new Geometry(
                "Cauda",
                formatoCauda
        );

        Material materialCauda = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialCauda.setColor(
                "Color",
                ColorRGBA.Red
        );

        cauda.setMaterial(
                materialCauda
        );

        cauda.setLocalTranslation(
                0,
                0.5f,
                1.2f
        );

        node.attachChild(cauda);
    }

    // =========================================
    // MOVIMENTO
    // =========================================

    public Vector3f mover(
            Vector3f direcao,
            float tpf
    ) {

        Vector3f movimento =
                direcao.mult(
                        velocidade * tpf
                );

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

            System.out.println(
                    "ESCUDO BLOQUEOU O DANO!"
            );

            System.out.println(
                    "Escudos restantes: "
                    + escudos
            );

            return;
        }

        vidaAtual -= dano;

        if (vidaAtual < 0) {
            vidaAtual = 0;
        }

        System.out.println(
                "Vida atual: "
                + vidaAtual
                + "/"
                + vidaMax
        );
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

    public void aumentarVidaMaxima(
            int quantidade
    ) {

        vidaMax += quantidade;

        vidaAtual += quantidade;

        System.out.println(
                "Vida máxima: "
                + vidaMax
        );
    }

    // =========================================
    // REGENERAÇÃO
    // =========================================

    public void aumentarRegeneracao(
            int quantidade
    ) {

        regeneracao += quantidade;

        System.out.println(
                "Regeneração aumentada para: "
                + regeneracao
        );
    }

    public int getRegeneracao() {

        return regeneracao;
    }

    // =========================================
    // ESCUDO
    // =========================================

    public void adicionarEscudo() {

        escudos++;

        System.out.println(
                "Escudo adicionado!"
        );

        System.out.println(
                "Escudos: "
                + escudos
        );
    }

    // =========================================
    // VELOCIDADE
    // =========================================

    public void aumentarVelocidade(
            float quantidade
    ) {

        velocidade += quantidade;

        System.out.println(
                "Velocidade: "
                + velocidade
        );
    }

    // =========================================
    // MULTI-DISPARO
    // =========================================

    public void aumentarNivelMultiDisparo() {

        if (nivelMultiDisparo < 2) {

            nivelMultiDisparo++;

            System.out.println(
                    "Multi-disparo nível: "
                    + nivelMultiDisparo
            );

        } else {

            System.out.println(
                    "Multi-disparo já está no máximo!"
            );
        }
    }

    // =========================================
    // XP
    // =========================================

    public boolean adicionarXP(
            int quantidade
    ) {

        xp += quantidade;

        boolean subiuDeNivel = false;

        while (xp >= getXpNecessario()) {

            xp -= getXpNecessario();

            nivel++;

            subiuDeNivel = true;

            System.out.println(
                    "LEVEL UP!"
            );

            System.out.println(
                    "Novo nível: "
                    + nivel
            );
        }

        return subiuDeNivel;
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

    public void setPosition(
            Vector3f position
    ) {

        node.setLocalTranslation(
                position
        );
    }

    public Vector3f getPosition() {

        return node.getLocalTranslation();
    }

    // =========================================
    // RESET VIDA
    // =========================================

    public void resetarVida() {

        vidaAtual = vidaMax;
    }
}