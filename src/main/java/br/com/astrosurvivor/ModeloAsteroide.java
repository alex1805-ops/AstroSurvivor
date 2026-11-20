package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class ModeloAsteroide {
    
    public static void aplicarTextura(AssetManager assetManager, Spatial modelo, String caminhoTextura) {

        Texture textura = assetManager.loadTexture(caminhoTextura);
        textura.setWrap(Texture.WrapMode.Repeat);

        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", textura);
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Ambient", new ColorRGBA(0.35f, 0.35f, 0.35f, 1f));
        material.setColor("Specular", new ColorRGBA(0.15f, 0.15f, 0.15f, 1f));
        material.setFloat("Shininess", 8f);

        aplicarMaterialRecursivamente(modelo, material);
    }

    private static void aplicarMaterialRecursivamente(Spatial spatial, Material material) {
        if (spatial instanceof Geometry) {
            ((Geometry) spatial).setMaterial(material);
        }
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial filho : node.getChildren()) {
                aplicarMaterialRecursivamente(filho, material);
            }
        }
    }
}
