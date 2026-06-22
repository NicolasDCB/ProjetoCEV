package view;

import controller.AutenticacaoController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private final AutenticacaoController authController = new AutenticacaoController();

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;

    public LoginView() {
        setTitle("CEV - Autenticação Corporativa");
        setSize(380, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("ID Funcional / Login:"));
        txtLogin = new JTextField();
        panel.add(txtLogin);

        panel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        panel.add(txtSenha);

        btnEntrar = new JButton("Autenticar (UC-05)");
        panel.add(btnEntrar);

        add(panel, BorderLayout.CENTER);

        btnEntrar.addActionListener(e -> efetuarLogin());
    }

    private void efetuarLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        try {
            Usuario usuario = authController.processarAutenticacao(login, senha);

            // Abre o painel correspondente ao usuário logado
            MainView principal = new MainView(usuario);
            principal.setVisible(true);

            this.dispose(); // Fecha a tela de login
        } catch (Exception ex) {
            // Exibe mensagem de erro e tentativas baseadas na regra de segurança
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Falha de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Configura visual moderno do sistema operacional (Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginView telaLogin = new LoginView();
            telaLogin.setVisible(true);
        });
    }
}