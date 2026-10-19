
package br.com.astrosurvivor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RankingManager {

    private final Path arquivo = Path.of("dados", "ranking.tsv");

    public static class Registro {
        public String nick;
        public int maiorNivel;
        public int maiorScore;

        public Registro(String nick, int maiorNivel, int maiorScore) {
            this.nick = nick;
            this.maiorNivel = maiorNivel;
            this.maiorScore = maiorScore;
        }
    }

    private String chave(String nick) {
        return nick.trim().toLowerCase(Locale.ROOT);
    }

    private Map<String, Registro> carregar() {
        Map<String, Registro> registros = new HashMap<>();

        if (!Files.exists(arquivo)) {
            return registros;
        }

        try {
            for (String linha :
                    Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {

                String[] partes = linha.split("\t", -1);

                if (partes.length != 3) {
                    continue;
                }

                try {
                    String nick = partes[0];
                    int nivel = Integer.parseInt(partes[1]);
                    int score = Integer.parseInt(partes[2]);

                    if (!nick.isBlank()) {
                        registros.put(
                            chave(nick),
                            new Registro(nick, nivel, score)
                        );
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Registro inválido: " + linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar ranking: " + e.getMessage());
        }

        return registros;
    }

    public void registrarResultado(String nick, int nivel, int score) {
        if (nick == null || nick.isBlank()) {
            return;
        }

        nick = nick.trim()
                .replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ");

        Map<String, Registro> registros = carregar();
        String chaveJogador = chave(nick);

        Registro registro = registros.get(chaveJogador);

        if (registro == null) {
            registros.put(
                chaveJogador,
                new Registro(nick, nivel, score)
            );
        } else {
            registro.maiorNivel = Math.max(registro.maiorNivel, nivel);
            registro.maiorScore = Math.max(registro.maiorScore, score);
        }

        salvar(registros);
    }

    private void salvar(Map<String, Registro> registros) {
        try {
            Files.createDirectories(arquivo.getParent());

            List<String> linhas = new ArrayList<>();

            for (Registro r : registros.values()) {
                linhas.add(r.nick + "\t" + r.maiorNivel + "\t" + r.maiorScore);
            }

            Files.write(arquivo, linhas, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.err.println("Erro ao salvar ranking: " + e.getMessage());
        }
    }

    public List<Registro> obterRanking() {
        List<Registro> ranking = new ArrayList<>(carregar().values());

        ranking.sort(
            Comparator.comparingInt((Registro r) -> r.maiorNivel)
                .reversed()
                .thenComparing(
                    Comparator.comparingInt((Registro r) -> r.maiorScore)
                        .reversed()
                )
        );

        return ranking;
    }

    public String obterRankingTexto(int limite) {
        List<Registro> ranking = obterRanking();

        if (ranking.isEmpty()) {
            return "Ainda não existem \n recordes registrados.";
        }

        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < Math.min(limite, ranking.size()); i++) {
            Registro r = ranking.get(i);

            texto.append(String.format(
                "%dº  %s  | Nível: %d | Score: %d%n",
                i + 1, r.nick, r.maiorNivel, r.maiorScore
            ));
        }

        return texto.toString();
    }
}