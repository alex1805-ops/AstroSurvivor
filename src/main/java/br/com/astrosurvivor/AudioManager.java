package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;

public class AudioManager {

    private final AssetManager assetManager;
    private final Node audioNode;

    public AudioManager(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;

        audioNode = new Node("AudioManager");
        rootNode.attachChild(audioNode);
    }

    public void tocarTiro() {
        tocarEfeito("Sounds/tiro.ogg");
    }

    public void tocarExplosao() {
        tocarEfeito("Sounds/explosao.ogg");
    }

    public void tocarDano() {
        tocarEfeito("Sounds/dano.ogg");
    }

    public void tocarGameOver() {
        tocarEfeito("Sounds/gameover.ogg");
    }

    public void tocarLevelUp() {
        tocarEfeito("Sounds/levelup.ogg");
    }

    public void tocarBossInicio() {
        tocarEfeito("Sounds/boss_inicio.ogg");
    }

    public void tocarBossMorte() {
        tocarEfeito("Sounds/boss_morte.ogg");
    }

    public void tocarUpgrade() {
        tocarEfeito("Sounds/upgrade.ogg");
    }

    public void tocarClique() {
        tocarEfeito("Sounds/clique.ogg");
    }

    private void tocarEfeito(String caminho) {

        AudioNode som = new AudioNode(assetManager, caminho, false);

        som.setPositional(false);
        som.setVolume(1.0f);

        audioNode.attachChild(som);

        som.play();
    }
}