package br.com.astrosurvivor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class GerenciadorBoss {

        private final AssetManager assetManager;
        private final Node rootNode;
        private final Player player;

        private final List<Inimigo> fragmentos = new ArrayList<>();

        private final List<AsteroideExplosivo> explosivos = new ArrayList<>();

        private final List<OndaChoque> ondasChoque = new ArrayList<>();

        private float tempoRestanteFragmentos = -1f;

        private static final float LIMITE_FRAGMENTOS = 20f;

        private float tempoProtecaoColisaoBoss = 0f;

        private int scorePendente = 0;
        private int scoreHorda = 0;

        private Boss bossAtual;

        private TipoBoss tipoBossAtual;

        private boolean bossAtivo = false;

        private boolean bossTerminou = false;

        // =========================================
        // HORDA
        // =========================================

        private float tempoHorda = 0f;

        private final float duracaoHorda = 30f;

        private float tempoSpawnHorda = 0f;

        private final float intervaloHorda = 0.35f;

        // =========================================
        // CONSTRUTOR
        // =========================================

        public GerenciadorBoss(
                        AssetManager assetManager,
                        Node rootNode,
                        Player player) {

                this.assetManager = assetManager;
                this.rootNode = rootNode;
                this.player = player;
        }

        // =========================================
        // INICIAR BOSS
        // =========================================

        public void iniciarBoss() {

                limpar();

                tipoBossAtual = sortearBoss();

                bossAtivo = true;
                bossTerminou = false;

                tempoHorda = 0f;
                tempoSpawnHorda = 0f;

                Vector3f posicaoPlayer = player.getPosition().clone();

                Vector3f posicaoInicial = posicaoPlayer
                                .clone()
                                .add(0, 0, -35f);

                // =========================================
                // HORDA
                // =========================================

                if (tipoBossAtual == TipoBoss.HORDA) {

                        System.out.println(
                                        "================================");

                        System.out.println(
                                        "BOSS: HORDA");

                        System.out.println(
                                        "SOBREVIVA POR "
                                                        + duracaoHorda
                                                        + " SEGUNDOS!");

                        System.out.println(
                                        "================================");

                        return;
                }

                // =========================================
                // BOSS EXPLOSIVO
                // =========================================

                if (tipoBossAtual == TipoBoss.METEORO_EXPLOSIVO) {

                        AsteroideExplosivo explosivo = new AsteroideExplosivo(
                                        assetManager,
                                        posicaoInicial,
                                        posicaoPlayer,
                                        true);

                        explosivos.add(explosivo);

                        rootNode.attachChild(
                                        explosivo.getNode());

                        return;
                }

                // =========================================
                // BOSS NORMAL
                // =========================================

                bossAtual = new Boss(
                                assetManager,
                                posicaoInicial,
                                posicaoPlayer,
                                tipoBossAtual);

                rootNode.attachChild(
                                bossAtual.getNode());

                System.out.println(
                                "BOSS INICIADO: "
                                                + tipoBossAtual);
        }

        // =========================================
        // SORTEAR BOSS
        // =========================================

        private TipoBoss sortearBoss() {

                TipoBoss[] bosses = TipoBoss.values();

                return bosses[new Random().nextInt(
                                bosses.length)];
        }

        // =========================================
        // UPDATE
        // =========================================

        public void atualizar(float tpf) {

                if (!bossAtivo) {
                        return;
                }

                // =========================================
                // ONDA DE CHOQUE
                // =========================================
                if (tempoProtecaoColisaoBoss > 0f) {
                        tempoProtecaoColisaoBoss -= tpf;
                }

                for (int i = ondasChoque.size() - 1; i >= 0; i--) {

                        OndaChoque onda = ondasChoque.get(i);

                        onda.atualizar(tpf, player);

                        if (onda.terminou()) {
                                rootNode.detachChild(onda.getNode());
                                ondasChoque.remove(i);
                        }
                }

                // =========================================
                // HORDA
                // =========================================

                if (tipoBossAtual == TipoBoss.HORDA) {

                        atualizarHorda(tpf);

                        return;
                }

                // =========================================
                // BOSS
                // =========================================

                if (bossAtual != null) {

                        bossAtual.atualizar(tpf);
                }

                // =========================================
                // EXPLOSIVOS
                // =========================================

                for (AsteroideExplosivo explosivo : explosivos) {

                        explosivo.atualizar(tpf);
                }

                // =========================================
                // FRAGMENTOS
                // =========================================

                for (Inimigo inimigo : fragmentos) {

                        inimigo.atualizar(tpf);
                }

                // =========================================
                // TEMPO RESTANTE FRAGMENTOS
                // =========================================
                if (tempoRestanteFragmentos >= 0f) {
                        tempoRestanteFragmentos -= tpf;

                        if (tempoRestanteFragmentos <= 0f && (!fragmentos.isEmpty() || !explosivos.isEmpty())) {
                                reiniciarBossAtual();
                                return;
                        }
                }

                // =========================================
                // VERIFICA A CONCLUSÃO DO BOSS
                // =========================================
                verificarConclusao();
        }

        // =========================================
        // HORDA
        // =========================================

        private void atualizarHorda(float tpf) {

                tempoHorda += tpf;
                tempoSpawnHorda += tpf;

                for (Inimigo inimigo : fragmentos) {
                        inimigo.atualizar(tpf);
                }

                if (tempoSpawnHorda >= intervaloHorda) {
                        criarMeteoroHorda();
                        tempoSpawnHorda = 0f;
                }

                if (tempoHorda >= duracaoHorda) {

                        System.out.println("HORDA DERROTADA!");

                        bossAtivo = false;
                        bossTerminou = true;

                        limpar();
                }
        }

        public boolean acabouAgora() {
                if (bossTerminou) {
                        bossTerminou = false;
                        return true;
                }
                return false;
        }

        // =========================================
        // CRIAR METEORO DA HORDA
        // =========================================

        private void criarMeteoroHorda() {

                Vector3f posicaoPlayer = player.getPosition();

                int lado = (int) (Math.random() * 4);

                float distancia = 30f;

                Vector3f posicao;

                switch (lado) {

                        case 0:

                                posicao = posicaoPlayer.clone().add((float) (Math.random() * 20f - 10f), 0, distancia);
                                break;

                        case 1:

                                posicao = posicaoPlayer.clone().add((float) (Math.random() * 20f - 10f), 0, -distancia);
                                break;

                        case 2:

                                posicao = posicaoPlayer.clone().add(-distancia, 0, (float) (Math.random() * 20f - 10f));
                                break;

                        default:

                                posicao = posicaoPlayer.clone().add(distancia, 0, (float) (Math.random() * 20f - 10f));
                }

                Inimigo inimigo = new Inimigo(
                                assetManager,
                                posicao,
                                posicaoPlayer.clone(),
                                TipoAsteroide.PEQUENO);

                fragmentos.add(inimigo);

                rootNode.attachChild(inimigo.getNode());
        }

        // =========================================
        // PROJÉTIL ACERTA BOSS
        // =========================================

        public boolean receberDano(
                        Vector3f posicaoProjetil,
                        int dano) {

                // =========================================
                // BOSS PRINCIPAL
                // =========================================

                if (bossAtual != null) {

                        float distancia = posicaoProjetil.distance(
                                        bossAtual.getPosicao());

                        if (distancia <= 5f) {

                                bossAtual.receberDano(
                                                dano);

                                if (!bossAtual.estaVivo()) {

                                        destruirBoss();
                                }

                                return true;
                        }
                }

                // =========================================
                // EXPLOSIVOS
                // =========================================

                for (int i = explosivos.size() - 1; i >= 0; i--) {

                        AsteroideExplosivo explosivo = explosivos.get(i);

                        float distancia = posicaoProjetil.distance(
                                        explosivo.getPosicao());

                        if (distancia <= 2f) {

                                explosivo.receberDano(
                                                dano);

                                if (!explosivo.estaVivo()) {

                                        destruirExplosivo(
                                                        explosivo,
                                                        i);
                                }

                                return true;
                        }
                }

                // =========================================
                // FRAGMENTOS
                // =========================================

                for (int i = fragmentos.size() - 1; i >= 0; i--) {

                        Inimigo inimigo = fragmentos.get(i);

                        float distancia = posicaoProjetil.distance(inimigo.getPosicao());

                        if (distancia <= 1.2f) {
                                inimigo.receberDano(dano);

                                if (!inimigo.estaVivo()) {
                                        destruirFragmento(inimigo, i);
                                }
                                return true;
                        }
                }

                return false;
        }

        // =========================================
        // DESTRUIR BOSS
        // =========================================

        private void destruirBoss() {

                Vector3f posicao = bossAtual.getPosicao().clone();

                rootNode.detachChild(bossAtual.getNode());

                tempoRestanteFragmentos = LIMITE_FRAGMENTOS;

                switch (tipoBossAtual) {

                        // =====================================
                        // GIGANTE
                        // =====================================

                        case ASTEROIDE_GIGANTE:
                                scorePendente += 1000;
                                criarGrandes(posicao);
                                break;

                        // =====================================
                        // BLINDADO
                        // =====================================

                        case ASTEROIDE_GIGANTE_BLINDADO:
                                scorePendente += 2000;
                                criar32Pequenos(posicao);
                                break;

                        default:
                                break;
                }

                bossAtual = null;

                verificarConclusao();
        }

        // =========================================
        // REINICIAR BOSS ATUAL
        // =========================================
        private void reiniciarBossAtual() {
                TipoBoss tipo = tipoBossAtual;

                limpar();

                tipoBossAtual = tipo;
                bossAtivo = true;
                bossTerminou = false;

                tempoRestanteFragmentos = -1f;
                tempoHorda = 0f;
                tempoSpawnHorda = 0f;

                Vector3f posicaoPlayer = player.getPosition().clone();
                Vector3f posicaoInicial = posicaoPlayer.clone().add(0, 0, -35f);

                if (tipo == TipoBoss.METEORO_EXPLOSIVO) {
                        AsteroideExplosivo explosivo = new AsteroideExplosivo(assetManager, posicaoInicial, posicaoPlayer, true);
                        explosivos.add(explosivo);
                        rootNode.attachChild(explosivo.getNode());
                } else if (tipo != TipoBoss.HORDA) {
                        bossAtual = new Boss(assetManager, posicaoInicial, posicaoPlayer, tipo);
                        rootNode.attachChild(bossAtual.getNode());
                }

                System.out.println("Tempo esgotado! O boss será reiniciado.");
        }

        // =========================================
        // GIGANTE → 4 GRANDES
        // =========================================

        private void criarGrandes(Vector3f centro) {

                for (int i = 0; i < 4; i++) {
                        Vector3f deslocamento = obterDeslocamento(i, 5f);
                        criarFragmento(TipoAsteroide.GRANDE, centro.clone().add(deslocamento));
                }
        }

        // =========================================
        // GRANDE → 4 MÉDIOS
        // =========================================

        private void criarMedios(
                        Vector3f centro) {

                for (int i = 0; i < 4; i++) {

                        Vector3f deslocamento = obterDeslocamento(
                                        i,
                                        3f);

                        criarFragmento(
                                        TipoAsteroide.MEDIO,
                                        centro.clone()
                                                        .add(deslocamento));
                }
        }

        // =========================================
        // MÉDIO → 4 PEQUENOS
        // =========================================

        private void criarPequenos(
                        Vector3f centro) {

                for (int i = 0; i < 4; i++) {

                        Vector3f deslocamento = obterDeslocamento(
                                        i,
                                        2f);

                        criarFragmento(
                                        TipoAsteroide.PEQUENO,
                                        centro.clone()
                                                        .add(deslocamento));
                }
        }

        // =========================================
        // BLINDADO → 32 PEQUENOS
        // =========================================

        private void criar32Pequenos(
                        Vector3f centro) {

                for (int i = 0; i < 32; i++) {

                        double angulo = Math.random()
                                        * Math.PI * 2;

                        float distancia = 2f
                                        + (float) (Math.random()
                                                        * 5f);

                        Vector3f posicao = centro.clone().add(
                                        (float) Math.cos(angulo)
                                                        * distancia,
                                        0,
                                        (float) Math.sin(angulo)
                                                        * distancia);

                        criarFragmento(
                                        TipoAsteroide.PEQUENO,
                                        posicao);
                }
        }

        // =========================================
        // CRIAR FRAGMENTO
        // =========================================

        private void criarFragmento(
                        TipoAsteroide tipo,
                        Vector3f posicao) {

                Inimigo inimigo = new Inimigo(
                                assetManager,
                                posicao,
                                player.getPosition()
                                                .clone(),
                                tipo);

                fragmentos.add(inimigo);

                rootNode.attachChild(
                                inimigo.getNode());
        }

        // =========================================
        // DESTRUIR FRAGMENTO
        // =========================================

        private void destruirFragmento(Inimigo inimigo, int indice) {

                Vector3f posicao = inimigo.getPosicao().clone();

                TipoAsteroide tipo = inimigo.getTipo();

                rootNode.detachChild(inimigo.getNode());

                fragmentos.remove(indice);

                int pontos = 0;

                switch (tipo) {

                        case PEQUENO:
                                pontos = 100;
                                break;

                        case MEDIO:
                                pontos = 200;
                                break;

                        case GRANDE:
                                pontos = 300;
                                break;
                }

                scorePendente += pontos;

                if(tipoBossAtual == TipoBoss.HORDA) {
                        scoreHorda += pontos;
                }

                // =====================================
                // FRAGMENTAÇÃO DO BOSS GIGANTE
                // =====================================

                if (tipoBossAtual == TipoBoss.ASTEROIDE_GIGANTE) {

                        if (tipo == TipoAsteroide.GRANDE) {

                                criarMedios(posicao);

                        } else if (tipo == TipoAsteroide.MEDIO) {

                                criarPequenos(posicao);
                        }
                }

                verificarConclusao();
        }

        // =========================================
        // DESTRUIR EXPLOSIVO
        // =========================================

        private void destruirExplosivo(AsteroideExplosivo explosivo, int indice) {
                Vector3f posicao = explosivo.getPosicao().clone();

                boolean eraBoss = explosivo.isBoss();

                OndaChoque onda = new OndaChoque(
                                assetManager,
                                posicao,
                                eraBoss ? 18f : 5f,
                                eraBoss ? 22f : 10f,
                                eraBoss ? 60 : 20);

                ondasChoque.add(onda);
                rootNode.attachChild(onda.getNode());

                rootNode.detachChild(explosivo.getNode());

                explosivos.remove(indice);

                // =====================================
                // BOSS EXPLODE
                // =====================================

                if (eraBoss) {
                        tempoRestanteFragmentos = LIMITE_FRAGMENTOS;
                        scorePendente += 1500;
                        for (int i = 0; i < 24; i++) {
                                double angulo = Math.random() * Math.PI * 2;

                                float distancia = 3f + (float) (Math.random() * 5f);

                                Vector3f posicaoFragmento = posicao.clone().add((float) Math.cos(angulo) * distancia, 0,
                                                (float) Math.sin(angulo) * distancia);

                                AsteroideExplosivo pequeno = new AsteroideExplosivo(
                                                assetManager,
                                                posicaoFragmento,
                                                player.getPosition().clone(),
                                                false);

                                explosivos.add(pequeno);

                                rootNode.attachChild(pequeno.getNode());
                        }
                        return;
                }

                // =====================================
                // PEQUENO EXPLODIU
                // =====================================

                verificarConclusao();
        }

        // =========================================
        // CONCLUSÃO
        // =========================================

        private void verificarConclusao() {

                if (!bossAtivo) {
                        return;
                }

                // HORDA
                if (tipoBossAtual == TipoBoss.HORDA) {

                        return;
                }

                // BOSS PRINCIPAL AINDA VIVO
                if (bossAtual != null) {
                        return;
                }

                // FRAGMENTOS RESTANTES
                if (!fragmentos.isEmpty()) {
                        return;
                }

                // EXPLOSIVOS RESTANTES
                if (!explosivos.isEmpty()) {
                        return;
                }

                bossAtivo = false;
                bossTerminou = true;

                System.out.println(
                                "================================");

                System.out.println(
                                "BOSS DERROTADO!");

                System.out.println(
                                "================================");
        }

        // =========================================
        // DESLOCAMENTO
        // =========================================

        private Vector3f obterDeslocamento(
                        int indice,
                        float distancia) {

                switch (indice) {

                        case 0:
                                return new Vector3f(
                                                distancia,
                                                0,
                                                0);

                        case 1:
                                return new Vector3f(
                                                -distancia,
                                                0,
                                                0);

                        case 2:
                                return new Vector3f(
                                                0,
                                                0,
                                                distancia);

                        default:
                                return new Vector3f(
                                                0,
                                                0,
                                                -distancia);
                }
        }

        // =========================================
        // STATUS
        // =========================================

        public boolean estaAtivo() {

                return bossAtivo;
        }

        public boolean foiDerrotado() {

                return !bossAtivo;
        }

        public TipoBoss getTipoBossAtual() {

                return tipoBossAtual;
        }

        // =========================================
        // LIMPAR
        // =========================================

        public void limpar() {

                if (bossAtual != null) {

                        rootNode.detachChild(
                                        bossAtual.getNode());

                        bossAtual = null;
                }

                for (Inimigo inimigo : fragmentos) {

                        rootNode.detachChild(
                                        inimigo.getNode());
                }

                fragmentos.clear();

                for (AsteroideExplosivo explosivo : explosivos) {

                        rootNode.detachChild(
                                        explosivo.getNode());
                }

                explosivos.clear();

                bossAtivo = false;
        }

        // =========================================
        // COLISÃO DO PLAYER
        // =========================================

        public void verificarColisaoPlayer() {

                Vector3f posicaoPlayer = player.getPosition();

                // =====================================
                // FRAGMENTOS
                // =====================================

                for (int i = fragmentos.size() - 1; i >= 0; i--) {

                        Inimigo inimigo = fragmentos.get(i);

                        if (posicaoPlayer.distance(
                                        inimigo.getPosicao()) <= 3f) {

                                int dano;

                                switch (inimigo.getTipo()) {

                                        case PEQUENO:
                                                dano = 10;
                                                break;

                                        case MEDIO:
                                                dano = 20;
                                                break;

                                        default:
                                                dano = 30;
                                }

                                player.receberDano(
                                                dano);

                                rootNode.detachChild(
                                                inimigo.getNode());

                                fragmentos.remove(i);
                        }
                }

                // =====================================
                // EXPLOSIVOS
                // =====================================

                for (int i = explosivos.size() - 1; i >= 0; i--) {

                        AsteroideExplosivo explosivo = explosivos.get(i);

                        if (posicaoPlayer.distance(
                                        explosivo.getPosicao()) <= 3f) {

                                player.receberDano(
                                                explosivo.getDanoExplosao());

                                rootNode.detachChild(
                                                explosivo.getNode());

                                explosivos.remove(i);
                        }
                }
        }

        // =========================================
        // QUANTIDADE DE FRAGMENTOS
        // =========================================

        public int getQuantidadeInimigos() {

                return fragmentos.size()
                                + explosivos.size();
        }
}