package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
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
                tamanho = 4f;
                break;

            case ASTEROIDE_GIGANTE_BLINDADO:
                tamanho = 5f;
                break;

            case METEORO_EXPLOSIVO:
                tamanho = 3.5f;
                break;

            case HORDA:
                tamanho = 0.1f;
                break;

            default:
                tamanho = 3f;
                break;
        }

        // ======================================================
        // COR
        // ======================================================
        ColorRGBA cor;

        switch (tipo) {
            case ASTEROIDE_GIGANTE:
                cor = ColorRGBA.Gray;
                break;

            case ASTEROIDE_GIGANTE_BLINDADO:
                cor = ColorRGBA.DarkGray;
                break;

            case METEORO_EXPLOSIVO:
                cor = ColorRGBA.Red;
                break;

            case HORDA:
                cor = ColorRGBA.Green;
                break;

            default:
                cor = ColorRGBA.Gray;
                break;
        }

        // ======================================================
        // MODELOS 3D DOS BOSSES
        // ======================================================

        String caminhoModelo = null;

        switch (tipo) {

            case ASTEROIDE_GIGANTE:
                caminhoModelo = "Models/Bosses/asteroide_gigante.obj";
                break;

            case ASTEROIDE_GIGANTE_BLINDADO:
                caminhoModelo = "Models/Bosses/asteroide_blindado.obj";
                break;

            case METEORO_EXPLOSIVO:
                caminhoModelo = "Models/Bosses/asteroide_explosivo_gigante.obj";
                break;
            
            case HORDA:
                caminhoModelo = "Models/Asteroides/asteroide_pequeno.obj";
                break;
        }

        if (caminhoModelo != null) {

            Spatial modelo = assetManager.loadModel(caminhoModelo);

            modelo.setLocalScale(tamanho);

            node.attachChild(modelo);
        }

        node.setLocalTranslation(posicaoInicial);

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