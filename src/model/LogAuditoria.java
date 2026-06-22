package model;

import java.time.LocalDateTime;

public class LogAuditoria {
    private Integer idLog;
    private String usuarioRef;
    private String acao;
    private LocalDateTime dataHora;

    public LogAuditoria(String usuarioRef, String acao, LocalDateTime dataHora) {
        this.usuarioRef = usuarioRef;
        this.acao = acao;
        this.dataHora = dataHora;
    }

    // Getters
    public String getUsuarioRef() { return usuarioRef; }
    public String getAcao() { return acao; }
    public LocalDateTime getDataHora() { return dataHora; }
}