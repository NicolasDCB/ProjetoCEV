package model;

import java.time.LocalDateTime;

public class Emergencia {
    private Integer idEmergencia;
    private LocalDateTime timestamp;
    private NivelEmergencia nivel;
    private String coordenadas;
    private String aerodromoDefinido;

    public Emergencia() {}

    public java.sql.Timestamp getTimestampAsSql() {
        return timestamp != null ? java.sql.Timestamp.valueOf(timestamp) : null;
    }

    // Getters e Setters
    public Integer getIdEmergencia() { return idEmergencia; }
    public void setIdEmergencia(Integer idEmergencia) { this.idEmergencia = idEmergencia; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public NivelEmergencia getNivel() { return nivel; }
    public void setNivel(NivelEmergencia nivel) { this.nivel = nivel; }

    public String getCoordenadas() { return coordenadas; }
    public void setCoordenadas(String coordenadas) { this.coordenadas = coordenadas; }

    public String getAerodromoDefinido() { return aerodromoDefinido; }
    public void setAerodromoDefinido(String aerodromoDefinido) { this.aerodromoDefinido = aerodromoDefinido; }
}