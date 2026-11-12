
package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Inimigo {

    private final Node node;
    private final TipoAsteroide tipo;

    private final float velocidade;
    private Vector3f direcao;

    private int vida;

    public Inimigo(
            AssetManager assetManager,
            Vector3f posicaoInicial,
            Vector3f posicaoAlvo,
            TipoAsteroide tipo) {

        this.tipo = tipo;

        String caminhoModelo;
        float tamanho;

        switch (tipo) {

            case PEQUENO:
                vida = 30;
                velocidade = 8f;
                tamanho = 1.0f;
                caminhoModelo = "Models/Asteroides/asteroide_pequeno.obj";
                break;

            case MEDIO:
                vida = 60;
                velocidade = 4f;
                tamanho = 2.0f;
                caminhoModelo = "Models/Asteroides/asteroide_medio.obj";
                break;

            case GRANDE:
                vida = 90;
                velocidade = 1f;
                tamanho = 2.5f;
                caminhoModelo = "Models/Asteroides/asteroide_grande.obj";
                break;

            default:
                throw new IllegalArgumentException("Tipo de asteroide inválido: " + tipo);
        }

        node = new Node("Asteroide_" + tipo);

        // Carrega o modelo 3D correspondente.
        Spatial modelo = assetManager.loadModel(caminhoModelo);

        modelo.setLocalScale(tamanho);

        node.attachChild(modelo);

        node.setLocalTranslation(posicaoInicial);

        // Define a direção até o alvo.
        this.direcao = posicaoAlvo.subtract(posicaoInicial).normalize();
    }

    public void atualizar(float tpf) {
        node.move(direcao.mult(velocidade * tpf));
    }

    public void receberDano(int dano) {
        vida -= dano;

        if (vida < 0) {
            vida = 0;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public Node getNode() {
        return node;
    }

    public Vector3f getPosicao() {
        return node.getWorldTranslation();
    }

    public TipoAsteroide getTipo() {
        return tipo;
    }

    public float getVelocidade() {
        return velocidade;
    }

    public int getVida() {
        return vida;
    }

    public Vector3f getDirecao() {
        return direcao.clone();
    }

    public void setDirecao(Vector3f novaDirecao) {
        if (novaDirecao != null && novaDirecao.lengthSquared() > 0.0001f) {
            direcao = novaDirecao.normalize();
        }
    }
}