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

    private int vida = 30;

    private final float velocidade = 2f;

    public Inimigo(AssetManager assetManager, Vector3f posicaoInicial){
        node = new Node("Inimigo");

        Box forma = new Box(0.8f, 0.8f, 0.8f);

        Geometry corpo = new Geometry("CorpoInimigo", forma);

        Material material= new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );

        material.setColor("Color", ColorRGBA.Red);

        corpo.setMaterial(material);

        node.attachChild(corpo);

        node.setLocalTranslation(posicaoInicial);
    }

    public void atualizar(float tpf, Vector3f posicaoJogador){
        Vector3f direcao = posicaoJogador.subtract(node.getLocalTranslation()).normalize();

        node.move(direcao.mult(velocidade * tpf));
    }

    public void receberDano(int dano){
        vida -= dano;

        if(vida < 0){
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
        return node.getLocalTranslation();
    }
}
