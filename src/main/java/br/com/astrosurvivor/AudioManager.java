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

    private final AudioNode musicaMenu;
    private final AudioNode motorNave;

    public AudioManager(AssetManager assetManager, Node rootNode) {
        audioNode = new Node("AudioManager");
        rootNode.attachChild(audioNode);

        tiro = criarSom(assetManager, "Sounds/tiro.wav", 1f);
        explosao = criarSom(assetManager, "Sounds/explosao.wav", 1f);
        dano = criarSom(assetManager, "Sounds/dano.wav", 1f);
        gameOver = criarSom(assetManager, "Sounds/gameover.wav", 1f);
        levelUp = criarSom(assetManager, "Sounds/levelup.wav", 1f);
        bossInicio = criarSom(assetManager, "Sounds/boss_inicio.wav", 1f);
        bossMorte = criarSom(assetManager, "Sounds/boss_morte.wav", 1f);
        upgrade = criarSom(assetManager, "Sounds/upgrade.wav", 1f);
        clique = criarSom(assetManager, "Sounds/clique.wav", 1f);

        musicaMenu = criarSom(assetManager, "Sounds/musica_menu.wav", 0.30f);
        musicaMenu.setLooping(true);

        motorNave = criarSom(assetManager, "Sounds/som_motor_nave.wav", 0.18f);
        motorNave.setLooping(true);

        audioNode.attachChild(tiro);
        audioNode.attachChild(explosao);
        audioNode.attachChild(dano);
        audioNode.attachChild(gameOver);
        audioNode.attachChild(levelUp);
        audioNode.attachChild(bossInicio);
        audioNode.attachChild(bossMorte);
        audioNode.attachChild(upgrade);
        audioNode.attachChild(clique);
        audioNode.attachChild(musicaMenu);
        audioNode.attachChild(motorNave);
    }

    private AudioNode criarSom(
            AssetManager assetManager,
            String caminho,
            float volume) {

        AudioNode som = new AudioNode(assetManager, caminho, false);
        som.setPositional(false);
        som.setVolume(volume);
        return som;
    }

    public void iniciarMusicaMenu() {
        motorNave.stop();

        if (musicaMenu.getStatus()
                != com.jme3.audio.AudioSource.Status.Playing) {
            musicaMenu.play();
        }
    }

    public void pararMusicaMenu() {
        musicaMenu.stop();
    }

    public void iniciarMotorNave() {
        musicaMenu.stop();

        if (motorNave.getStatus()
                != com.jme3.audio.AudioSource.Status.Playing) {
            motorNave.play();
        }
    }

    public void pararMotorNave() {
        motorNave.stop();
    }

    public void pararSonsContinuos() {
        musicaMenu.stop();
        motorNave.stop();
    }

    public void tocarTiro() { tocar(tiro); }
    public void tocarExplosao() { tocar(explosao); }
    public void tocarDano() { tocar(dano); }
    public void tocarGameOver() { tocar(gameOver); }
    public void tocarLevelUp() { tocar(levelUp); }
    public void tocarBossInicio() { tocar(bossInicio); }
    public void tocarBossMorte() { tocar(bossMorte); }
    public void tocarUpgrade() { tocar(upgrade); }
    public void tocarClique() { tocar(clique); }

    private void tocar(AudioNode som) {
        som.stop();
        som.play();
    }
}
