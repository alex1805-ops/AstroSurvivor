
package br.com.astrosurvivor;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class TelaPause {

    private final Node node = new Node("TelaPause");

    private Geometry botaoContinuar;
    private Geometry botaoReiniciar;
    private Geometry botaoMenu;

    private final float largura = 360f;
    private final float altura = 65f;

    public TelaPause(
            AssetManager assets,
            BitmapFont fonte,
            float larguraTela,
            float alturaTela) {

        Geometry fundo = new Geometry("FundoPause", new Quad(larguraTela, alturaTela));

        Material materialFundo = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");

        materialFundo.setColor( "Color", new ColorRGBA(0f, 0f, 0f, 0.82f));
        materialFundo.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        fundo.setMaterial(materialFundo);
        node.attachChild(fundo);

        BitmapText titulo = new BitmapText(fonte);
        titulo.setText("JOGO PAUSADO");
        titulo.setSize(55);
        titulo.setColor(ColorRGBA.White);
        centralizar(titulo, larguraTela / 2f, alturaTela * 0.75f);
        node.attachChild(titulo);

        botaoContinuar = criarBotao(
            assets, fonte, "CONTINUAR",
            larguraTela, alturaTela * 0.58f
        );

        botaoReiniciar = criarBotao(
            assets, fonte, "REINICIAR",
            larguraTela, alturaTela * 0.43f
        );

        botaoMenu = criarBotao(
            assets, fonte, "MENU INICIAL",
            larguraTela, alturaTela * 0.28f
        );

        node.attachChild(botaoContinuar);
        node.attachChild(botaoReiniciar);
        node.attachChild(botaoMenu);

        node.setCullHint(Node.CullHint.Always);
    }

    private Geometry criarBotao(
            AssetManager assets,
            BitmapFont fonte,
            String texto,
            float larguraTela,
            float centroY) {

        float x = (larguraTela - largura) / 2f;
        float y = centroY - altura / 2f;

        Geometry botao = new Geometry(texto, new Quad(largura, altura));

        Material material = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");

        material.setColor("Color", new ColorRGBA(0.04f, 0.12f, 0.20f, 0.95f));

        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        botao.setMaterial(material);
        botao.setLocalTranslation(x, y, 2);

        BitmapText textoBotao = new BitmapText(fonte);
        textoBotao.setText(texto);
        textoBotao.setSize(28);
        textoBotao.setColor(ColorRGBA.White);

        centralizar(textoBotao, larguraTela / 2f, centroY + textoBotao.getLineHeight() / 2f);

        node.attachChild(textoBotao);

        return botao;
    }

    private void centralizar(BitmapText texto, float centroX, float y) {
        texto.setLocalTranslation(centroX - texto.getLineWidth() / 2f, y, 3 );
    }

    public Node getNode() {
        return node;
    }

    public Geometry getBotaoContinuar() {
        return botaoContinuar;
    }

    public Geometry getBotaoReiniciar() {
        return botaoReiniciar;
    }

    public Geometry getBotaoMenu() {
        return botaoMenu;
    }

    public void mostrar() {
        node.setCullHint(Node.CullHint.Never);
    }

    public void esconder() {
        node.setCullHint(Node.CullHint.Always);
    }
}