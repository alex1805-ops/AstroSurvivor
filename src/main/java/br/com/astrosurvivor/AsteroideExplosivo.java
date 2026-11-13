package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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
            boolean boss) {
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
        // ======================================================
        // MODELOS 3D DOS ASTEROIDES EXPLOSIVOS
        // ======================================================

        float tamanho = boss ? 3.5f : 0.5f;

        String caminhoModelo;

        if (boss) {

            caminhoModelo = "Models/Bosses/asteroide_explosivo_gigante.obj";

        } else {

            caminhoModelo = "Models/Bosses/asteroide_explosivo_pequeno.obj";
        }

        Spatial modelo = assetManager.loadModel(caminhoModelo);

        modelo.setLocalScale(tamanho);

        node.attachChild(modelo);

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

    public int getVidaMax() {
        return boss ? 1500 : 30;
    }
}