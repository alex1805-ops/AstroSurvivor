package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import java.util.Random;

public class StarField {

    private Node node;
    private Random random;

    // Tamanho do campo
    private float tamanhoCampo = 150f;

    // Quantidade de estrelas
    private int quantidadeEstrelas = 10000;

    public StarField(AssetManager assetManager){

        node = new Node("StarField");

        random = new Random();

        //======================================================
        // CRIA AS ESTRELAS
        //======================================================

        for(int i = 0; i < quantidadeEstrelas; i++){

            //==================================================
            // FORMATO DA ESTRELA
            //==================================================

            Box formato = new Box(
                0.08f,
                0.08f,
                0.08f
            );

            Geometry estrela = new Geometry(
                "Estrela",
                formato
            );

            //==================================================
            // MATERIAL
            //==================================================

            Material material = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
            );

            material.setColor(
                "Color",
                ColorRGBA.White
            );

            estrela.setMaterial(material);

            //==================================================
            // POSIÇÃO ALEATÓRIA
            //==================================================

            float x =
                random.nextFloat() * tamanhoCampo * 2 - tamanhoCampo;

            float y =
                random.nextFloat() * tamanhoCampo * 2 - tamanhoCampo;

            float z =
                random.nextFloat() * tamanhoCampo * 2 - tamanhoCampo;

            estrela.setLocalTranslation(
                new Vector3f(x, y, z)
            );

            //==================================================
            // ADICIONA AO CAMPO
            //==================================================

            node.attachChild(estrela);
        }
    }

    //==========================================================
    // ACESSO AO NODE
    //==========================================================

    public Node getNode(){
        return node;
    }
}