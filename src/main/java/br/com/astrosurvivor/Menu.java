package br.com.astrosurvivor;

import com.jme3.app.SimpleApplication;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;

import com.jme3.math.ColorRGBA;

import com.jme3.material.Material;
import com.jme3.material.RenderState;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import com.jme3.scene.shape.Quad;

import com.jme3.ui.Picture;

public class Menu {

    private SimpleApplication app;

    private Node node;

    // ======================================================
    // FUNDO
    // ======================================================

    private Picture fundo;

    // ======================================================
    // TÍTULO
    // ======================================================

    private BitmapText titulo;

    // ======================================================
    // TEXTOS DOS BOTÕES
    // ======================================================

    private BitmapText botaoJogar;
    private BitmapText botaoSair;

    // ======================================================
    // ÁREAS CLICÁVEIS
    // ======================================================

    private Geometry areaBotaoJogar;
    private Geometry areaBotaoSair;

    // ======================================================
    // TAMANHO DOS BOTÕES
    // ======================================================

    private final float larguraBotao = 350f;
    private final float alturaBotao = 70f;

    // ======================================================
    // POSIÇÃO DOS BOTÕES
    // ======================================================

    private final float posicaoJogarY = 330f;
    private final float posicaoSairY = 250f;

    // ======================================================
    // CONSTRUTOR
    // ======================================================

    public Menu(SimpleApplication app) {

        this.app = app;

        node = new Node("Menu");

        criarMenu();
    }

    // ======================================================
    // CRIA MENU
    // ======================================================

    private void criarMenu() {

        BitmapFont fonte =
            app.getAssetManager().loadFont(
                "Interface/Fonts/Default.fnt"
            );

        BitmapFont fonteTitulo =
            app.getAssetManager().loadFont(
                "Interface/Fonts/Orbitron.fnt"
            );

        // ==================================================
        // FUNDO
        // ==================================================

        fundo = new Picture("MenuFundo");

        fundo.setImage(
            app.getAssetManager(),
            "Interface/menu_fundo.png",
            true
        );

        fundo.setWidth(
            app.getCamera().getWidth()
        );

        fundo.setHeight(
            app.getCamera().getHeight()
        );

        fundo.setPosition(
            0,
            0
        );

        fundo.setPosition(
            0,
            0
        );

        node.attachChild(
            fundo
        );

        // ==================================================
        // TÍTULO
        // ==================================================

        titulo = new BitmapText(
            fonteTitulo,
            false
        );

        titulo.setText(
            "ASTRO SURVIVOR"
        );

        titulo.setSize(
            60
        );

        titulo.setColor(
            ColorRGBA.White
        );

        float larguraTitulo =
            titulo.getLineWidth();

        titulo.setLocalTranslation(
            (app.getCamera().getWidth() - larguraTitulo) / 2f,
            520,
            2
        );

        node.attachChild(
            titulo
        );

        // ==================================================
        // BOTÃO JOGAR
        // ==================================================

        float xJogar =
            (app.getCamera().getWidth() - larguraBotao) / 2f;

        areaBotaoJogar =
            criarAreaBotao(
                "AreaBotaoJogar",
                xJogar,
                posicaoJogarY
            );

        node.attachChild(
            areaBotaoJogar
        );

        botaoJogar =
            criarTextoBotao(
                fonteTitulo,
                "JOGAR",
                xJogar,
                posicaoJogarY
            );

        node.attachChild(
            botaoJogar
        );

        // ==================================================
        // BOTÃO SAIR
        // ==================================================

        float xSair =
            (app.getCamera().getWidth() - larguraBotao) / 2f;

        areaBotaoSair =
            criarAreaBotao(
                "AreaBotaoSair",
                xSair,
                posicaoSairY
            );

        node.attachChild(
            areaBotaoSair
        );

        botaoSair =
            criarTextoBotao(
                fonteTitulo,
                "SAIR",
                xSair,
                posicaoSairY
            );

        node.attachChild(
            botaoSair
        );
    }

    // ======================================================
    // CRIA ÁREA CLICÁVEL + VISUAL DO BOTÃO
    // ======================================================

    private Geometry criarAreaBotao(
        String nome,
        float x,
        float y
    ) {

        Geometry area =
            new Geometry(
                nome,
                new Quad(
                    larguraBotao,
                    alturaBotao
                )
            );

        // ==================================================
        // MATERIAL DO BOTÃO
        // ==================================================

        Material material =
            new Material(
                app.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md"
            );

        /*
         * Fundo escuro e transparente.
         *
         * Isso permite que o fundo do menu continue
         * aparecendo através do botão.
         */

        material.setColor(
            "Color",
            new ColorRGBA(
                0.02f,
                0.05f,
                0.09f,
                0.82f
            )
        );

        material
            .getAdditionalRenderState()
            .setBlendMode(
                RenderState.BlendMode.Alpha
            );

        area.setMaterial(
            material
        );

        area.setLocalTranslation(
            x,
            y,
            1
        );

        return area;
    }

    // ======================================================
    // CRIA TEXTO DO BOTÃO
    // ======================================================

    private BitmapText criarTextoBotao(
        BitmapFont fonteTitulo,
        String texto,
        float x,
        float y
    ) {

        BitmapText botao =
            new BitmapText(
                fonteTitulo,
                false
            );

        botao.setText(
            texto
        );

        botao.setSize(
            35
        );

        botao.setColor(
            ColorRGBA.White
        );

        float larguraTexto =
            botao.getLineWidth();

        float alturaTexto =
            botao.getLineHeight();

        // ==================================================
        // CENTRALIZA HORIZONTALMENTE
        // ==================================================

        float textoX =
            x
            + (larguraBotao - larguraTexto) / 2f;

        // ==================================================
        // CENTRALIZA VERTICALMENTE
        // ==================================================

        float textoY =
            y
            + (alturaBotao + alturaTexto) / 2f;

        botao.setLocalTranslation(
            textoX,
            textoY,
            2
        );

        return botao;
    }

    // ======================================================
    // ACESSO AO NODE
    // ======================================================

    public Node getNode() {

        return node;
    }

    // ======================================================
    // ACESSO AO BOTÃO JOGAR
    // ======================================================

    public BitmapText getBotaoJogar() {

        return botaoJogar;
    }

    // ======================================================
    // ACESSO AO BOTÃO SAIR
    // ======================================================

    public BitmapText getBotaoSair() {

        return botaoSair;
    }

    // ======================================================
    // ACESSO À ÁREA CLICÁVEL DO JOGAR
    // ======================================================

    public Geometry getAreaBotaoJogar() {

        return areaBotaoJogar;
    }

    // ======================================================
    // ACESSO À ÁREA CLICÁVEL DO SAIR
    // ======================================================

    public Geometry getAreaBotaoSair() {

        return areaBotaoSair;
    }

    // ======================================================
    // REMOVE MENU
    // ======================================================

    public void remover() {

        node.removeFromParent();
    }
}