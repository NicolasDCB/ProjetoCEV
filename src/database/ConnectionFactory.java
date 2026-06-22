package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    private static final String URL = "jdbc:h2:mem:cev_db;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static boolean inicializado = false;

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        if (!inicializado) {
            criarTabelasEIniciarDados(conn);
        }
        return conn;
    }

    private static void criarTabelasEIniciarDados(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Tabela de usuários
            st.execute("CREATE TABLE IF NOT EXISTS usuario (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "credenciales VARCHAR(50) UNIQUE NOT NULL, " +
                    "senha VARCHAR(50) NOT NULL, " +
                    "perfil VARCHAR(20) NOT NULL)");

            // Tabela de emergências / incidentes
            st.execute("CREATE TABLE IF NOT EXISTS emergencia (" +
                    "id_emergencia INT AUTO_INCREMENT PRIMARY KEY, " +
                    "timestamp_registro TIMESTAMP NOT NULL, " +
                    "nivel_emergencia VARCHAR(20) NOT NULL, " +
                    "coordenadas VARCHAR(100) NOT NULL, " +
                    "aerodromo_definido VARCHAR(100))");

            // Tabela de logs de auditoria
            st.execute("CREATE TABLE IF NOT EXISTS log_auditoria (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "usuario_ref VARCHAR(50) NOT NULL, " +
                    "acao VARCHAR(100) NOT NULL, " +
                    "data_hora TIMESTAMP NOT NULL)");

            // Carga de dados acadêmicos iniciais para simulação (MERGE impede chaves duplicadas)
            st.execute("MERGE INTO usuario (id, credenciales, senha, perfil) KEY(credenciales) " +
                    "VALUES (1, 'atc1', '1234', 'ATC')");
            st.execute("MERGE INTO usuario (id, credenciales, senha, perfil) KEY(credenciales) " +
                    "VALUES (2, 'analista1', '1234', 'ANALISTA')");

            inicializado = true;
        } catch (SQLException e) {
            System.err.println("Erro ao instanciar tabelas internas no H2: " + e.getMessage());
        }
    }
}