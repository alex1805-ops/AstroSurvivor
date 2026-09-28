package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Inimigo {
    private final Node node;

    private final TipoAsteroide tipo;

    private final float velocidade;
    private final Vector3f direcao;

    private int vida;

    public Inimigo(
        AssetManager assetManager,
        Vector3f posicaoInicial,
        Vector3f posicaoAlvo,
        TipoAsteroide tipo
    ){
        this.tipo = tipo;

        //Configurações de cada tipo
        switch (tipo) {
            case PEQUENO:
                vida = 30;
                velocidade = 8f;
                break;
            
            case MEDIO:
                vida = 60;
                velocidade = 4f;
                break;

            case GRANDE:
                vida = 90;
                velocidade = 1f;
                break;
        
            default:
                vida = 30;
                velocidade = 8f;
                break;
        }

        node = new Node("Asteroide_" + tipo);

        //Tamanho dvisual de cada asteroide
        float tamanho;

        switch (tipo) {
            case PEQUENO:
                tamanho = 0.5f;
                break;
            
            case MEDIO:
                tamanho = 1.0f;
                break;

            case GRANDE:
                tamanho = 1.8f;
                break;
        
            default:
                tamanho = 0.5f;
                break;
        }

        Box forma = new Box(tamanho, tamanho, tamanho);

        Geometry corpo = new Geometry("CorpoAsteroide_" + tipo, forma);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        material.setColor("Color", ColorRGBA.Gray);

        corpo.setMaterial(material);

        node.attachChild(corpo);

        node.setLocalTranslation(posicaoInicial);

        Vector3f direcao = posicaoAlvo.subtract(posicaoInicial).normalize();

        this.direcao = direcao;
    }

    public void atualizar(float tpf){
        node.move(direcao.mult(velocidade * tpf));
    }

    public void receberDano(int dano){
        vida -= dano;

        if (vida < 0){
            vida = 0;
        }
    }

    public boolean estaVivo(){
        return vida > 0;
    }

    public Node getNode(){
        return node;
    }

    public Vector3f getPosicao(){
        return node.getWorldTranslation();
    }

    public TipoAsteroide getTipo(){
        return tipo;
    }

    public float getVelocidade(){
        return velocidade;
    }

    public int getVida(){
        return vida;
    }
}