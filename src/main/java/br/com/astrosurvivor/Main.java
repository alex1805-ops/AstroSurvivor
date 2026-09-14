package br.com.astrosurvivor;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;

import com.jme3.math.ColorRGBA;

import com.jme3.material.Material;
import com.jme3.material.RenderState;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

import java.util.ArrayList;
import java.util.List;


public class Main extends SimpleApplication {

    // ==========================================================
    // OBJETOS DO JOGO
    // ==========================================================

    private Player player;
    private StarField starField;

    // ==========================================================
    // INIMIGOS
    // ==========================================================
    private final List<Inimigo> inimigos = new ArrayList<>();

    // ==========================================================
    // HUD
    // ==========================================================

    private BitmapText textoVida;
    private BarraVida barraVida;

    // ==========================================================
    // MENU
    // ==========================================================

    private Menu menu;
    private Node menuNode;

    // ==========================================================
    // GAME OVER
    // ==========================================================

    private Node gameOverNode;

    private Geometry fundoGameOver;

    private BitmapText tituloGameOver;
    private BitmapText textoGameOver;

    private Geometry botaoJogarNovamente;
    private BitmapText textoBotaoJogarNovamente;

    private Geometry botaoVoltarMenu;
    private BitmapText textoBotaoVoltarMenu;

    // ==========================================================
    // ESTADO DO JOGO
    // ==========================================================

    private enum EstadoJogo {
        MENU,
        JOGANDO,
        GAME_OVER
    }

    private EstadoJogo estadoAtual = EstadoJogo.MENU;

    // ==========================================================
    // MOVIMENTO
    // ==========================================================

    private static final String MOVIMENTO_FRENTE =
        "MovimentoFrente";

    private static final String MOVIMENTO_TRAS =
        "MovimentoTras";

    private static final String MOVIMENTO_ESQUERDA =
        "MovimentoEsquerda";

    private static final String MOVIMENTO_DIREITA =
        "MovimentoDireita";

    // ==========================================================
    // AÇÕES
    // ==========================================================

    private static final String TESTE_DANO =
        "TesteDano";

    private static final String CLIQUE_MOUSE =
        "CliqueMouse";

    // ==========================================================
    // LISTENER DE MOVIMENTO
    // ==========================================================

    private final AnalogListener analogListener =
        (name, value, tpf) -> {

            if (estadoAtual != EstadoJogo.JOGANDO) {
                return;
            }

            if (name.equals(MOVIMENTO_FRENTE)) {
                player.mover(
                    Vector3f.UNIT_Z.negate(),
                    tpf
                );
            }

            if (name.equals(MOVIMENTO_TRAS)) {
                player.mover(
                    Vector3f.UNIT_Z,
                    tpf
                );
            }

            if (name.equals(MOVIMENTO_ESQUERDA)) {
                player.mover(
                    Vector3f.UNIT_X.negate(),
                    tpf
                );
            }

            if (name.equals(MOVIMENTO_DIREITA)) {
                player.mover(
                    Vector3f.UNIT_X,
                    tpf
                );
            }
        };

    // ==========================================================
    // LISTENER DE AÇÕES
    // ==========================================================

    private final ActionListener actionListener = new ActionListener() {

            @Override
            public void onAction(
                String name,
                boolean isPressed,
                float tpf
            ) {

                if (!isPressed) {
                    return;
                }

                // ==================================================
                // TESTE DE DANO
                // ==================================================

                if (name.equals(TESTE_DANO)) {

                    if (estadoAtual == EstadoJogo.JOGANDO) {

                        System.out.println(
                            "SPACE FOI APERTADO!"
                        );

                        player.receberDano(10);
                    }

                    return;
                }

                // ==================================================
                // CLIQUE DO MOUSE
                // ==================================================

                if (name.equals(CLIQUE_MOUSE)) {

                    processarClique();
                }
            }
        };

    // ==========================================================
    // MAIN
    // ==========================================================

    public static void main(String[] args) {

        Main jogo = new Main();

        AppSettings configuracoes =
            new AppSettings(true);

        configuracoes.setTitle(
            "Astro Survivor"
        );

        configuracoes.setResolution(
            1280,
            720
        );

        configuracoes.setFullscreen(true);

        jogo.setSettings(
            configuracoes
        );

        jogo.start();
    }

    // ==========================================================
    // INICIALIZAÇÃO
    // ==========================================================

    @Override
    public void simpleInitApp() {

        setDisplayStatView(false);
        setDisplayFps(false);

        flyCam.setEnabled(false);

        // ======================================================
        // CONTROLES
        // ======================================================

        configurarControles();

        // ======================================================
        // PLAYER
        // ======================================================

        player = new Player(assetManager);

        player.setPosition(
            new Vector3f(0, 0, 0)
        );

        rootNode.attachChild(
            player.getNode()
        );

        // ======================================================
        // ESTRELAS
        // ======================================================

        starField = new StarField(
            assetManager
        );

        rootNode.attachChild(
            starField.getNode()
        );

        // ======================================================
        // CÂMERA
        // ======================================================

        cam.setLocation(
            new Vector3f(0, 8, 12)
        );

        cam.lookAt(
            Vector3f.ZERO,
            Vector3f.UNIT_Y
        );

        // ======================================================
        // FONTE
        // ======================================================

        BitmapFont fonte =
            assetManager.loadFont(
                "Interface/Fonts/Default.fnt"
            );

        // ======================================================
        // HUD
        // ======================================================

        criarHUD(fonte);

        // ======================================================
        // GAME OVER
        // ======================================================

        criarGameOver(fonte);

        // ======================================================
        // MENU
        // ======================================================

        menu = new Menu(this);
        menuNode = menu.getNode();
        guiNode.attachChild(menuNode);

        // ======================================================
        // COMEÇA NO MENU
        // ======================================================

        mostrarMenu();
    }

    // ==========================================================
    // CONTROLES
    // ==========================================================

    private void configurarControles() {

        inputManager.addMapping(
            MOVIMENTO_FRENTE,
            new KeyTrigger(KeyInput.KEY_W)
        );

        inputManager.addMapping(
            MOVIMENTO_TRAS,
            new KeyTrigger(KeyInput.KEY_S)
        );

        inputManager.addMapping(
            MOVIMENTO_ESQUERDA,
            new KeyTrigger(KeyInput.KEY_A)
        );

        inputManager.addMapping(
            MOVIMENTO_DIREITA,
            new KeyTrigger(KeyInput.KEY_D)
        );

        inputManager.addMapping(
            TESTE_DANO,
            new KeyTrigger(KeyInput.KEY_SPACE)
        );

        inputManager.addMapping(
            CLIQUE_MOUSE,
            new MouseButtonTrigger(
                MouseInput.BUTTON_LEFT
            )
        );

        inputManager.addListener(
            analogListener,

            MOVIMENTO_FRENTE,
            MOVIMENTO_TRAS,
            MOVIMENTO_ESQUERDA,
            MOVIMENTO_DIREITA
        );

        inputManager.addListener(
            actionListener,

            TESTE_DANO,
            CLIQUE_MOUSE
        );
    }

    // ==========================================================
    // HUD
    // ==========================================================

    private void criarHUD(BitmapFont fonte) {

        textoVida = new BitmapText(
            fonte,
            false
        );

        textoVida.setSize(24);

        textoVida.setColor(
            ColorRGBA.White
        );

        textoVida.setText(
            "VIDA: "
            + player.getVida()
            + "/"
            + player.getVidaMax()
        );

        textoVida.setLocalTranslation(
            20,
            cam.getHeight() - 20,
            0
        );

        guiNode.attachChild(
            textoVida
        );

        // ======================================================
        // BARRA
        // ======================================================

        barraVida = new BarraVida(
            assetManager,
            20,
            cam.getHeight() - 80
        );

        guiNode.attachChild(
            barraVida.getNode()
        );

        barraVida.atualizar(
            player.getVida(),
            player.getVidaMax()
        );
    }

    // ==========================================================
    // GAME OVER
    // ==========================================================

    private void criarGameOver(BitmapFont fonte) {

        gameOverNode = new Node(
            "GameOver"
        );

        guiNode.attachChild(
            gameOverNode
        );

        // ======================================================
        // FUNDO
        // ======================================================

        fundoGameOver = new Geometry(
            "FundoGameOver",
            new Quad(
                cam.getWidth(),
                cam.getHeight()
            )
        );

        Material materialFundo =
            new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
            );

        materialFundo.setColor(
            "Color",
            new ColorRGBA(
                0f,
                0f,
                0f,
                0.75f
            )
        );

        materialFundo
            .getAdditionalRenderState()
            .setBlendMode(
                RenderState.BlendMode.Alpha
            );

        fundoGameOver.setMaterial(
            materialFundo
        );

        gameOverNode.attachChild(
            fundoGameOver
        );

        // ======================================================
        // TÍTULO
        // ======================================================

        tituloGameOver =
            new BitmapText(
                fonte,
                false
            );

        tituloGameOver.setSize(72);

        tituloGameOver.setColor(
            ColorRGBA.Red
        );

        tituloGameOver.setText(
            "GAME OVER"
        );

        centralizarTexto(
            tituloGameOver,
            cam.getWidth() / 2f,
            500
        );

        gameOverNode.attachChild(
            tituloGameOver
        );

        // ======================================================
        // MENSAGEM
        // ======================================================

        textoGameOver =
            new BitmapText(
                fonte,
                false
            );

        textoGameOver.setSize(30);

        textoGameOver.setColor(
            ColorRGBA.White
        );

        textoGameOver.setText(
            "Sua nave foi destruída!"
        );

        centralizarTexto(
            textoGameOver,
            cam.getWidth() / 2f,
            430
        );

        gameOverNode.attachChild(
            textoGameOver
        );

        // ======================================================
        // BOTÃO JOGAR NOVAMENTE
        // ======================================================

        botaoJogarNovamente =
            criarBotao(
                350,
                70,
                (cam.getWidth() - 350) / 2f,
                320
            );

        gameOverNode.attachChild(
            botaoJogarNovamente
        );

        textoBotaoJogarNovamente =
            criarTextoBotao(
                fonte,
                "JOGAR NOVAMENTE",
                botaoJogarNovamente
            );

        gameOverNode.attachChild(
            textoBotaoJogarNovamente
        );

        // ======================================================
        // BOTÃO VOLTAR
        // ======================================================

        botaoVoltarMenu =
            criarBotao(
                350,
                70,
                (cam.getWidth() - 350) / 2f,
                220
            );

        gameOverNode.attachChild(
            botaoVoltarMenu
        );

        textoBotaoVoltarMenu =
            criarTextoBotao(
                fonte,
                "VOLTAR AO MENU",
                botaoVoltarMenu
            );

        gameOverNode.attachChild(
            textoBotaoVoltarMenu
        );
    }

    // ==========================================================
    // CRIAR BOTÃO DO GAME OVER
    // ==========================================================

    private Geometry criarBotao(
        float largura,
        float altura,
        float x,
        float y
    ) {

        Geometry botao =
            new Geometry(
                "Botao",
                new Quad(
                    largura,
                    altura
                )
            );

        Material material =
            new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md"
            );

        material.setColor(
            "Color",
            ColorRGBA.DarkGray
        );

        botao.setMaterial(
            material
        );

        botao.setLocalTranslation(
            x,
            y,
            2
        );

        return botao;
    }

    // ==========================================================
    // TEXTO DO BOTÃO
    // ==========================================================

    private BitmapText criarTextoBotao(
        BitmapFont fonte,
        String texto,
        Geometry botao
    ) {

        BitmapText textoBotao =
            new BitmapText(
                fonte,
                false
            );

        textoBotao.setSize(28);

        textoBotao.setColor(
            ColorRGBA.White
        );

        textoBotao.setText(
            texto
        );

        float larguraBotao = 350f;
        float alturaBotao = 70f;

        float larguraTexto =
            textoBotao.getLineWidth();

        float x =
            botao.getLocalTranslation().x
            + (larguraBotao - larguraTexto) / 2f;

        float y =
            botao.getLocalTranslation().y
            + (alturaBotao / 2f)
            + 10f;

        textoBotao.setLocalTranslation(
            x,
            y,
            3
        );

        return textoBotao;
    }

    // ==========================================================
    // CENTRALIZAR TEXTO
    // ==========================================================

    private void centralizarTexto(
        BitmapText texto,
        float centroX,
        float posicaoY
    ) {

        float largura =
            texto.getLineWidth();

        texto.setLocalTranslation(
            centroX - largura / 2f,
            posicaoY,
            3
        );
    }

    // ==========================================================
    // CLIQUE
    // ==========================================================

    private void processarClique() {

        Vector2f cursor = inputManager.getCursorPosition();

        float mouseX = cursor.x;
        float mouseY = cursor.y;

        // ======================================================
        // MENU
        // ======================================================
        if (estadoAtual == EstadoJogo.MENU) {

            if (mouseDentro(mouseX, mouseY, menu.getAreaBotaoJogar())) {
                iniciarJogo();
                return;
            }

            if (mouseDentro(mouseX, mouseY, menu.getAreaBotaoSair())) {
                System.out.println("SAINDO DO JOGO...");
                stop();
                return;
            }
        }

        // ======================================================
        // GAME OVER
        // ======================================================

        if (estadoAtual == EstadoJogo.GAME_OVER) {

            if (mouseDentro(mouseX, mouseY, botaoJogarNovamente)) {
                reiniciarJogo();
                return;
            }

            if (mouseDentro(mouseX, mouseY, botaoVoltarMenu)) {
                voltarAoMenu();
                return;
            }
        }
    }

    // ==========================================================
    // CLIQUE EM TEXTO
    // ==========================================================

    private boolean mouseDentroTexto(float mouseX, float mouseY, BitmapText texto) {
        float x =texto.getLocalTranslation().x;

        float y = texto.getLocalTranslation().y;

        float largura = texto.getLineWidth();

        float altura = texto.getLineHeight();

        return mouseX >= x
            && mouseX <= x + largura
            && mouseY >= y - altura
            && mouseY <= y;
    }

    // ==========================================================
    // CLIQUE EM GEOMETRIA
    // ==========================================================

    private boolean mouseDentro(
        float mouseX,
        float mouseY,
        Geometry botao
    ) {

        float x =
            botao.getLocalTranslation().x;

        float y =
            botao.getLocalTranslation().y;

        float largura = 350f;
        float altura = 70f;

        return mouseX >= x
            && mouseX <= x + largura
            && mouseY >= y
            && mouseY <= y + altura;
    }

    // ==========================================================
    // INICIAR JOGO
    // ==========================================================

    private void iniciarJogo() {

        estadoAtual = EstadoJogo.JOGANDO;

        menu.remover();

        if (menuNode.getParent() != null) {
            menuNode.removeFromParent();
        }

        menu.getNode().setCullHint(
            Node.CullHint.Always
        );

        gameOverNode.setCullHint(
            Node.CullHint.Always
        );

        player.getNode().setCullHint(
            Node.CullHint.Never
        );

        starField.getNode().setCullHint(
            Node.CullHint.Never
        );

        textoVida.setCullHint(
            BitmapText.CullHint.Never
        );

        barraVida.getNode().setCullHint(
            Node.CullHint.Never
        );

        criarInimigo();

        inputManager.setCursorVisible(
            false
        );

        System.out.println(
            "JOGO INICIADO!"
        );
    }

    // ==========================================================
    // MORTE
    // ==========================================================

    private void jogadorMorreu() {

        estadoAtual =
            EstadoJogo.GAME_OVER;

        System.out.println(
            "PLAYER MORREU!"
        );

        player.getNode().setCullHint(
            Node.CullHint.Always
        );

        textoVida.setCullHint(
            BitmapText.CullHint.Always
        );

        barraVida.getNode().setCullHint(
            Node.CullHint.Always
        );

        gameOverNode.setCullHint(
            Node.CullHint.Never
        );

        inputManager.setCursorVisible(
            true
        );
    }

    // ==========================================================
    // CRIAR INIMIGOS
    // ==========================================================
    private void criarInimigo(){
        Vector3f posicaoInicial = player.getNode().getLocalTranslation().add(0, 0, -30);

        Inimigo inimigo = new Inimigo(assetManager, posicaoInicial);

        inimigos.add(inimigo);

        rootNode.attachChild(inimigo.getNode());
    }

    // ==========================================================
    // COLISÃO
    // ==========================================================
    private void verificarColisoesComInimigos(){
        Vector3f posicaoJogador = player.getNode().getLocalTranslation();

        for (int i = inimigos .size() - 1; i >= 0; i--){
            Inimigo inimigo = inimigos.get(i);

            float distancia = posicaoJogador.distance(inimigo.getPosicao());

            if(distancia <= 1.5f){
                player.receberDano(10);

                rootNode.detachChild(inimigo.getNode());

                inimigos.remove(i);

                System.out.println("O jogador foi atingido!");
            }
        }
    }

    // ==========================================================
    // REINICIAR
    // ==========================================================

    private void reiniciarJogo() {

        player.resetarVida();

        player.setPosition(
            new Vector3f(0, 0, 0)
        );

        barraVida.atualizar(
            player.getVida(),
            player.getVidaMax()
        );

        estadoAtual =
            EstadoJogo.JOGANDO;

        gameOverNode.setCullHint(
            Node.CullHint.Always
        );

        player.getNode().setCullHint(
            Node.CullHint.Never
        );

        textoVida.setCullHint(
            BitmapText.CullHint.Never
        );

        barraVida.getNode().setCullHint(
            Node.CullHint.Never
        );

        inputManager.setCursorVisible(
            false
        );

        System.out.println(
            "JOGO REINICIADO!"
        );
    }

    // ==========================================================
    // VOLTAR AO MENU
    // ==========================================================

    private void voltarAoMenu() {

        estadoAtual =
            EstadoJogo.MENU;

        gameOverNode.setCullHint(
            Node.CullHint.Always
        );

        menu.getNode().setCullHint(
            Node.CullHint.Never
        );

        player.getNode().setCullHint(
            Node.CullHint.Always
        );

        starField.getNode().setCullHint(
            Node.CullHint.Always
        );

        textoVida.setCullHint(
            BitmapText.CullHint.Always
        );

        barraVida.getNode().setCullHint(
            Node.CullHint.Always
        );

        inputManager.setCursorVisible(
            true
        );

        System.out.println(
            "VOLTANDO AO MENU..."
        );
    }

    // ==========================================================
    // MOSTRAR MENU
    // ==========================================================

    private void mostrarMenu() {

        estadoAtual =
            EstadoJogo.MENU;

        menu.getNode().setCullHint(
            Node.CullHint.Never
        );

        gameOverNode.setCullHint(
            Node.CullHint.Always
        );

        player.getNode().setCullHint(
            Node.CullHint.Always
        );

        starField.getNode().setCullHint(
            Node.CullHint.Always
        );

        textoVida.setCullHint(
            BitmapText.CullHint.Always
        );

        barraVida.getNode().setCullHint(
            Node.CullHint.Always
        );

        inputManager.setCursorVisible(
            true
        );
    }

    // ==========================================================
    // UPDATE
    // ==========================================================

    @Override
    public void simpleUpdate(float tpf) {

        if (estadoAtual != EstadoJogo.JOGANDO) {
            return;
        }

        Vector3f posicaoJogador = player.getNode().getLocalTranslation();

        // ==========================================================
        // INIMIGOS
        // ==========================================================
        for (Inimigo inimigo : inimigos){
            inimigo.atualizar(tpf, posicaoJogador);
        }

        verificarColisoesComInimigos();

        // ======================================================
        // POSIÇÃO
        // ======================================================

        Vector3f posicao =
            player.getPosition();

        // ======================================================
        // CÂMERA
        // ======================================================

        Vector3f posicaoCamera =
            new Vector3f(
                posicao.x,
                posicao.y + 8,
                posicao.z + 12
            );

        cam.setLocation(
            posicaoCamera
        );

        cam.lookAt(
            posicao,
            Vector3f.UNIT_Y
        );

        // ======================================================
        // VIDA
        // ======================================================

        textoVida.setText(
            "VIDA: "
            + player.getVida()
            + "/"
            + player.getVidaMax()
        );

        barraVida.atualizar(
            player.getVida(),
            player.getVidaMax()
        );

        // ======================================================
        // MORTE
        // ======================================================

        if (!player.estaVivo()) {

            jogadorMorreu();
        }

        // ==========================================================
        // INIMIGOS
        // ==========================================================
        for (Inimigo inimigo : inimigos){
            inimigo.atualizar(tpf, player.getNode().getLocalTranslation());
        }
    }
}