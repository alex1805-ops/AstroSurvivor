package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class OndaChoque {

    private final Node node = new Node("OndaChoque");
    private final Geometry visual;
    private final Vector3f centro;

    private final float raioMaximo;
    private final float velocidadeExpansao;
    private final int dano;

    private float raioAtual = 0.5f;
    private boolean danoAplicado = false;
    private boolean terminou = false;

    public OndaChoque(
            AssetManager assetManager,
            Vector3f posicao,
            float raioMaximo,
            float velocidadeExpansao,
            int dano) {

        this.centro = posicao.clone();
        this.raioMaximo = raioMaximo;
        this.velocidadeExpansao = velocidadeExpansao;
        this.dano = dano;

        Sphere esfera = new Sphere(24, 24, 1f);
        visual = new Geometry("VisualOndaChoque", esfera);

        Material material = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");

        material.setColor(
                "Color",
                new ColorRGBA(1f, 0.25f, 0.05f, 0.30f));

        material.getAdditionalRenderState()
                .setBlendMode(RenderState.BlendMode.Alpha);

        material.getAdditionalRenderState()
                .setWireframe(true);

        visual.setMaterial(material);
        visual.setQueueBucket(
                com.jme3.renderer.queue.RenderQueue.Bucket.Transparent);

        node.attachChild(visual);
        node.setLocalTranslation(centro);
        atualizarEscala();
    }

    public void atualizar(float tpf, Player player) {
        if (terminou) {
            return;
        }

        raioAtual += velocidadeExpansao * tpf;

        atualizarEscala();

        if (!danoAplicado) {
            float distancia = centro.distance(player.getPosition());

            if (distancia <= raioAtual + 1f) {
                player.receberDano(dano);
                danoAplicado = true;
            }
        }

        if (raioAtual >= raioMaximo) {
            terminou = true;
        }
    }

    private void atualizarEscala() {
        visual.setLocalScale(raioAtual);
    }

    public Node getNode() {
        return node;
    }

    public boolean terminou() {
        return terminou;
    }
}