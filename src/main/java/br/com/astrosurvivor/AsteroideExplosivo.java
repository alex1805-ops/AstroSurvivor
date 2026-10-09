package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class AsteroideExplosivo {
    private final Node node;

    private final Vector3f direcao;

    private final boolean boss;

    private int vida;

    private final float velocidade;

    private final float raioExplosao;

    private final int danoExplosao;

    public AsteroideExplosivo(
        AssetManager assetManager,
        Vector3f posicaoInicial,
        Vector3f alvo,
        boolean boss
    ) {
        this.boss = boss;

        if (boss) {
            vida = 1500;
            velocidade = 1f;

            raioExplosao = 18f;
            danoExplosao = 60;

        } else {

            vida = 30;
            velocidade = 8f;
            raioExplosao = 5f;
            danoExplosao = 20;
        }

        node = new Node(boss ? "ExplosivoBoss" : "ExplosivoPequeno");

        float tamanho = boss ? 3.5f : 0.5f;

        Box forma = new Box(tamanho, tamanho, tamanho);

        Geometry corpo = new Geometry("Explosivo", forma);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        material.setColor("Color", boss ? ColorRGBA.Red : ColorRGBA.Orange);

        corpo.setMaterial(material);

        node.attachChild(corpo);

        node.setLocalTranslation(posicaoInicial);

        direcao = alvo.subtract(posicaoInicial).normalize();
    }
    // ======================================================
    // UPDATE
    // ======================================================
    public void atualizar(float tpf) {
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
    // GETTERS
    // ======================================================
    public boolean estaVivo() {
        return vida > 0;
    }

    public Node getNode() {
        return node;
    }

    public Vector3f getPosicao() {
        return node.getLocalTranslation();
    }

    public float getRaioExplosao() {
        return raioExplosao;
    }

    public int getDanoExplosao() {
        return danoExplosao;
    }

    public boolean isBoss() {
        return boss;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return boss ? 1500 : 30;
    }
}