package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
public class AudioManager {
private final Node audioNode;

private final AudioNode tiro;
private final AudioNode explosao;
private final AudioNode dano;
private final AudioNode gameOver;
private final AudioNode levelUp;
private final AudioNode bossInicio;
private final AudioNode bossMorte;
private final AudioNode upgrade;
private final AudioNode clique;

public AudioManager(AssetManager assetManager, Node rootNode) {

    audioNode = new Node("AudioManager");
    rootNode.attachChild(audioNode);

    tiro = criarSom(assetManager, "Sounds/tiro.wav");
    explosao = criarSom(assetManager, "Sounds/explosao.wav");
    dano = criarSom(assetManager, "Sounds/dano.wav");
    gameOver = criarSom(assetManager, "Sounds/gameover.wav");
    levelUp = criarSom(assetManager, "Sounds/levelup.wav");
    bossInicio = criarSom(assetManager, "Sounds/boss_inicio.wav");
    bossMorte = criarSom(assetManager, "Sounds/boss_morte.wav");
    upgrade = criarSom(assetManager, "Sounds/upgrade.wav");
    clique = criarSom(assetManager, "Sounds/clique.wav");

    audioNode.attachChild(tiro);
    audioNode.attachChild(explosao);
    audioNode.attachChild(dano);
    audioNode.attachChild(gameOver);
    audioNode.attachChild(levelUp);
    audioNode.attachChild(bossInicio);
    audioNode.attachChild(bossMorte);
    audioNode.attachChild(upgrade);
    audioNode.attachChild(clique);
    System.out.println("ÁUDIOS CARREGADOS COM SUCESSO!");
}

private AudioNode criarSom(AssetManager assetManager, String caminho) {

    AudioNode som = new AudioNode(
            assetManager,
            caminho,
            false
    );

    som.setPositional(false);
    som.setVolume(1.0f);

    return som;
}

public void tocarTiro() {
    System.out.println("TOCANDO TIRO!");
    tocar(tiro);
}

public void tocarExplosao() {
    tocar(explosao);
}

public void tocarDano() {
    tocar(dano);
}

public void tocarGameOver() {
    tocar(gameOver);
}

public void tocarLevelUp() {
    tocar(levelUp);
}

public void tocarBossInicio() {
    tocar(bossInicio);
}

public void tocarBossMorte() {
    tocar(bossMorte);
}

public void tocarUpgrade() {
    tocar(upgrade);
}

public void tocarClique() {
    tocar(clique);
}

private void tocar(AudioNode som) {

    som.stop();
    som.play();
}

}
