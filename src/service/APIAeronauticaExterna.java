package service;

public interface APIAeronauticaExterna {
    String fornecerDadosAerodromo(String icaoAerodromo);
    String fornecerCondicoesMeteorologicas(String icaoAerodromo);
    String fornecerTrafegoAereo(double latMin, double latMax, double lonMin, double lonMax);
}