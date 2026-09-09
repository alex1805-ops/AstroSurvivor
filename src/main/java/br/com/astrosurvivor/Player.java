package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class Player{
    private Node node;
    
    private Geometry corpo;
    private Geometry cabine;
    private Geometry asaEsquerda;
    private Geometry asaDireita;
    private Geometry cauda;

    private float velocidade = 5f;

    private int vidaMax = 100;
    private int vidaAtual = vidaMax;

    // =========================================
    // CRIA O PLAYER
    // =========================================
    public Player(AssetManager assetManager){

        node = new Node("Player");

        // =========================================
        // CORPO
        // =========================================
        Box formatoCorpo = new Box(0.7f, 0.3f, 1.8f);

        corpo = new Geometry("Corpo", formatoCorpo);

        Material materialCorpo = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialCorpo.setColor("Color", ColorRGBA.Blue);

        corpo.setMaterial(materialCorpo);

        node.attachChild(corpo);

        // =========================================
        // CABINE
        // =========================================
        Box formatoCabine = new Box(0.5f, 0.25f, 0.6f);

        cabine = new Geometry("Cabine", formatoCabine);

        Material materialCabine = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialCabine.setColor("Color", ColorRGBA.Green);

        cabine.setMaterial(materialCabine);

        node.attachChild(cabine);

        // =========================================
        // ASA ESQUERDA
        // =========================================
        Box formatoAsaEsquerda = new Box(0.8f, 0.1f, 0.7f);

        asaEsquerda = new Geometry("AsaEsquerda", formatoAsaEsquerda);

        Material materialAsaEsquerda = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialAsaEsquerda.setColor("Color", ColorRGBA.Gray);

        asaEsquerda.setMaterial(materialAsaEsquerda);

        asaEsquerda.setLocalTranslation(-1.2f, 0, 0);

        // INCLINA A ASA
        asaEsquerda.rotate(0, 0.3f, 0);

        node.attachChild(asaEsquerda);

        // =========================================
        // ASA DIREITA
        // =========================================
        Box formatoAsaDireita = new Box(0.8f, 0.1f, 0.7f);

        asaDireita = new Geometry("AsaDireita", formatoAsaDireita);

        Material materialAsaDireita = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialAsaDireita.setColor("Color", ColorRGBA.White);

        asaDireita.setMaterial(materialAsaDireita);

        asaDireita.setLocalTranslation(1.2f, 0, 0);

        // INCLINA A ASA
        asaDireita.rotate(0, -0.3f, 0);

        node.attachChild(asaDireita);

        // =========================================
        // CAUDA
        // =========================================
        Box formatoCauda = new Box(0.4f, 0.5f, 0.4f);

        cauda = new Geometry("Cauda", formatoCauda);

        Material materialCauda = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md"
        );

        materialCauda.setColor("Color", ColorRGBA.Red);

        cauda.setMaterial(materialCauda);

        cauda.setLocalTranslation(0, 0.5f, 1.2f);

        node.attachChild(cauda);
    }

    // =========================================
    // MOVIMENTAÇÃO
    // =========================================
    public Vector3f mover(Vector3f direcao, float tpf){
        
        Vector3f movimento = direcao.mult(velocidade * tpf);

        node.move(movimento);
        return movimento;
    }

    // =========================================
    // SISTEMA DE VIDA
    // =========================================
    public void receberDano(int dano){
        
        if(!estaVivo()){
            return;
        }

        vidaAtual -= dano;

        if(vidaAtual < 0){
            vidaAtual = 0;
        }

        System.out.println("Vida atual: " + vidaAtual + "/" + vidaMax);
    }

    public void curar(int conserto){
        vidaAtual += conserto;

        if(vidaAtual > vidaMax){
            vidaAtual = vidaMax;
        }
    }

    public int getVida(){
        return vidaAtual;
    }

    public int getVidaMax(){
        return vidaMax;
    }

    public boolean estaVivo(){
        return vidaAtual > 0;
    }

    // ========================================
    // ACESSO AO NODE
    // ========================================
    public Node getNode(){
        return node;
    }

    // ========================================
    // POSIÇÃO
    // ========================================
    public void setPosition(Vector3f position){
        node.setLocalTranslation(position);
    }

    public Vector3f getPosition(){
        return node.getLocalTranslation();
    }

    public void resetarVida(){
        vidaAtual = vidaMax;
    }
}