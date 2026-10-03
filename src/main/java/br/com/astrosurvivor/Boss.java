package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

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
        TipoBoss tipo
    ) {
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

        Box forma = new Box(tamanho, tamanho, tamanho);

        Geometry corpo = new Geometry("CorpoBoss", forma);

        Material material = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );

        material.setColor("Color", cor);

        corpo.setMaterial(material);

        node.attachChild(corpo);

        node.setLocalTranslation(posicaoInicial);

        // ======================================================
        // DIREÇÃO
        // ======================================================
        if(alvo != null) {
            direcao = alvo.subtract(posicaoInicial).normalize();
        } else {
            direcao = Vector3f.ZERO.clone();
        }
    }

    // ======================================================
    // UPDATE
    // ======================================================
    public void atualizar(float tpf) {
        if ( velocidade <= 0f) {
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