package service.external;

import service.APIAeronauticaExterna;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class APIAeronauticaExternaImpl implements APIAeronauticaExterna {

    private final HttpClient httpClient;
    private final String AIRPORT_DB_TOKEN = System.getenv("AIRPORT_DB_TOKEN");

    public APIAeronauticaExternaImpl() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String fornecerDadosAerodromo(String icaoAerodromo) {
        if (AIRPORT_DB_TOKEN == null || AIRPORT_DB_TOKEN.isBlank()) {
            return "{\"erro\": \"Variável de ambiente AIRPORT_DB_TOKEN não configurada.\"}";
        }
        String url = "https://airportdb.io/api/v1/airport/"
                + icaoAerodromo.toUpperCase()
                + "?apiToken=" + AIRPORT_DB_TOKEN;
        return fazerRequisicaoGET(url);
    }

    @Override
    public String fornecerCondicoesMeteorologicas(String icaoAerodromo) {
        String url = "https://aviationweather.gov/api/data/metar?ids="
                + icaoAerodromo.toUpperCase()
                + "&format=json";
        return fazerRequisicaoGET(url);
    }

    @Override
    public String fornecerTrafegoAereo(double latMin, double latMax, double lonMin, double lonMax) {
        String url = String.format(Locale.US,
                "https://opensky-network.org/api/states/all?lamin=%.4f&lomin=%.4f&lamax=%.4f&lomax=%.4f",
                latMin, lonMin, latMax, lonMax
        );
        return fazerRequisicaoGET(url);
    }

    private String fazerRequisicaoGET(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() == 204) {
                return "[]";
            } else {
                return "{\"erro\": \"Falha na API. Status HTTP: " + response.statusCode() + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"erro\": \"Exceção ao conectar com API externa: " + e.getMessage() + "\"}";
        }
    }
}