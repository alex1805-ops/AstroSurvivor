
package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Projetil {

    private final Node node;
    private final Vector3f direcao;
    private final float velocidade = 30f;

    // Disparo normal: plasma azul.
    public Projetil(
            AssetManager assetManager,
            Vector3f posicaoInicial,
            Vector3f direcao) {

        this(
            assetManager,
            posicaoInicial,
            direcao,
            "Models/Disparos/disparo_plasma.obj",
            new ColorRGBA(0.15f, 0.65f, 1f, 1f)
        );
    }

    // Construtor com modelo e cor personalizados.
    public Projetil(
            AssetManager assetManager,
            Vector3f posicaoInicial,
            Vector3f direcao,
            String caminhoModelo,
            ColorRGBA cor) {

        node = new Node("Projetil");

        Spatial modelo = assetManager.loadModel(caminhoModelo);

        modelo.setLocalScale(0.55f);

        Material material = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );

        material.setColor("Color", cor);

        aplicarMaterial(modelo, material);

        node.attachChild(modelo);

        node.setLocalTranslation(posicaoInicial);

        this.direcao = direcao.normalize();

        Quaternion rotacao = new Quaternion();
        rotacao.lookAt(this.direcao, Vector3f.UNIT_Y);

        node.setLocalRotation(rotacao);
    }

    private void aplicarMaterial(Spatial spatial, Material material) {

        if (spatial instanceof Geometry) {
            ((Geometry) spatial).setMaterial(material);

        } else if (spatial instanceof Node) {

            Node grupo = (Node) spatial;

            for (Spatial filho : grupo.getChildren()) {
                aplicarMaterial(filho, material);
            }
        }
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