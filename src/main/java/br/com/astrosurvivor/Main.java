package br.com.astrosurvivor;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

        // ==========================================================
        // OBJETOS DO JOGO
        // ==========================================================

        private Player player;
        private StarField starField;
        private GerenciadorBoss gerenciadorBoss;
        private AudioManager audioManager;

        // ==========================================================
        // INIMIGOS
        // ==========================================================

        private final List<Inimigo> inimigos = new ArrayList<>();

        private float tempoSpawn = 0f;

        // Tempo entre o nascimento de cada meteoro
        private final float intervaloSpawn = 2.0f;

        // ==========================================================
        // PROJÉTEIS
        // ==========================================================
        private final List<Projetil> projeteis = new ArrayList<>();

        // ==========================================================
        // HUD
        // ==========================================================

        private BitmapText textoVida;
        private BarraVida barraVida;
        private int score = 0;
        private BitmapText textoScore;
        private BitmapText textoNivel;
        private BitmapText textoXP;
        private BitmapText TextoEscudo;

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
        // UPGRADES
        // ==========================================================
        private Node upgradeNode;

        private Geometry fundoUpgrade;

        private BitmapText tituloUpgrade;

        private BitmapText[] textosUpgrade = new BitmapText[5];

        private Geometry[] botoesUpgrade = new Geometry[5];

        // ==========================================================
        // ESTADO DO JOGO
        // ==========================================================

        private enum EstadoJogo {
                MENU,
                JOGANDO,
                BOSS,
                ESCOLHA_UPGRADE,
                GAME_OVER
        }

        private EstadoJogo estadoAtual = EstadoJogo.MENU;

        // ==========================================================
        // MOVIMENTO
        // ==========================================================

        private static final String MOVIMENTO_FRENTE = "MovimentoFrente";
        private static final String MOVIMENTO_TRAS = "MovimentoTras";
        private static final String MOVIMENTO_ESQUERDA = "MovimentoEsquerda";
        private static final String MOVIMENTO_DIREITA = "MovimentoDireita";

        // ==========================================================
        // AÇÕES
        // ==========================================================

        private static final String TESTE_DANO = "TesteDano";
        private static final String CLIQUE_MOUSE = "CliqueMouse";

        // ==========================================================
        // AUXILIAR: o jogador pode agir (andar/atirar)?
        // ==========================================================

        private boolean jogadorPodeAgir() {
                return estadoAtual == EstadoJogo.JOGANDO
                                || estadoAtual == EstadoJogo.BOSS;
        }

        // ==========================================================
        // LISTENER DE MOVIMENTO
        // ==========================================================

        private final AnalogListener analogListener = (name, value, tpf) -> {

                // CORRIGIDO: agora também funciona durante o boss
                if (!jogadorPodeAgir()) {
                        return;
                }

                if (name.equals(MOVIMENTO_FRENTE)) {
                        player.mover(Vector3f.UNIT_Z.negate(), tpf);
                }

                if (name.equals(MOVIMENTO_TRAS)) {
                        player.mover(Vector3f.UNIT_Z, tpf);
                }

                if (name.equals(MOVIMENTO_ESQUERDA)) {
                        player.mover(Vector3f.UNIT_X.negate(), tpf);
                }

                if (name.equals(MOVIMENTO_DIREITA)) {
                        player.mover(Vector3f.UNIT_X, tpf);
                }
        };

        // ==========================================================
        // LISTENER DE AÇÕES
        // ==========================================================

        private final ActionListener actionListener = new ActionListener() {

                @Override
                public void onAction(String name, boolean isPressed, float tpf) {

                        if (!isPressed) {
                                return;
                        }

                        // TESTE DE DANO
                        if (name.equals(TESTE_DANO)) {

                                if (jogadorPodeAgir()) {
                                        System.out.println("SPACE FOI APERTADO!");
                                        player.receberDano(10);
                                }

                                return;
                        }

                        // CLIQUE DO MOUSE
                        if (name.equals(CLIQUE_MOUSE)) {

                                // CORRIGIDO: também atira durante o boss
                                if (jogadorPodeAgir()) {
                                        atirar();
                                } else {
                                        processarClique();
                                }
                        }
                }
        };

        // ==========================================================
        // PROCESSA CLIQUE UPGRADE
        // ==========================================================
        private void processarCliqueUpgrade(float mouseX, float mouseY) {

                for (int i = 0; i < 5; i++) {

                        if (mouseDentro(mouseX, mouseY, botoesUpgrade[i])) {

                                TipoUpgrade upgrade;

                                switch (i) {
                                        case 0:
                                                upgrade = TipoUpgrade.VIDA_MAXIMA;
                                                break;
                                        case 1:
                                                upgrade = TipoUpgrade.REGENERACAO;
                                                break;
                                        case 2:
                                                upgrade = TipoUpgrade.ESCUDO;
                                                break;
                                        case 3:
                                                upgrade = TipoUpgrade.VELOCIDADE;
                                                break;
                                        default:
                                                upgrade = TipoUpgrade.MULTI_DISPARO;
                                }

                                if (upgrade == TipoUpgrade.MULTI_DISPARO && player.getNivelMultiDisparo() >= 2) {
                                        System.out.println("NÍVEL MÁXIMO DE MULTI-DISPARO ATINGIDO!");
                                }

                                audioManager.tocarClique();

                                aplicarUpgrade(upgrade);
                                finalizarEscolhaUpgrade();
                                return;
                        }
                }
        }

        // ==========================================================
        // FINALIZAR ESCOLHA DE UPGRADE
        // ==========================================================
        private void finalizarEscolhaUpgrade() {

                upgradeNode.setCullHint(Node.CullHint.Always);

                estadoAtual = EstadoJogo.JOGANDO;

                tempoSpawn = 0f;

                inputManager.setCursorVisible(false);

                System.out.println("UPGRADE ESCOLHIDO!");
                System.out.println("Voltando ao jogo...");
        }

        // ==========================================================
        // SCORE
        // ==========================================================
        private void adicionarScore(TipoAsteroide tipo) {

                int xpGanho = 0;

                switch (tipo) {

                        case PEQUENO:
                                score += 100;
                                xpGanho = 50;
                                break;

                        case MEDIO:
                                score += 200;
                                xpGanho = 100;
                                break;

                        case GRANDE:
                                score += 300;
                                xpGanho = 150;
                                break;
                }

                boolean subiuDeNivel = player.adicionarXP(xpGanho);

                if (subiuDeNivel) {
                        audioManager.tocarLevelUp();
                }

                textoScore.setText("SCORE: " + score);

                // Só inicia boss se estiver no estado JOGANDO
                if (subiuDeNivel
                                && estadoAtual == EstadoJogo.JOGANDO
                                && player.getNivel() % 10 == 0) {

                        iniciarBoss();
                }
        }

        // ==========================================================
        // MAIN
        // ==========================================================

        public static void main(String[] args) {

                Main jogo = new Main();

                AppSettings configuracoes = new AppSettings(true);

                configuracoes.setTitle("Astro Survivor");
                configuracoes.setResolution(1280, 720);
                configuracoes.setFullscreen(true);

                jogo.setSettings(configuracoes);

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

                configurarControles();

                // PLAYER
                player = new Player(assetManager);
                audioManager = new AudioManager(assetManager, rootNode);
                player.setPosition(new Vector3f(0, 0, 0));
                rootNode.attachChild(player.getNode());

                gerenciadorBoss = new GerenciadorBoss(assetManager, rootNode, player);

                // ESTRELAS
                starField = new StarField(assetManager);
                rootNode.attachChild(starField.getNode());

                // CÂMERA
                cam.setLocation(new Vector3f(0, 8, 12));
                cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

                // FONTE
                BitmapFont fonte = assetManager.loadFont("Interface/Fonts/Default.fnt");

                criarHUD(fonte);
                criarGameOver(fonte);
                criarTelaUpgrade(fonte);

                // MENU
                menu = new Menu(this);
                menuNode = menu.getNode();
                guiNode.attachChild(menuNode);

                mostrarMenu();
        }

        // ==========================================================
        // CONTROLES
        // ==========================================================

        private void configurarControles() {

                inputManager.addMapping(MOVIMENTO_FRENTE, new KeyTrigger(KeyInput.KEY_W));
                inputManager.addMapping(MOVIMENTO_TRAS, new KeyTrigger(KeyInput.KEY_S));
                inputManager.addMapping(MOVIMENTO_ESQUERDA, new KeyTrigger(KeyInput.KEY_A));
                inputManager.addMapping(MOVIMENTO_DIREITA, new KeyTrigger(KeyInput.KEY_D));
                inputManager.addMapping(TESTE_DANO, new KeyTrigger(KeyInput.KEY_SPACE));
                inputManager.addMapping(CLIQUE_MOUSE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

                inputManager.addListener(
                                analogListener,
                                MOVIMENTO_FRENTE,
                                MOVIMENTO_TRAS,
                                MOVIMENTO_ESQUERDA,
                                MOVIMENTO_DIREITA);

                inputManager.addListener(
                                actionListener,
                                TESTE_DANO,
                                CLIQUE_MOUSE);
        }

        // ==========================================================
        // HUD
        // ==========================================================

        private void criarHUD(BitmapFont fonte) {

                textoVida = new BitmapText(fonte);
                textoVida.setSize(24);
                textoVida.setColor(ColorRGBA.White);
                textoVida.setText("VIDA: " + player.getVida() + "/" + player.getVidaMax());
                textoVida.setLocalTranslation(20, cam.getHeight() - 20, 0);
                guiNode.attachChild(textoVida);

                // SCORE
                textoScore = new BitmapText(fonte);
                textoScore.setSize(24);
                textoScore.setColor(ColorRGBA.White);
                textoScore.setText("SCORE: " + score);
                textoScore.setLocalTranslation(10, cam.getHeight() - 100, 0);
                guiNode.attachChild(textoScore);

                // NÍVEL
                textoNivel = new BitmapText(fonte);
                textoNivel.setSize(24);
                textoNivel.setColor(ColorRGBA.White);
                textoNivel.setText("NÍVEL: " + player.getNivel());
                textoNivel.setLocalTranslation(10, cam.getHeight() - 130, 0);
                guiNode.attachChild(textoNivel);

                // XP
                textoXP = new BitmapText(fonte);
                textoXP.setSize(20);
                textoXP.setColor(ColorRGBA.White);
                textoXP.setText("XP: " + player.getXp() + " / " + player.getXpNecessario());
                textoXP.setLocalTranslation(10, cam.getHeight() - 160, 0);
                guiNode.attachChild(textoXP);

                // ESCUDOS
                TextoEscudo = new BitmapText(fonte);
                TextoEscudo.setSize(20);
                TextoEscudo.setColor(ColorRGBA.White);
                TextoEscudo.setText("ESCUDOS: " + player.getEscudos());
                TextoEscudo.setLocalTranslation(10, cam.getHeight() - 190, 0);
                guiNode.attachChild(TextoEscudo);

                // BARRA DE VIDA
                barraVida = new BarraVida(assetManager, 20, cam.getHeight() - 80);
                guiNode.attachChild(barraVida.getNode());
                barraVida.atualizar(player.getVida(), player.getVidaMax());
        }

        // ==========================================================
        // ATUALIZAR CÂMERA E HUD (usado em JOGANDO e BOSS)
        // ==========================================================

        private void atualizarCameraEHud() {

                Vector3f posicao = player.getPosition();

                cam.setLocation(new Vector3f(
                                posicao.x,
                                posicao.y + 8,
                                posicao.z + 12));

                cam.lookAt(posicao, Vector3f.UNIT_Y);

                textoVida.setText("VIDA: " + player.getVida() + "/" + player.getVidaMax());

                textoNivel.setText("NÍVEL: " + player.getNivel());

                textoXP.setText("XP: " + player.getXp() + " / " + player.getXpNecessario());

                TextoEscudo.setText("ESCUDOS: " + player.getEscudos());

                barraVida.atualizar(player.getVida(), player.getVidaMax());
        }

        // ==========================================================
        // GAME OVER
        // ==========================================================

        private void criarGameOver(BitmapFont fonte) {

                gameOverNode = new Node("GameOver");
                guiNode.attachChild(gameOverNode);

                // FUNDO
                fundoGameOver = new Geometry(
                                "FundoGameOver",
                                new Quad(cam.getWidth(), cam.getHeight()));

                Material materialFundo = new Material(
                                assetManager,
                                "Common/MatDefs/Misc/Unshaded.j3md");

                materialFundo.setColor("Color", new ColorRGBA(0f, 0f, 0f, 0.75f));

                materialFundo.getAdditionalRenderState()
                                .setBlendMode(RenderState.BlendMode.Alpha);

                fundoGameOver.setMaterial(materialFundo);
                gameOverNode.attachChild(fundoGameOver);

                // TÍTULO
                tituloGameOver = new BitmapText(fonte);
                tituloGameOver.setSize(72);
                tituloGameOver.setColor(ColorRGBA.Red);
                tituloGameOver.setText("GAME OVER");
                centralizarTexto(tituloGameOver, cam.getWidth() / 2f, 500);
                gameOverNode.attachChild(tituloGameOver);

                // MENSAGEM
                textoGameOver = new BitmapText(fonte);
                textoGameOver.setSize(30);
                textoGameOver.setColor(ColorRGBA.White);
                textoGameOver.setText("Sua nave foi destruída!");
                centralizarTexto(textoGameOver, cam.getWidth() / 2f, 430);
                gameOverNode.attachChild(textoGameOver);

                // BOTÃO JOGAR NOVAMENTE
                botaoJogarNovamente = criarBotao(350, 70, (cam.getWidth() - 350) / 2f, 320);
                gameOverNode.attachChild(botaoJogarNovamente);

                textoBotaoJogarNovamente = criarTextoBotao(fonte, "JOGAR NOVAMENTE", botaoJogarNovamente);
                gameOverNode.attachChild(textoBotaoJogarNovamente);

                // BOTÃO VOLTAR
                botaoVoltarMenu = criarBotao(350, 70, (cam.getWidth() - 350) / 2f, 220);
                gameOverNode.attachChild(botaoVoltarMenu);

                textoBotaoVoltarMenu = criarTextoBotao(fonte, "VOLTAR AO MENU", botaoVoltarMenu);
                gameOverNode.attachChild(textoBotaoVoltarMenu);
        }

        // ==========================================================
        // CRIAR BOTÃO DO GAME OVER
        // ==========================================================

        private Geometry criarBotao(float largura, float altura, float x, float y) {

                Geometry botao = new Geometry("Botao", new Quad(largura, altura));

                Material material = new Material(
                                assetManager,
                                "Common/MatDefs/Misc/Unshaded.j3md");

                material.setColor("Color", ColorRGBA.DarkGray);

                botao.setMaterial(material);
                botao.setLocalTranslation(x, y, 2);

                return botao;
        }

        // ==========================================================
        // TEXTO DO BOTÃO
        // ==========================================================

        private BitmapText criarTextoBotao(BitmapFont fonte, String texto, Geometry botao) {

                BitmapText textoBotao = new BitmapText(fonte);

                textoBotao.setSize(28);
                textoBotao.setColor(ColorRGBA.White);
                textoBotao.setText(texto);

                float larguraBotao = 350f;
                float alturaBotao = 70f;

                float larguraTexto = textoBotao.getLineWidth();

                float x = botao.getLocalTranslation().x + (larguraBotao - larguraTexto) / 2f;

                float y = botao.getLocalTranslation().y + (alturaBotao / 2f) + 10f;

                textoBotao.setLocalTranslation(x, y, 3);

                return textoBotao;
        }

        // ==========================================================
        // CENTRALIZAR TEXTO
        // ==========================================================

        private void centralizarTexto(BitmapText texto, float centroX, float posicaoY) {

                float largura = texto.getLineWidth();

                texto.setLocalTranslation(centroX - largura / 2f, posicaoY, 3);
        }

        // ==========================================================
        // UPGRADES
        // ==========================================================
        private void criarTelaUpgrade(BitmapFont fonte) {

                upgradeNode = new Node("TelaUpgrade");
                guiNode.attachChild(upgradeNode);

                // FUNDO
                fundoUpgrade = new Geometry(
                                "FundoUpgrade",
                                new Quad(cam.getWidth(), cam.getHeight()));

                Material material = new Material(
                                assetManager,
                                "Common/MatDefs/Misc/Unshaded.j3md");

                material.setColor("Color", new ColorRGBA(0f, 0f, 0f, 0.85f));

                material.getAdditionalRenderState()
                                .setBlendMode(RenderState.BlendMode.Alpha);

                fundoUpgrade.setMaterial(material);
                upgradeNode.attachChild(fundoUpgrade);

                // TÍTULO
                tituloUpgrade = new BitmapText(fonte);
                tituloUpgrade.setSize(45);
                tituloUpgrade.setColor(ColorRGBA.Yellow);
                tituloUpgrade.setText("BOSS DERROTADO!");
                centralizarTexto(tituloUpgrade, cam.getWidth() / 2f, 650);
                upgradeNode.attachChild(tituloUpgrade);

                // OPÇÕES
                String[] nomes = {
                                "1 - VIDA MÁXIMA +20",
                                "2 - REGENERAÇÃO +30",
                                "3 - ESCUDO +1",
                                "4 - VELOCIDADE +1",
                                "5 - MULTI-DISPARO"
                };

                for (int i = 0; i < 5; i++) {

                        float largura = 600f;
                        float altura = 60f;

                        float x = (cam.getWidth() - largura) / 2f;

                        float y = 500f - (i * 80f);

                        botoesUpgrade[i] = criarBotaoCustomizado(largura, altura, x, y);

                        upgradeNode.attachChild(botoesUpgrade[i]);

                        textosUpgrade[i] = new BitmapText(fonte);
                        textosUpgrade[i].setSize(25);
                        textosUpgrade[i].setColor(ColorRGBA.White);
                        textosUpgrade[i].setText(nomes[i]);

                        centralizarTexto(textosUpgrade[i], cam.getWidth() / 2f, y + 40);

                        upgradeNode.attachChild(textosUpgrade[i]);
                }

                upgradeNode.setCullHint(Node.CullHint.Always);
        }

        // ==========================================================
        // CRIAR BOTÃO CUSTOMIZADO
        // ==========================================================
        private Geometry criarBotaoCustomizado(float largura, float altura, float x, float y) {

                Geometry botao = new Geometry("BotaoUpgrade", new Quad(largura, altura));

                Material material = new Material(
                                assetManager,
                                "Common/MatDefs/Misc/Unshaded.j3md");

                material.setColor("Color", ColorRGBA.DarkGray);

                botao.setMaterial(material);
                botao.setLocalTranslation(x, y, 0);

                return botao;
        }

        // ==========================================================
        // INICIAR TELA DE UPGRADE
        // ==========================================================
        private void iniciarEscolhaUpgrade() {

                estadoAtual = EstadoJogo.ESCOLHA_UPGRADE;

                upgradeNode.setCullHint(Node.CullHint.Never);

                player.getNode().setCullHint(Node.CullHint.Never);

                textoVida.setCullHint(BitmapText.CullHint.Never);
                barraVida.getNode().setCullHint(Node.CullHint.Never);
                textoScore.setCullHint(BitmapText.CullHint.Never);
                textoNivel.setCullHint(BitmapText.CullHint.Never);
                textoXP.setCullHint(BitmapText.CullHint.Never);
                TextoEscudo.setCullHint(BitmapText.CullHint.Never);

                inputManager.setCursorVisible(true);

                System.out.println("ESCOLHA SEU UPGRADE!");
        }

        // ==========================================================
        // CLIQUE
        // ==========================================================

        private void processarClique() {

                Vector2f cursor = inputManager.getCursorPosition();

                float mouseX = cursor.x;
                float mouseY = cursor.y;

                // MENU
                if (estadoAtual == EstadoJogo.MENU) {

                        if (mouseDentro(mouseX, mouseY, menu.getAreaBotaoJogar())) {
                                audioManager.tocarClique();
                                iniciarJogo();
                                return;
                        }

                        if (mouseDentro(mouseX, mouseY, menu.getAreaBotaoSair())) {
                                audioManager.tocarClique();
                                System.out.println("SAINDO DO JOGO...");
                                stop();
                                return;
                        }
                }

                // UPGRADE
                if (estadoAtual == EstadoJogo.ESCOLHA_UPGRADE) {
                        processarCliqueUpgrade(mouseX, mouseY);
                        return;
                }

                // GAME OVER
                if (estadoAtual == EstadoJogo.GAME_OVER) {

                        if (mouseDentro(mouseX, mouseY, botaoJogarNovamente)) {
                                audioManager.tocarClique();
                                reiniciarJogo();
                                return;
                        }

                        if (mouseDentro(mouseX, mouseY, botaoVoltarMenu)) {
                                audioManager.tocarClique();
                                voltarAoMenu();
                        }
                }
        }

        // ==========================================================
        // CLIQUE EM TEXTO
        // ==========================================================

        private boolean mouseDentroTexto(float mouseX, float mouseY, BitmapText texto) {

                float x = texto.getLocalTranslation().x;
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
        // CORRIGIDO: usa o tamanho real do Quad (antes era sempre 350x70,
        // o que quebrava os botões de upgrade, que são 600x60)
        // ==========================================================

        private boolean mouseDentro(float mouseX, float mouseY, Geometry botao) {

                float x = botao.getLocalTranslation().x;
                float y = botao.getLocalTranslation().y;

                float largura = 350f;
                float altura = 70f;

                if (botao.getMesh() instanceof Quad) {
                        Quad quad = (Quad) botao.getMesh();
                        largura = quad.getWidth();
                        altura = quad.getHeight();
                }

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

                tempoSpawn = 0f;

                menuNode.setCullHint(Node.CullHint.Always);

                menu.getNode().setCullHint(Node.CullHint.Always);
                gameOverNode.setCullHint(Node.CullHint.Always);

                player.getNode().setCullHint(Node.CullHint.Never);
                starField.getNode().setCullHint(Node.CullHint.Never);

                textoVida.setCullHint(BitmapText.CullHint.Never);
                barraVida.getNode().setCullHint(Node.CullHint.Never);

                criarInimigo();

                inputManager.setCursorVisible(false);

                System.out.println("JOGO INICIADO!");
        }

        // ==========================================================
        // MORTE
        // ==========================================================

        private void jogadorMorreu() {

                audioManager.tocarGameOver();

                estadoAtual = EstadoJogo.GAME_OVER;

                System.out.println("PLAYER FOI DE F!");

                player.getNode().setCullHint(Node.CullHint.Always);

                textoVida.setCullHint(BitmapText.CullHint.Always);
                barraVida.getNode().setCullHint(Node.CullHint.Always);

                gameOverNode.setCullHint(Node.CullHint.Never);

                inputManager.setCursorVisible(true);
        }

        // ==========================================================
        // ATIRAR
        // ==========================================================
        private void atirar() {

                audioManager.tocarTiro();

                Vector3f posicaoJogador = player.getNode().getWorldTranslation().clone();

                int nivelDisparo = player.getNivelMultiDisparo();

                // FRENTE
                criarProjetil(posicaoJogador, Vector3f.UNIT_Z.negate());

                // NÍVEL 1: FRENTE + TRÁS
                if (nivelDisparo >= 1) {
                        criarProjetil(posicaoJogador, Vector3f.UNIT_Z);
                }

                // NÍVEL 2: FRENTE + TRÁS + ESQUERDA + DIREITA
                if (nivelDisparo >= 2) {
                        criarProjetil(posicaoJogador, Vector3f.UNIT_X.negate());
                        criarProjetil(posicaoJogador, Vector3f.UNIT_X);
                }

                System.out.println("Player atirou! Nível de disparo: " + nivelDisparo);
        }

        // ==========================================================
        // CRIAR PROJÉTIL
        // ==========================================================
        private void criarProjetil(Vector3f posicaoJogador, Vector3f direcao) {

                Vector3f posicaoInicial = posicaoJogador.clone().add(direcao.mult(2.5f));

                Projetil projetil = new Projetil(assetManager, posicaoInicial, direcao);

                projeteis.add(projetil);

                rootNode.attachChild(projetil.getNode());
        }

        // ==========================================================
        // APLICAR UPGRADES
        // ==========================================================
        private void aplicarUpgrade(TipoUpgrade tipo) {

                audioManager.tocarUpgrade();

                switch (tipo) {

                        case VIDA_MAXIMA:
                                player.aumentarVidaMaxima(20);
                                break;

                        case REGENERACAO:
                                player.aumentarRegeneracao();
                                break;

                        case ESCUDO:
                                player.adicionarEscudo();
                                break;

                        case VELOCIDADE:
                                player.aumentarVelocidade(1f);
                                break;

                        case MULTI_DISPARO:
                                player.aumentarNivelMultiDisparo();
                                break;
                }
        }

        // ==========================================================
        // CRIAR INIMIGO
        // ==========================================================

        private void criarInimigo() {

                Vector3f posicaoJogador = player.getNode().getWorldTranslation();

                Vector3f posicaoInicial;

                int lado = (int) (Math.random() * 4);

                float distanciaNascimento = 30f;

                switch (lado) {
                        // Frente
                        case 0:
                                posicaoInicial = posicaoJogador.clone().add(
                                                (float) (Math.random() * 20f - 10f),
                                                0f,
                                                distanciaNascimento);
                                break;

                        // Trás
                        case 1:
                                posicaoInicial = posicaoJogador.clone().add(
                                                (float) (Math.random() * 20f - 10f),
                                                0f,
                                                -distanciaNascimento);
                                break;

                        // Esquerda
                        case 2:
                                posicaoInicial = posicaoJogador.clone().add(
                                                -distanciaNascimento,
                                                0f,
                                                (float) (Math.random() * 20f - 10f));
                                break;

                        // Direita
                        default:
                                posicaoInicial = posicaoJogador.clone().add(
                                                distanciaNascimento,
                                                0f,
                                                (float) (Math.random() * 20f - 10f));
                                break;
                }

                TipoAsteroide tipo = sortearTipoAsteroide();

                Inimigo inimigo = new Inimigo(
                                assetManager,
                                posicaoInicial,
                                posicaoJogador.clone(),
                                tipo);

                inimigos.add(inimigo);

                rootNode.attachChild(inimigo.getNode());
        }

        // ==========================================================
        // INICIAR BOSS
        // ==========================================================
        private void iniciarBoss() {

                audioManager.tocarBossInicio();

                System.out.println("================================");
                System.out.println("ENTRANDO EM BOSS!");
                System.out.println("NÍVEL: " + player.getNivel());
                System.out.println("================================");

                estadoAtual = EstadoJogo.BOSS;

                tempoSpawn = 0f;

                // Remove os meteoros normais
                limparInimigos();

                // Remove projéteis antigos
                limparProjeteis();

                gerenciadorBoss.iniciarBoss();

                inputManager.setCursorVisible(false);
        }

        // ==========================================================
        // SORTEIO DE TIPO DE ASTEROIDE
        // ==========================================================
        private TipoAsteroide sortearTipoAsteroide() {

                double sorteio = Math.random();

                if (sorteio < 0.50) {
                        return TipoAsteroide.PEQUENO;
                }

                if (sorteio < 0.80) {
                        return TipoAsteroide.MEDIO;
                } else {
                        return TipoAsteroide.GRANDE;
                }
        }

        // ==========================================================
        // CRIAÇÃO DE ASTEROIDES
        // ==========================================================
        private void criarAsteroide(
                        TipoAsteroide tipo,
                        Vector3f posicaoInicial,
                        Vector3f posicaoAlvo) {

                Inimigo asteroide = new Inimigo(assetManager, posicaoInicial, posicaoAlvo, tipo);

                inimigos.add(asteroide);

                rootNode.attachChild(asteroide.getNode());
        }

        // ==========================================================
        // CRIAR FRAGMENTOS
        // ==========================================================
        private void criarFragmentos(TipoAsteroide tipoDestruido, Vector3f posicao) {

                TipoAsteroide tipoFragmento;

                switch (tipoDestruido) {
                        case MEDIO:
                                tipoFragmento = TipoAsteroide.PEQUENO;
                                break;

                        case GRANDE:
                                tipoFragmento = TipoAsteroide.MEDIO;
                                break;

                        case PEQUENO:
                        default:
                                return;
                }

                Vector3f[] deslocamentos = {
                                new Vector3f(1.5f, 0, 0),
                                new Vector3f(-1.5f, 0f, 0f),
                                new Vector3f(0f, 1.5f, 0),
                                new Vector3f(0f, -1.5f, 0)
                };

                for (Vector3f deslocamento : deslocamentos) {

                        Vector3f posicaoFragmento = posicao.clone().add(deslocamento);

                        criarAsteroide(
                                        tipoFragmento,
                                        posicaoFragmento,
                                        player.getNode().getWorldTranslation().clone());
                }
        }

        // ==========================================================
        // COLISÃO
        // ==========================================================

        private void verificarColisoesComInimigos() {

                Vector3f posicaoJogador = player.getNode().getWorldTranslation();

                for (int i = inimigos.size() - 1; i >= 0; i--) {

                        Inimigo inimigo = inimigos.get(i);

                        Vector3f posicaoInimigo = inimigo.getNode().getWorldTranslation();

                        float distanciaX = Math.abs(posicaoJogador.x - posicaoInimigo.x);
                        float distanciaY = Math.abs(posicaoJogador.y - posicaoInimigo.y);
                        float distanciaZ = Math.abs(posicaoJogador.z - posicaoInimigo.z);

                        /*
                         * Área aproximada da nave:
                         * X = Largura
                         * Y = Altura
                         * Z = Comprimento
                         *
                         * O tamanho do meteoro também é considerado
                         */

                        boolean colidiu = distanciaX <= 3.0f && distanciaY <= 1.2f && distanciaZ <= 2.5f;

                        if (colidiu) {

                                int dano = 0;

                                switch (inimigo.getTipo()) {
                                        case PEQUENO:
                                                dano = 10;
                                                break;

                                        case MEDIO:
                                                dano = 20;
                                                break;

                                        case GRANDE:
                                                dano = 30;
                                                break;
                                }

                                player.receberDano(dano);
                                audioManager.tocarDano();

                                rootNode.detachChild(inimigo.getNode());
                                inimigos.remove(i);

                                System.out.println("O jogador foi atingido por um " + inimigo.getTipo()
                                                + " causando " + dano + " de dano.");

                                if (player.getVida() <= 0) {
                                        jogadorMorreu();
                                }
                        }
                }
        }

        // ==========================================================
        // COLISÃO ENTRE PROJÉTEIS E INIMIGOS
        // ==========================================================
        private void verificarColisoesProjetilInimigo() {

                for (int p = projeteis.size() - 1; p >= 0; p--) {

                        Projetil projetil = projeteis.get(p);

                        Vector3f posicaoProjetil = projetil.getNode().getWorldTranslation();

                        boolean projetilAcertou = false;
                        boolean inimigoMorreu = false;
                        TipoAsteroide tipoMorto = null;

                        for (int i = inimigos.size() - 1; i >= 0; i--) {

                                Inimigo inimigo = inimigos.get(i);

                                Vector3f posicaoInimigo = inimigo.getNode().getWorldTranslation();

                                if (posicaoProjetil.distance(posicaoInimigo) <= 1.2f) {

                                        inimigo.receberDano(30);

                                        System.out.println("METEORO ATINGIDO");

                                        projetilAcertou = true;

                                        if (!inimigo.estaVivo()) {

                                                Vector3f posicaoDestruicao = inimigo.getPosicao().clone();

                                                tipoMorto = inimigo.getTipo();
                                                inimigoMorreu = true;

                                                rootNode.detachChild(inimigo.getNode());
                                                inimigos.remove(i);

                                                audioManager.tocarExplosao();

                                                criarFragmentos(tipoMorto, posicaoDestruicao);

                                                System.out.println("METEORO " + tipoMorto + " DESTRUÍDO");
                                        }

                                        break;
                                }
                        }

                        if (projetilAcertou) {
                                rootNode.detachChild(projetil.getNode());
                                projeteis.remove(p);
                        }

                        // Só aqui, depois de terminar de mexer nas listas
                        if (inimigoMorreu) {

                                adicionarScore(tipoMorto);

                                // Se o score iniciou o boss, as listas foram limpas: para tudo
                                if (estadoAtual != EstadoJogo.JOGANDO) {
                                        return;
                                }
                        }
                }
        }

        // ==========================================================
        // LIMPAR PROJÉTEIS DISTANTES
        // ==========================================================
        private void limparProjeteisDistantes() {

                Vector3f posicaoJogador = player.getNode().getWorldTranslation();

                for (int i = projeteis.size() - 1; i >= 0; i--) {

                        Projetil projetil = projeteis.get(i);

                        float distancia = posicaoJogador.distance(projetil.getPosicao());

                        if (distancia > 100f) {
                                rootNode.detachChild(projetil.getNode());
                                projeteis.remove(i);
                        }
                }
        }

        // ==========================================================
        // LIMPAR INIMIGOS
        // ==========================================================

        private void limparInimigos() {

                for (Inimigo inimigo : inimigos) {
                        rootNode.detachChild(inimigo.getNode());
                }

                inimigos.clear();
        }

        // ==========================================================
        // LIMPAR PROJÉTEIS
        // ==========================================================
        private void limparProjeteis() {

                for (Projetil projetil : projeteis) {
                        rootNode.detachChild(projetil.getNode());
                }

                projeteis.clear();
        }

        // ==========================================================
        // REINICIAR
        // ==========================================================

        private void reiniciarJogo() {

                score = 0;
                textoScore.setText("SCORE: " + score);

                limparInimigos();
                limparProjeteis();
                gerenciadorBoss.limpar();

                upgradeNode.setCullHint(Node.CullHint.Always);

                tempoSpawn = 0f;

                player.resetarProgresso();

                player.setPosition(new Vector3f(0, 0, 0));

                barraVida.atualizar(player.getVida(), player.getVidaMax());

                estadoAtual = EstadoJogo.JOGANDO;

                gameOverNode.setCullHint(Node.CullHint.Always);

                player.getNode().setCullHint(Node.CullHint.Never);

                textoVida.setCullHint(BitmapText.CullHint.Never);
                barraVida.getNode().setCullHint(Node.CullHint.Never);

                criarInimigo();

                inputManager.setCursorVisible(false);

                System.out.println("JOGO REINICIADO!");
        }

        // ==========================================================
        // VOLTAR AO MENU
        // ==========================================================

        private void voltarAoMenu() {

                limparInimigos();
                limparProjeteis();
                gerenciadorBoss.limpar();

                upgradeNode.setCullHint(Node.CullHint.Always);

                tempoSpawn = 0f;

                estadoAtual = EstadoJogo.MENU;

                gameOverNode.setCullHint(Node.CullHint.Always);

                if (menuNode.getParent() == null) {
                        guiNode.attachChild(menuNode);
                }

                menuNode.setCullHint(Node.CullHint.Never);

                player.getNode().setCullHint(Node.CullHint.Always);
                starField.getNode().setCullHint(Node.CullHint.Always);

                textoVida.setCullHint(BitmapText.CullHint.Always);
                barraVida.getNode().setCullHint(Node.CullHint.Always);

                inputManager.setCursorVisible(true);

                System.out.println("VOLTANDO AO MENU...");
        }

        // ==========================================================
        // MOSTRAR MENU
        // ==========================================================

        private void mostrarMenu() {

                estadoAtual = EstadoJogo.MENU;

                menu.getNode().setCullHint(Node.CullHint.Never);
                gameOverNode.setCullHint(Node.CullHint.Always);

                player.getNode().setCullHint(Node.CullHint.Always);
                starField.getNode().setCullHint(Node.CullHint.Always);

                textoVida.setCullHint(BitmapText.CullHint.Always);
                barraVida.getNode().setCullHint(Node.CullHint.Always);

                inputManager.setCursorVisible(true);
        }

        // ==========================================================
        // ATUALIZAR BOSS
        // ==========================================================
        private void atualizarBoss(float tpf) {

                player.atualizarRegeneração(tpf);
                player.atualizarEscudo(tpf);

                gerenciadorBoss.atualizar(tpf);

                gerenciadorBoss.verificarColisaoPlayer();

                // Projéteis precisam se mover durante o boss
                for (Projetil projetil : projeteis) {
                        projetil.atualizar(tpf);
                }

                for (int i = projeteis.size() - 1; i >= 0; i--) {

                        Projetil projetil = projeteis.get(i);

                        boolean acertou = gerenciadorBoss.receberDano(projetil.getPosicao(), 30);

                        if (acertou) {
                                rootNode.detachChild(projetil.getNode());
                                projeteis.remove(i);
                        }
                }

                limparProjeteisDistantes();

                atualizarCameraEHud();

                if (!player.estaVivo()) {
                        jogadorMorreu();
                        return;
                }

                if (gerenciadorBoss.acabouAgora()) {
                        audioManager.tocarBossMorte();

                        limparProjeteis();
                        iniciarEscolhaUpgrade();
                }
        }

        // ==========================================================
        // UPDATE
        // ==========================================================

        @Override
        public void simpleUpdate(float tpf) {

                if (estadoAtual == EstadoJogo.BOSS) {
                        atualizarBoss(tpf);
                        return;
                }

                if (estadoAtual != EstadoJogo.JOGANDO) {
                        return;
                }

                // SPAWN CONTÍNUO
                tempoSpawn += tpf;

                if (tempoSpawn >= intervaloSpawn) {
                        criarInimigo();
                        tempoSpawn = 0f;
                }

                // ATUALIZAR INIMIGOS
                for (Inimigo inimigo : inimigos) {
                        inimigo.atualizar(tpf);
                }

                // ATUALIZAR PROJÉTEIS
                for (Projetil projetil : projeteis) {
                        projetil.atualizar(tpf);
                }

                verificarColisoesComInimigos();

                // Pode ter morrido na colisão
                if (estadoAtual != EstadoJogo.JOGANDO) {
                        return;
                }

                verificarColisoesProjetilInimigo();

                // Pode ter entrado no boss na colisão de projéteis
                if (estadoAtual != EstadoJogo.JOGANDO) {
                        return;
                }

                player.atualizarRegeneração(tpf);
                player.atualizarEscudo(tpf);

                limparProjeteisDistantes();

                atualizarCameraEHud();

                // MORTE
                if (!player.estaVivo()) {
                        jogadorMorreu();
                }
        }
}