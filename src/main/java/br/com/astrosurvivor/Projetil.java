package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Projetil {

    private final Node node;
    private final Vector3f direcao;
    private final float velocidade = 30f;

    // Construtor normal: mantém os tiros amarelos.
    public Projetil(
            AssetManager assetManager,
            Vector3f posicaoInicial,
            Vector3f direcao) {

        this(assetManager, posicaoInicial, direcao, ColorRGBA.Yellow);
    }

    // Novo construtor: permite escolher a cor do projétil.
    public Projetil(
            AssetManager assetManager,
            Vector3f posicaoInicial,
            Vector3f direcao,
            ColorRGBA cor) {

        node = new Node("Projetil");

        Box forma = new Box(0.12f, 0.12f, 0.5f);
        Geometry corpo = new Geometry("CorpoProjetil", forma);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        material.setColor("Color", cor);
        corpo.setMaterial(material);
        node.attachChild(corpo);

        node.setLocalTranslation(posicaoInicial);

        this.direcao = direcao.normalize();

        Quaternion rotacao = new Quaternion();
        rotacao.lookAt(this.direcao, Vector3f.UNIT_Y);
        node.setLocalRotation(rotacao);
    }

    public void atualizar(float tpf) {
        node.move(direcao.mult(velocidade * tpf));
    }

    public Node getNode() {
        return node;
    }

    public Vector3f getPosicao() {
        return node.getWorldTranslation();
    }
}