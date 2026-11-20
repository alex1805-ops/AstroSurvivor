package br.com.astrosurvivor;

import java.util.Random;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class StarField {

    private Node node;
    private Random random;

    // Tamanho do campo
    private float tamanhoCampo = 200f;

    // Quantidade de estrelas
    private int quantidadeEstrelas = 11250;

    // Materiais compartilhados
    private Material materialBranco;
    private Material materialAzulado;
    private Material materialAlaranjado;
    private Material materialDourado;

    public StarField(AssetManager assetManager) {

        node = new Node("StarField");
        random = new Random();

        // Materiais das estrelas
        materialBranco = criarMaterial(assetManager,
                new ColorRGBA(1f, 1f, 1f, 1f));

        materialAzulado = criarMaterial(assetManager,
                new ColorRGBA(0.59f, 0.78f, 1f, 1f));

        materialAlaranjado = criarMaterial(assetManager,
                new ColorRGBA(1f, 0.55f, 0.25f, 1f));

        materialDourado = criarMaterial(assetManager,
                new ColorRGBA(1f, 0.82f, 0.42f, 1f));

        // Carrega o modelo somente uma vez
        Spatial modeloBase = assetManager.loadModel(
                "Models/Estrelas/estrela.obj");

        // Cria as estrelas
        for (int i = 0; i < quantidadeEstrelas; i++) {

            // Clona a geometria, compartilhando a malha
            Spatial estrela = modeloBase.clone(false);

            // Escolhe a cor
            Material materialEscolhido = escolherMaterial();

            aplicarMaterial(estrela, materialEscolhido);

            // Tamanho aleatório
            float sorteioTamanho = random.nextFloat();

            float escala;

            if (sorteioTamanho < 0.80f) {
                // Estrelas pequenas: maioria
                escala = 0.10f + random.nextFloat() * 0.10f;

            } else if (sorteioTamanho < 0.96f) {
                // Estrelas médias
                escala = 0.21f + random.nextFloat() * 0.15f;

            } else {
                // Estrelas grandes: raras
                escala = 0.35f + random.nextFloat() * 0.25f;
            }

            estrela.setLocalScale(escala);

            if (escala >= 0.30f) {
                // Estrelas grandes com cores mais intensas
                estrela.setLocalScale(escala * 1.3f);
            }

            // Posição aleatória
            float x = random.nextFloat() * tamanhoCampo * 2
                    - tamanhoCampo;

            float y = random.nextFloat() * tamanhoCampo * 2
                    - tamanhoCampo;

            float z = random.nextFloat() * tamanhoCampo * 2
                    - tamanhoCampo;

            estrela.setLocalTranslation(x, y, z);

            // Adiciona ao campo
            node.attachChild(estrela);
        }
    }

    private Material criarMaterial(
            AssetManager assetManager,
            ColorRGBA cor) {

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        material.setColor("Color", cor);

        return material;
    }

    private Material escolherMaterial() {

        float sorteio = random.nextFloat();

        if (sorteio < 0.55f) {
            return materialBranco;
        }

        if (sorteio < 0.75f) {
            return materialAzulado;
        }

        if (sorteio < 0.93f) {
            return materialAlaranjado;
        }

        return materialDourado;
    }

    private void aplicarMaterial(
            Spatial spatial,
            Material material) {

        if (spatial instanceof Geometry) {

            ((Geometry) spatial).setMaterial(material);

        } else if (spatial instanceof Node) {

            Node grupo = (Node) spatial;

            for (Spatial filho : grupo.getChildren()) {
                aplicarMaterial(filho, material);
            }
        }
    }

    public Node getNode() {
        return node;
    }
}