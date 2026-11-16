package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Boss {
    private final Node node;

    private final TipoBoss tipo;

    private int vida;
    private final int vidaMax;

    private float velocidade;

    private Vector3f direcao;

    public Boss(
            AssetManager assetManager,
            Vector3f posicaoInicial,
            Vector3f alvo,
            TipoBoss tipo) {
        this.tipo = tipo;

        // ======================================================
        // CONFIGURAÇÃO
        // ======================================================
        switch (tipo) {
            case ASTEROIDE_GIGANTE:
                vidaMax = 1000;
                velocidade = 0.7f;
                break;

            case ASTEROIDE_GIGANTE_BLINDADO:
                vidaMax = 5000;
                velocidade = 0.4f;
                break;

            case METEORO_EXPLOSIVO:
                vidaMax = 1500;
                velocidade = 1.0f;
                break;

            case HORDA:
                vidaMax = 1;
                velocidade = 0f;
                break;

            default:
                vidaMax = 1000;
                velocidade = 1f;
                break;
        }
        vida = vidaMax;

        // ======================================================
        // NODE
        // ======================================================
        node = new Node("Boss_" + tipo);

        // ======================================================
        // TAMANHO
        // ======================================================
        float tamanho;

        switch (tipo) {
            case ASTEROIDE_GIGANTE:
                tamanho = 8f;
                break;

            case ASTEROIDE_GIGANTE_BLINDADO:
                tamanho = 8f;
                break;

            case METEORO_EXPLOSIVO:
                tamanho = 6f;
                break;

            case HORDA:
                tamanho = 0.1f;
                break;

            default:
                tamanho = 3f;
                break;
        }

        // ======================================================
        // MODELOS 3D DOS BOSSES
        // ======================================================

        String caminhoModelo = null;
        String caminhoTextura = null;

        switch (tipo) {

            case ASTEROIDE_GIGANTE:
                caminhoModelo = "Models/Asteroides/asteroide_grande.obj";
                caminhoTextura = "Models/Bosses/asteroide_gigante_textura.png";
                break;

            case ASTEROIDE_GIGANTE_BLINDADO:
                caminhoModelo = "Models/Asteroides/asteroide_grande.obj";
                caminhoTextura = "Models/Bosses/asteroide_blindado_textura.png";
                break;

            case METEORO_EXPLOSIVO:
                caminhoModelo = "Models/Asteroides/asteroide_grande.obj";
                caminhoTextura = "Models/Bosses/meteoro_explosivo_textura.png";
                break;

            case HORDA:
                caminhoModelo = "Models/Asteroides/asteroide_pequeno.obj";
                caminhoTextura = "Models/Asteroides/asteroide_pequeno_textura.png";
                break;
        }

        if (caminhoModelo != null) {

            Spatial modelo = assetManager.loadModel(caminhoModelo);

            modelo.setLocalScale(tamanho);

            ModeloAsteroide.aplicarTextura(assetManager, modelo, caminhoTextura);

            node.attachChild(modelo);
        }

        // ======================================================
        // DIREÇÃO
        // ======================================================
        if (alvo != null) {
            direcao = alvo.subtract(posicaoInicial).normalize();
        } else {
            direcao = Vector3f.ZERO.clone();
        }
    }

    // ======================================================
    // UPDATE
    // ======================================================
    public void atualizar(float tpf) {
        if (velocidade <= 0f) {
            return;
        }

        node.move(direcao.mult(velocidade * tpf));
    }

    // ======================================================
    // DANO
    // ======================================================
    public void receberDano(int dano) {
        vida -= dano;

        if (vida < 0) {
            vida = 0;
        }
    }

    // ======================================================
    // ESTÁ VIVO
    // ======================================================
    public boolean estaVivo() {
        return vida > 0;
    }

    // ======================================================
    // GETTETS
    // ======================================================
    public Node getNode() {
        return node;
    }

    public Vector3f getPosicao() {
        return node.getLocalTranslation();
    }

    public TipoBoss getTipo() {
        return tipo;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMax() {
        return vidaMax;
    }
}