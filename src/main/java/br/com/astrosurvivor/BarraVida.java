package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;

public class BarraVida {

    private Node node;

    private Geometry fundo;
    private Geometry barra;

    private float largura = 300f;
    private float altura = 30f;

    private float posicaoX;
    private float posicaoY;

    public BarraVida(
        AssetManager assetManager,
        float posicaoX,
        float posicaoY
    ) {

        this.posicaoX = posicaoX;
        this.posicaoY = posicaoY;

        node = new Node("BarraVida");

        // ======================================================
        // FUNDO
        // ======================================================

        Mesh meshFundo = criarMesh(largura, 1f);

        fundo = new Geometry(
            "FundoVida",
            meshFundo
        );

        Material materialFundo = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );

        Texture texturaFundo = assetManager.loadTexture(
            "Interface/vida_fundo.png"
        );

        materialFundo.setTexture(
            "ColorMap",
            texturaFundo
        );

        materialFundo.getAdditionalRenderState()
            .setBlendMode(RenderState.BlendMode.Alpha);

        fundo.setMaterial(materialFundo);

        fundo.setLocalTranslation(
            posicaoX,
            posicaoY,
            0
        );

        node.attachChild(fundo);

        // ======================================================
        // BARRA
        // ======================================================

        Mesh meshBarra = criarMesh(largura, 1f);

        barra = new Geometry(
            "BarraVida",
            meshBarra
        );

        Material materialBarra = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );

        Texture texturaBarra = assetManager.loadTexture(
            "Interface/vida.png"
        );

        materialBarra.setTexture(
            "ColorMap",
            texturaBarra
        );

        materialBarra.getAdditionalRenderState()
            .setBlendMode(RenderState.BlendMode.Alpha);

        barra.setMaterial(materialBarra);

        barra.setLocalTranslation(
            posicaoX,
            posicaoY,
            0.1f
        );

        node.attachChild(barra);
    }

    // ======================================================
    // CRIA O QUAD
    // ======================================================

    private Mesh criarMesh(
        float larguraAtual,
        float uvX
    ) {

        Mesh mesh = new Mesh();

        // ======================================================
        // VÉRTICES
        // ======================================================

        float[] vertices = {

            0, 0, 0,

            larguraAtual, 0, 0,

            larguraAtual, altura, 0,

            0, altura, 0
        };

        // ======================================================
        // UV
        // ======================================================

        float[] texCoord = {

            0, 0,

            uvX, 0,

            uvX, 1,

            0, 1
        };

        // ======================================================
        // TRIÂNGULOS
        // ======================================================

        int[] indices = {

            0, 1, 2,

            0, 2, 3
        };

        mesh.setBuffer(
            Type.Position,
            3,
            vertices
        );

        mesh.setBuffer(
            Type.TexCoord,
            2,
            texCoord
        );

        mesh.setBuffer(
            Type.Index,
            3,
            indices
        );

        mesh.updateBound();

        return mesh;
    }

    // ======================================================
    // ATUALIZA VIDA
    // ======================================================

    public void atualizar(
        int vida,
        int vidaMax
    ) {

        float porcentagem =
            vida / (float) vidaMax;

        if (porcentagem < 0f) {
            porcentagem = 0f;
        }

        if (porcentagem > 1f) {
            porcentagem = 1f;
        }

        // ======================================================
        // NOVA LARGURA
        // ======================================================

        float novaLargura =
            largura * porcentagem;

        // ======================================================
        // NOVA MESH
        // ======================================================

        Mesh novaMesh = criarMesh(
            novaLargura,
            porcentagem
        );

        barra.setMesh(novaMesh);

        // ======================================================
        // ESCONDE A BARRA QUANDO CHEGAR A ZERO
        // ======================================================

        if (porcentagem <= 0f) {

            barra.setCullHint(
                Geometry.CullHint.Always
            );

        } else {

            barra.setCullHint(
                Geometry.CullHint.Never
            );
        }
    }

    // ======================================================
    // NODE PRINCIPAL
    // ======================================================

    public Node getNode() {

        return node;
    }
}