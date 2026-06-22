package repository;

import database.ConnectionFactory;
import model.Usuario;
import model.PerfilAcesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRepository {

    public Usuario buscarPorCredenciais(String credenciais) {
        String sql = "SELECT * FROM usuario WHERE credenciales = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, credenciais);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("credenciales"),
                            rs.getString("senha"),
                            PerfilAcesso.valueOf(rs.getString("perfil"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }
}