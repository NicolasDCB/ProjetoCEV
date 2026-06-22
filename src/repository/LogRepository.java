package repository;

import database.ConnectionFactory;
import model.LogAuditoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogRepository {

    public void salvarLog(LogAuditoria log) {
        String sql = "INSERT INTO log_auditoria (usuario_ref, acao, data_hora) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getUsuarioRef());
            ps.setString(2, log.getAcao());
            ps.setTimestamp(3, Timestamp.valueOf(log.getDataHora()));
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao salvar log de auditoria: " + e.getMessage());
        }
    }
}