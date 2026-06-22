package service;

import model.AlternativaPouso;
import service.external.APIAeronauticaExternaImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnalisePousoService {
    private final APIAeronauticaExterna apiExterna = new APIAeronauticaExternaImpl();
    private static final String CSV_PATH = "airports.csv";

    public List<AlternativaPouso> analisarAlternativas(String coordenadas) {
        List<AlternativaPouso> alternativas = new ArrayList<>();

        // 1. Converte a string do painel (ex: "23S, 45W") para decimais puras
        double[] pontoEmergencia = converterCoordenadas(coordenadas);
        double latEmergencia = pontoEmergencia[0];
        double lonEmergencia = pontoEmergencia[1];

        List<AeroportoLocal> candidatosProximos = new ArrayList<>();

        // 2. Lê o arquivo airports.csv e calcula a distância real de cada aeroporto do mundo
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String linha = br.readLine(); // Pula o cabeçalho (id, ident, type, name...)

            while ((linha = br.readLine()) != null) {
                // Evita quebras se a linha contiver aspas com vírgulas dentro do nome
                String[] colunas = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (colunas.length > 5) {
                    String type = colunas[2].replace("\"", "");
                    // Filtra apenas aeroportos de médio e grande porte, descartando helipontos/pistas desativadas
                    if (type.equals("medium_airport") || type.equals("large_airport")) {

                        String icao = colunas[1].replace("\"", "");
                        String nome = colunas[3].replace("\"", "");

                        // Garante que o aeroporto possui código ICAO válido de 4 letras (Ex: SBGR)
                        if (icao.length() == 4) {
                            try {
                                double latAero = Double.parseDouble(colunas[4]);
                                double lonAero = Double.parseDouble(colunas[5]);

                                // Calcula a distância real em quilómetros usando a Fórmula de Haversine
                                double dist = calcularDistanciaHaversine(latEmergencia, lonEmergencia, latAero, lonAero);

                                candidatosProximos.add(new AeroportoLocal(icao, nome, dist));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao ler base de dados offline (airports.csv). Usando modo de contingência.");
            e.printStackTrace();
        }

        // 3. Ordena pela menor distância geográfica e escolhe os 3 mais próximos do avião
        candidatosProximos.sort(Comparator.comparingDouble(a -> a.distancia));

        int limite = Math.min(3, candidatosProximos.size());

        // Se o arquivo CSV falhar ou estiver vazio, carrega uma contingência automática
        if (limite == 0) {
            candidatosProximos.add(new AeroportoLocal("SBGR", "Guarulhos Intl", 45.0));
            candidatosProximos.add(new AeroportoLocal("SBSP", "Congonhas Airport", 98.0));
            candidatosProximos.add(new AeroportoLocal("SBKP", "Viracopos Intl", 150.0));
            limite = 3;
        }

        // 4. Bate nas APIs externas em tempo real apenas para o Top 3 mais próximos encontrados!
        for (int i = 0; i < limite; i++) {
            AeroportoLocal aero = candidatosProximos.get(i);

            String dadosJson = apiExterna.fornecerDadosAerodromo(aero.icao);
            String metarJson = apiExterna.fornecerCondicoesMeteorologicas(aero.icao);
            String radarJson = apiExterna.fornecerTrafegoAereo(latEmergencia - 1, latEmergencia + 1, lonEmergencia - 1, lonEmergencia + 1);

            double tempoEstimado = (aero.distancia / 800.0) * 60.0; // Assume velocidade média de 800 km/h

            // Regra de Viabilidade baseada nos retornos das APIs
            boolean viabilidade = !metarJson.contains("erro") && !radarJson.contains("erro");

            // Se a API online trouxer o nome atualizado, usa-o. Caso contrário, mantém o nome do arquivo CSV local.
            String nomeFinal = dadosJson.contains("erro") ? aero.icao + " - " + aero.nome : extrairNomeDoJson(dadosJson, aero.icao);

            alternativas.add(new AlternativaPouso(nomeFinal, tempoEstimado, aero.distancia, viabilidade));
        }

        // Ordena a tabela colocando viáveis no topo
        alternativas.sort(Comparator.comparing(AlternativaPouso::getViabilidade).reversed()
                .thenComparing(AlternativaPouso::getTempoEstimadoVoo));

        return alternativas;
    }

    private double[] converterCoordenadas(String coordenadas) {
        double lat = -23.4256; // Padrão se der erro de parsing (região de SP)
        double lon = -46.4731;
        try {
            String[] partes = coordenadas.toUpperCase().split(",");
            if (partes.length >= 2) {
                String pLat = partes[0].trim();
                String pLon = partes[1].trim();

                // Trata Latitude (Ex: 23S ou 23N)
                lat = Double.parseDouble(pLat.replaceAll("[A-Z]", ""));
                if (pLat.contains("S")) lat = -lat;

                // Trata Longitude (Ex: 45W ou 45E)
                lon = Double.parseDouble(pLon.replaceAll("[A-Z]", ""));
                if (pLon.contains("W")) lon = -lon;
            }
        } catch (Exception ignored) {}
        return new double[]{lat, lon};
    }

    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // Raio da Terra em quilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String extrairNomeDoJson(String json, String icao) {
        try {
            int pos = json.indexOf("\"name\":");
            int inicio = json.indexOf("\"", pos + 7) + 1;
            int fim = json.indexOf("\"", inicio);
            return icao + " - " + json.substring(inicio, fim);
        } catch (Exception e) {
            return icao + " - Conexão Ativa";
        }
    }

    // Classe interna auxiliar para mapeamento temporário
    private static class AeroportoLocal {
        String icao;
        String nome;
        double distancia;

        AeroportoLocal(String icao, String nome, double distancia) {
            this.icao = icao;
            this.nome = nome;
            this.distancia = distancia;
        }
    }
}