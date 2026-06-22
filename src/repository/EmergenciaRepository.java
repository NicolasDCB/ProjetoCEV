package repository;

import database.ConnectionFactory;
import model.Emergencia;
import model.NivelEmergencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmergenciaRepository {

    public void salvar(Emergencia emergencia) {
        String sql = "INSERT INTO emergencia (timestamp_registro, nivel_emergencia, coordenadas, aerodromo_definido) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, emergencia.getTimestampAsSql());
            ps.setString(2, emergencia.getNivel().name());
            ps.setString(3, emergencia.getCoordenadas());
            ps.setString(4, emergencia.getAerodromoDefinido());
            ps.executeUpdate();

            // Recupera o ID único gerado automaticamente pelo banco de dados
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emergencia.setIdEmergencia(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar emergência: " + e.getMessage());
        }
    }

    public void atualizar(Emergencia emergencia) {
        String sql = "UPDATE emergencia SET nivel_emergencia = ?, coordenadas = ?, aerodromo_definido = ? WHERE id_emergencia = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emergencia.getNivel().name());
            ps.setString(2, emergencia.getCoordenadas());
            ps.setString(3, emergencia.getAerodromoDefinido());
            ps.setInt(4, emergencia.getIdEmergencia());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar emergência: " + e.getMessage());
        }
    }

    public List<Emergencia> buscarComFiltros(String dataFiltro, String nivelFiltro) {
        List<Emergencia> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM emergencia WHERE 1=1");

        if (dataFiltro != null && !dataFiltro.trim().isEmpty()) {
            sql.append(" AND CAST(timestamp_registro AS DATE) = '").append(dataFiltro).append("'");
        }
        if (nivelFiltro != null && !nivelFiltro.equals("TODOS")) {
            sql.append(" AND nivel_emergencia = '").append(nivelFiltro).append("'");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {

            while (rs.next()) {
                Emergencia em = new Emergencia();
                em.setIdEmergencia(rs.getInt("id_emergencia"));
                em.setTimestamp(rs.getTimestamp("timestamp_registro").toLocalDateTime());
                em.setNivel(NivelEmergencia.valueOf(rs.getString("nivel_emergencia")));
                em.setCoordenadas(rs.getString("coordenadas"));
                em.setAerodromoDefinido(rs.getString("aerodromo_definido"));
                lista.add(em);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar histórico de incidentes: " + e.getMessage());
        }
        return lista;
    }
}