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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

        // Intervalo inicial entre os spans de asteroides
        private final float intervaloSpawnInicial = 2.0f;

        // Redução do intervalo a cada 10 níveis
        private final float reducaoSpawnPorMarco = 0.2f;

        // Intervalo mínimo entre os spawns
        private final float intervaloSpawnMinimo = 0.5f;

        // ==========================================================
        // PROJÉTEIS
        // ==========================================================
        private final List<Projetil> projeteis = new ArrayList<>();
        private final List<Projetil> projeteisRefletidos = new ArrayList<>();

        // ==========================================================
        // HUD
        // ==========================================================

        private BitmapText textoVida;
        private BarraVida barraVida;
        private BarraVida barraVidaBoss;
        private int score = 0;
        private BitmapText textoScore;
        private BitmapText textoNivel;
        private BitmapText textoXP;
        private BitmapText TextoEscudo;

        private TelaPause telaPause;

        private String nickJogador = "Jogador";
        private final RankingManager rankingManager = new RankingManager();

        private BitmapText textoRanking;
        private boolean recordeSalvoNaPartida = false;

        private EstadoJogo estadoAntesPause = EstadoJogo.JOGANDO;

        private static final String PAUSAR = "Pausar";

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
                GAME_OVER,
                PAUSADO
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
        private static final String UPGRADE_1 = "Upgrade1";
        private static final String UPGRADE_2 = "Upgrade2";
        private static final String UPGRADE_3 = "Upgrade3";
        private static final String UPGRADE_4 = "Upgrade4";
        private static final String UPGRADE_5 = "Upgrade5";

        // ==========================================================
        // AUXILIAR: o jogador pode agir (andar/atirar)?
        // ==========================================================

        private boolean jogadorPodeAgir() {
                return estadoAtual == EstadoJogo.JOGANDO || estadoAtual == EstadoJogo.BOSS;
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

                        // PAUSAR
                        if (name.equals(PAUSAR)) {
                                if (estadoAtual == EstadoJogo.JOGANDO || estadoAtual == EstadoJogo.BOSS) {
                                        pausarJogo();
                                } else if (estadoAtual == EstadoJogo.PAUSADO) {
                                        continuarJogo();
                                }
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

                        // ESCOLHA DOS UPGRADES
                        if (estadoAtual == EstadoJogo.ESCOLHA_UPGRADE) {
                                switch (name) {
                                        case UPGRADE_1:
                                                selecionarUpgrade(0);
                                                return;

                                        case UPGRADE_2:
                                                selecionarUpgrade(1);
                                                return;

                                        case UPGRADE_3:
                                                selecionarUpgrade(2);
                                                return;

                                        case UPGRADE_4:
                                                selecionarUpgrade(3);
                                                return;

                                        case UPGRADE_5:
                                                selecionarUpgrade(4);
                                                return;

                                        default:
                                                break;
                                }
                        }
                }
        };

        // ==========================================================
        // PAUSE
        // ==========================================================
        private void pausarJogo() {
                estadoAntesPause = estadoAtual;
                estadoAtual = EstadoJogo.PAUSADO;

                telaPause.mostrar();
                inputManager.setCursorVisible(true);
        }

        private void continuarJogo() {
                telaPause.esconder();
                estadoAtual = estadoAntesPause;

                inputManager.setCursorVisible(
                                estadoAtual != EstadoJogo.JOGANDO && estadoAtual != EstadoJogo.BOSS);
        }

        // ==========================================================
        // PROCESSA UPGRADE
        // ==========================================================
        private void selecionarUpgrade(int indice) {
                if (estadoAtual != EstadoJogo.ESCOLHA_UPGRADE) {
                        return;
                }

                if (indice < 0 || indice > 5) {
                        return;
                }

                TipoUpgrade upgrade;

                switch (indice) {
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
                                break;
                }

                if (upgrade == TipoUpgrade.MULTI_DISPARO && player.getNivelMultiDisparo() >= 4) {
                        System.out.println("NÍVEL MÁXIMO DE MULTI_DISPARO ATINGIDO!");
                        return;
                }

                audioManager.tocarUpgrade();

                aplicarUpgrade(upgrade);
                finalizarEscolhaUpgrade();
        }

        private void atualizarBotaoMultiDisparo() {
                boolean nivelMaximo = player.getNivelMultiDisparo() >= 4;

                if(nivelMaximo) {
                        botoesUpgrade[4].getMaterial().setColor("Color", ColorRGBA.Gray);
                        textosUpgrade[4].setText("5 - MULTI-DISPARO (NÍVEL MÁXIMO)");
                        textosUpgrade[4].setSize(25);
                        textosUpgrade[4].setColor(ColorRGBA.DarkGray);
                } else {
                        botoesUpgrade[4].getMaterial().setColor("Color", ColorRGBA.DarkGray);
                        textosUpgrade[4].setText("5 - MULTI-DISPARO");
                        textosUpgrade[4].setSize(25);
                        textosUpgrade[4].setColor(ColorRGBA.White);
                }

                centralizarTexto(textosUpgrade[4], cam.getWidth() / 2f, 220f);
        }

        // ==========================================================
        // FINALIZAR ESCOLHA DE UPGRADE
        // ==========================================================
        private void finalizarEscolhaUpgrade() {

                upgradeNode.setCullHint(Node.CullHint.Always);

                estadoAtual = EstadoJogo.JOGANDO;

                tempoSpawn = 0f;

                inputManager.setCursorVisible(true);

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

                String nick;

                while (true) {
                        nick = JOptionPane.showInputDialog(
                                        null,
                                        "Digite seu nick para jogar:",
                                        "Astro Survivor",
                                        JOptionPane.QUESTION_MESSAGE);

                        if (nick == null) {
                                return;
                        }

                        nick = nick.trim();

                        if (!nick.isBlank()) {
                                break;
                        }

                        JOptionPane.showMessageDialog(
                                        null,
                                        "Digite um nick válido.",
                                        "Nick inválido",
                                        JOptionPane.WARNING_MESSAGE);
                }

                Main jogo = new Main();
                jogo.nickJogador = nick;

                AppSettings configuracoes = new AppSettings(true);
                configuracoes.setTitle("Astro Survivor - " + nick);
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
                BitmapFont fonte = assetManager.loadFont("Interface/Fonts/Orbitron.fnt");

                criarHUD(fonte);
                criarGameOver(fonte);
                criarTelaUpgrade(fonte);

                // MENU
                menu = new Menu(this);
                menuNode = menu.getNode();
                guiNode.attachChild(menuNode);
                mostrarMenu();

                // PAUSE
                telaPause = new TelaPause(
                                assetManager,
                                fonte,
                                cam.getWidth(),
                                cam.getHeight());

                guiNode.attachChild(telaPause.getNode());
        }

        // ==========================================================
        // CONTROLES
        // ==========================================================

        private void configurarControles() {

                inputManager.deleteMapping("SIMPLEAPP_Exit");

                inputManager.addMapping(MOVIMENTO_FRENTE, new KeyTrigger(KeyInput.KEY_W));
                inputManager.addMapping(MOVIMENTO_TRAS, new KeyTrigger(KeyInput.KEY_S));
                inputManager.addMapping(MOVIMENTO_ESQUERDA, new KeyTrigger(KeyInput.KEY_A));
                inputManager.addMapping(MOVIMENTO_DIREITA, new KeyTrigger(KeyInput.KEY_D));
                inputManager.addMapping(TESTE_DANO, new KeyTrigger(KeyInput.KEY_SPACE));
                inputManager.addMapping(CLIQUE_MOUSE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

                inputManager.addMapping(UPGRADE_1, new KeyTrigger(KeyInput.KEY_1));
                inputManager.addMapping(UPGRADE_2, new KeyTrigger(KeyInput.KEY_2));
                inputManager.addMapping(UPGRADE_3, new KeyTrigger(KeyInput.KEY_3));
                inputManager.addMapping(UPGRADE_4, new KeyTrigger(KeyInput.KEY_4));
                inputManager.addMapping(UPGRADE_5, new KeyTrigger(KeyInput.KEY_5));
                inputManager.addMapping(PAUSAR, new KeyTrigger(KeyInput.KEY_ESCAPE));

                inputManager.addListener(
                                analogListener,
                                MOVIMENTO_FRENTE,
                                MOVIMENTO_TRAS,
                                MOVIMENTO_ESQUERDA,
                                MOVIMENTO_DIREITA);

                inputManager.addListener(
                                actionListener,
                                TESTE_DANO,
                                CLIQUE_MOUSE,
                                UPGRADE_1,
                                UPGRADE_2,
                                UPGRADE_3,
                                UPGRADE_4,
                                UPGRADE_5,
                                PAUSAR);
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

                // BARRA DE VIDA DO BOSS
                barraVidaBoss = new BarraVida(assetManager, (cam.getWidth() - 300f) / 2f, 30f);
                guiNode.attachChild(barraVidaBoss.getNode());
                barraVidaBoss.getNode().setCullHint(Node.CullHint.Always);

                // RANKING
                textoRanking = new BitmapText(fonte);
                textoRanking.setSize(16);
                textoRanking.setColor(ColorRGBA.White);
                textoRanking.setLocalTranslation(
                                cam.getWidth() - 300,
                                cam.getHeight() - 25,
                                5);
                guiNode.attachChild(textoRanking);

                atualizarRankingHUD();
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

                atualizarRankingHUD();
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
        // Atualizar HUD do Ranking
        // ==========================================================
        private void atualizarRankingHUD() {
                if (textoRanking == null) {
                        return;
                }

                textoRanking.setText(
                                "TOP 5\n" + rankingManager.obterRankingTexto(5));
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
                                "3 - ESCUDO +3",
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
                atualizarBotaoMultiDisparo();

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

                // MENU DE PAUSA
                if (estadoAtual == EstadoJogo.PAUSADO) {

                        if (mouseDentro(mouseX, mouseY, telaPause.getBotaoContinuar())) {
                                audioManager.tocarClique();
                                continuarJogo();
                                return;
                        }

                        if (mouseDentro(mouseX, mouseY, telaPause.getBotaoReiniciar())) {
                                audioManager.tocarClique();
                                reiniciarJogo();
                                return;
                        }

                        if (mouseDentro(mouseX, mouseY, telaPause.getBotaoMenu())) {
                                audioManager.tocarClique();
                                voltarAoMenu();
                                return;
                        }
                }

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

                        if (mouseDentro(mouseX, mouseY, menu.getAreaBotaoRanking())) {
                                audioManager.tocarClique();

                                String textoRanking = rankingManager.obterRankingTexto(
                                                rankingManager.obterRanking().size());

                                SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(
                                                        null,
                                                        textoRanking,
                                                        "ASTRO SURVIVOR - RANKING GERAL",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                });

                                return;
                        }

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

                recordeSalvoNaPartida = false;

                estadoAtual = EstadoJogo.JOGANDO;
                audioManager.iniciarMotorNave();

                tempoSpawn = 0f;

                menuNode.setCullHint(Node.CullHint.Always);

                menu.getNode().setCullHint(Node.CullHint.Always);
                gameOverNode.setCullHint(Node.CullHint.Always);

                player.getNode().setCullHint(Node.CullHint.Never);
                starField.getNode().setCullHint(Node.CullHint.Never);

                textoVida.setCullHint(BitmapText.CullHint.Never);
                barraVida.getNode().setCullHint(Node.CullHint.Never);

                criarInimigo();

                inputManager.setCursorVisible(true);

                System.out.println("JOGO INICIADO!");
        }

        // ==========================================================
        // MORTE
        // ==========================================================

        private void jogadorMorreu() {

                audioManager.tocarGameOver();
                audioManager.pararSonsContinuos();

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

                Vector3f posicao = player.getNode()
                                .getWorldTranslation().clone();

                Quaternion rotacao = player.getNode()
                                .getWorldRotation();

                int nivel = player.getNivelMultiDisparo();

                // Direções relativas à orientação da nave.
                Vector3f frente = rotacao.mult(
                                new Vector3f(0, 0, -1)).normalizeLocal();

                Vector3f tras = frente.negate();

                Vector3f direita = rotacao.mult(
                                new Vector3f(1, 0, 0)).normalizeLocal();

                Vector3f esquerda = direita.negate();

                // Disparo frontal.
                criarProjetil(posicao.clone(), frente.clone());

                // Nível 1: disparo para trás.
                if (nivel >= 1) {
                        criarProjetil(posicao.clone(), tras.clone());
                }

                // Nível 2: disparos laterais.
                if (nivel >= 2) {
                        criarProjetil(posicao.clone(), esquerda.clone());
                        criarProjetil(posicao.clone(), direita.clone());
                }

                // Nível 3: segundo disparo frontal, deslocado para a direita.
                if (nivel >= 3) {
                        Vector3f posicaoExtra = posicao.add(
                                        direita.mult(0.5f));

                        criarProjetil(posicaoExtra, frente.clone());
                }

                // Nível 4: terceiro disparo frontal, deslocado para a esquerda.
                if (nivel >= 4) {
                        Vector3f posicaoExtra = posicao.add(
                                        direita.mult(0.5f));

                        criarProjetil(posicaoExtra, tras.clone());
                }

                System.out.println("Multi-disparo nível: " + nivel);
        }

        private Vector3f direcaoLocalParaMundo(Vector3f direcaoLocal) {
                Vector3f direcao = player.getNode()
                                .getWorldRotation()
                                .mult(direcaoLocal);

                // Os tiros permanecem no plano horizontal.
                direcao.y = 0f;

                return direcao.normalizeLocal();
        }

        // ==========================================================
        // MOVIMENTAÇÃO DE TELA COM O MOUSE
        // ==========================================================
        private void atualizarOrientacaoNavePeloMouse() {
                if (!jogadorPodeAgir()) {
                        return;
                }

                Vector2f cursor = inputManager.getCursorPosition();

                // Cria um raio da câmera passando pela posição do mouse.
                Vector3f origemRaio = cam.getWorldCoordinates(cursor, 0f);

                Vector3f fimRaio = cam.getWorldCoordinates(cursor, 1f);

                Vector3f direcaoRaio = fimRaio.subtract(origemRaio).normalizeLocal();

                // Evita divisão por zero.
                if (Math.abs(direcaoRaio.y) < 0.0001f) {
                        return;
                }

                // Encontra onde o raio cruza o plano horizontal Y = 0.
                float distancia = -origemRaio.y / direcaoRaio.y;

                if (distancia < 0f) {
                        return;
                }

                Vector3f alvo = origemRaio.add(
                                direcaoRaio.mult(distancia));

                Vector3f posicaoNave = player.getNode().getWorldTranslation();

                alvo.y = posicaoNave.y;

                Vector3f diferenca = alvo.subtract(posicaoNave);
                diferenca.y = 0f;

                if (diferenca.lengthSquared() > 0.0001f) {
                        player.getNode().lookAt(alvo, Vector3f.UNIT_Y);
                }
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

        private void criarProjetilRefletido(
                        Vector3f posicaoOrigem,
                        Vector3f posicaoAlvo) {

                Vector3f direcao = posicaoAlvo.clone()
                                .subtractLocal(posicaoOrigem);

                if (direcao.lengthSquared() < 0.001f) {
                        return;
                }

                direcao.normalizeLocal();

                // O tiro nasce um pouco fora do asteroide.
                Vector3f posicaoInicial = posicaoOrigem.clone()
                                .addLocal(direcao.mult(2.5f));

                Projetil projetil = new Projetil(
                                assetManager,
                                posicaoInicial,
                                direcao,
                                ColorRGBA.Red);

                projeteisRefletidos.add(projetil);
                rootNode.attachChild(projetil.getNode());
        }

        private void atualizarProjeteisRefletidos(float tpf) {

                for (int i = projeteisRefletidos.size() - 1; i >= 0; i--) {

                        Projetil projetil = projeteisRefletidos.get(i);
                        projetil.atualizar(tpf);

                        Vector3f posicaoProjetil = projetil.getPosicao();
                        Vector3f posicaoNave = player.getNode().getWorldTranslation();

                        if (posicaoProjetil.distance(posicaoNave) <= 1.5f) {

                                rootNode.detachChild(projetil.getNode());
                                projeteisRefletidos.remove(i);

                                // Dano aplicado somente quando o tiro atinge a nave.
                                player.receberDano(10);

                        } else if (posicaoProjetil.distance(posicaoNave) > 60f) {

                                // Remove tiros que já passaram longe da nave.
                                rootNode.detachChild(projetil.getNode());
                                projeteisRefletidos.remove(i);
                        }
                }
        }

        private void limparProjeteisRefletidos() {

                for (Projetil projetil : projeteisRefletidos) {
                        rootNode.detachChild(projetil.getNode());
                }

                projeteisRefletidos.clear();
        }

        // ==========================================================
        // CALCULAR INTERVALO ENTRE SPAWNS
        // ==========================================================
        private float calcularIntervaloSpawn() {
                int marcosDeNivel = player.getNivel() / 10;

                float intervalo = intervaloSpawnInicial - (marcosDeNivel * reducaoSpawnPorMarco);

                return Math.max(intervaloSpawnMinimo, intervalo);
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
        // COLISAO ENTRE ASTEROIDES
        // ==========================================================

        private void verificarColisoesEntreAsteroides() {
                List<Inimigo> asteroides = new ArrayList<>(inimigos);
                List<Inimigo> remover = new ArrayList<>();
                List<Inimigo> processados = new ArrayList<>();

                List<TipoAsteroide> tiposFragmentos = new ArrayList<>();
                List<Vector3f> posicoesFragmentos = new ArrayList<>();

                for (int i = 0; i < asteroides.size(); i++) {
                        Inimigo a = asteroides.get(i);

                        if (remover.contains(a) || processados.contains(a)) {
                                continue;
                        }

                        for (int j = i + 1; j < asteroides.size(); j++) {
                                Inimigo b = asteroides.get(j);

                                if (remover.contains(b) || processados.contains(b)) {
                                        continue;
                                }

                                Vector3f posA = a.getPosicao().clone();
                                Vector3f posB = b.getPosicao().clone();

                                float raioA = obterRaioAsteroide(a.getTipo());
                                float raioB = obterRaioAsteroide(b.getTipo());

                                float distancia = posA.distance(posB);

                                if (distancia > raioA + raioB) {
                                        continue;
                                }

                                processados.add(a);
                                processados.add(b);

                                if (a.getTipo() == b.getTipo()) {
                                        refletirAsteroides(a, b, raioA, raioB);
                                } else {
                                        remover.add(a);
                                        remover.add(b);

                                        tiposFragmentos.add(a.getTipo());
                                        posicoesFragmentos.add(posA);

                                        tiposFragmentos.add(b.getTipo());
                                        posicoesFragmentos.add(posB);
                                }

                                break;
                        }
                }

                for (Inimigo asteroide : remover) {
                        rootNode.detachChild(asteroide.getNode());
                        inimigos.remove(asteroide);
                }

                for (int i = 0; i < tiposFragmentos.size(); i++) {
                        criarFragmentos(
                                        tiposFragmentos.get(i),
                                        posicoesFragmentos.get(i));
                }
        }

        private float obterRaioAsteroide(TipoAsteroide tipo) {
                switch (tipo) {
                        case PEQUENO:
                                return 0.5f;
                        case MEDIO:
                                return 1.0f;
                        case GRANDE:
                                return 1.8f;
                        default:
                                return 0.5f;
                }
        }

        // ==========================================================
        // IREFLETIR ASTEROIDES
        // ==========================================================
        private void refletirAsteroides(
                        Inimigo a,
                        Inimigo b,
                        float raioA,
                        float raioB) {

                Vector3f posA = a.getPosicao().clone();
                Vector3f posB = b.getPosicao().clone();

                Vector3f normal = posA.subtract(posB);

                if (normal.lengthSquared() < 0.0001f) {
                        normal.set(Vector3f.UNIT_X);
                } else {
                        normal.normalizeLocal();
                }

                Vector3f vA = a.getDirecao().mult(a.getVelocidade());
                Vector3f vB = b.getDirecao().mult(b.getVelocidade());

                Vector3f velocidadeRelativa = vA.subtract(vB);
                float aproximacao = velocidadeRelativa.dot(normal);

                // Só altera a trajetória se estiverem se aproximando.
                if (aproximacao < 0f) {
                        Vector3f novaVA = vA.subtract(
                                        normal.mult(aproximacao));

                        Vector3f novaVB = vB.add(
                                        normal.mult(aproximacao));

                        a.setDirecao(novaVA);
                        b.setDirecao(novaVB);
                }

                // Separa os asteroides para impedir colisões repetidas.
                float distanciaAtual = posA.distance(posB);
                float distanciaMinima = raioA + raioB + 0.05f;
                float sobreposicao = distanciaMinima - distanciaAtual;

                if (sobreposicao > 0f) {
                        Vector3f separacao = normal.mult(sobreposicao / 2f);

                        a.getNode().move(separacao);
                        b.getNode().move(separacao.negate());
                }
        }

        // ==========================================================
        // INICIAR BOSS
        // ==========================================================
        private void iniciarBoss() {

                audioManager.tocarBossInicio();
                audioManager.iniciarMusicaBoss();

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

                inputManager.setCursorVisible(true);
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

                recordeSalvoNaPartida = false;
                telaPause.esconder();

                score = 0;
                textoScore.setText("SCORE: " + score);

                limparInimigos();
                limparProjeteis();
                gerenciadorBoss.limpar();

                barraVidaBoss.getNode().setCullHint(Node.CullHint.Always);

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

                inputManager.setCursorVisible(true);

                System.out.println("JOGO REINICIADO!");
        }

        // ==========================================================
        // VOLTAR AO MENU
        // ==========================================================

        private void voltarAoMenu() {

                if (!recordeSalvoNaPartida) {
                        rankingManager.registrarResultado(
                                        nickJogador,
                                        player.getNivel(),
                                        score);

                        recordeSalvoNaPartida = true;
                }

                telaPause.esconder();

                limparInimigos();
                limparProjeteis();
                gerenciadorBoss.limpar();
                audioManager.pararSonsContinuos();

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

                audioManager.iniciarMusicaMenu();

                atualizarRankingHUD();
        }

        // ==========================================================
        // MOSTRAR MENU
        // ==========================================================

        private void mostrarMenu() {

                audioManager.iniciarMusicaMenu();
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

                score += gerenciadorBoss.consumirScorePendente();
                textoScore.setText("SCORE: " + score);

                int vidaBoss = gerenciadorBoss.getVidaBossAtual();
                int vidaBossMax = gerenciadorBoss.getVidaBossMaxima();

                if (vidaBossMax > 0) {
                        barraVidaBoss.getNode().setCullHint(Node.CullHint.Never);
                        barraVidaBoss.atualizar(vidaBoss, vidaBossMax);
                } else {
                        barraVidaBoss.getNode().setCullHint(Node.CullHint.Always);
                }

                gerenciadorBoss.verificarColisaoPlayer();

                // Projéteis precisam se mover durante o boss
                for (Projetil projetil : projeteis) {
                        projetil.atualizar(tpf);
                }

                for (int i = projeteis.size() - 1; i >= 0; i--) {

                        Projetil projetil = projeteis.get(i);

                        boolean acertou = gerenciadorBoss.receberDano(projetil.getPosicao(), 30);

                        if (acertou) {
                                Vector3f origemReflexao = gerenciadorBoss.consumirPosicaoTiroRefletido();

                                if (origemReflexao != null) {
                                        System.out.println("O BOSS REFLETIU O TIRO");

                                        Vector3f posicaoNave = player.getNode().getWorldTranslation().clone();

                                        criarProjetilRefletido(origemReflexao, posicaoNave);
                                }

                                rootNode.detachChild(projetil.getNode());
                                projeteis.remove(i);
                        }
                }

                atualizarProjeteisRefletidos(tpf);

                limparProjeteisDistantes();

                atualizarCameraEHud();

                // Atualiza a mira também durante o Boss
                if(jogadorPodeAgir()){
                        atualizarOrientacaoNavePeloMouse();
                }

                if (!player.estaVivo()) {
                        jogadorMorreu();
                        return;
                }

                if (gerenciadorBoss.acabouAgora()) {
                        audioManager.pararMusicaBoss();
                        audioManager.tocarBossMorte();

                        limparProjeteis();
                        iniciarEscolhaUpgrade();

                        audioManager.iniciarMotorNave();
                }
        }

        // ==========================================================
        // UPDATE
        // ==========================================================

        @Override
        public void simpleUpdate(float tpf) {

                if (estadoAtual != EstadoJogo.BOSS) {
                        barraVidaBoss.getNode().setCullHint(Node.CullHint.Always);

                        limparProjeteisRefletidos();
                }

                gerenciadorBoss.atualizarOndasChoque(tpf);

                if (estadoAtual == EstadoJogo.BOSS) {
                        atualizarBoss(tpf);
                        return;
                }

                if (estadoAtual != EstadoJogo.JOGANDO) {
                        return;
                }

                // SPAWN CONTÍNUO
                tempoSpawn += tpf;

                float intervaloSpawnAtual = calcularIntervaloSpawn();

                if (tempoSpawn >= intervaloSpawnAtual) {
                        criarInimigo();
                        tempoSpawn = 0f;
                }

                // ATUALIZAR INIMIGOS
                for (Inimigo inimigo : inimigos) {
                        inimigo.atualizar(tpf);
                }
                verificarColisoesEntreAsteroides();

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

                // MIRA
                if (jogadorPodeAgir()) {
                        atualizarOrientacaoNavePeloMouse();
                }
        }
}