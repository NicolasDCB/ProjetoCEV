package model;

public class AlternativaPouso {
    private String aerodromoId;
    private Double tempoEstimadoVoo;
    private Double distancia;
    private Boolean viabilidade;

    public AlternativaPouso(String aerodromoId, Double tempoEstimadoVoo, Double distancia, Boolean viabilidade) {
        this.aerodromoId = aerodromoId;
        this.tempoEstimadoVoo = tempoEstimadoVoo;
        this.distancia = distancia;
        this.viabilidade = viabilidade;
    }

    // Getters e Setters
    public String getAerodromoId() { return aerodromoId; }
    public void setAerodromoId(String aerodromoId) { this.aerodromoId = aerodromoId; }

    public Double getTempoEstimadoVoo() { return tempoEstimadoVoo; }
    public void setTempoEstimadoVoo(Double tempoEstimadoVoo) { this.tempoEstimadoVoo = tempoEstimadoVoo; }

    public Double getDistancia() { return distancia; }
    public void setDistancia(Double distancia) { this.distancia = distancia; }

    public Boolean getViabilidade() { return viabilidade; }
    public void setViabilidade(Boolean viabilidade) { this.viabilidade = viabilidade; }
}