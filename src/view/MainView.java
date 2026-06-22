package view;

import controller.AnalisePousoController;
import controller.GestaoEmergenciaController;
import controller.HistoricoController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class MainView extends JFrame {
    private final Usuario usuarioLogado;

    private final GestaoEmergenciaController gestaoCtrl = new GestaoEmergenciaController();
    private final AnalisePousoController analiseCtrl = new AnalisePousoController();
    private final HistoricoController historicoCtrl = new HistoricoController();

    private Emergencia emergenciaAtual = null;

    // Componentes Gráficos
    private JComboBox<NivelEmergencia> cbNivel;
    private JTextField txtCoordenadas;
    private JTable tblAlternativas;
    private DefaultTableModel modelAlternativas;
    private JButton btnRegistrar, btnAtualizar;

    private JTextField txtFiltroData;
    private JComboBox<String> cbFiltroNivel;
    private JTable tblHistorico;
    private DefaultTableModel modelHistorico;
    private JButton btnFiltrar;

    public MainView(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("CEV - Sistema de Controle de Vetoração de Emergência [Logado como: " + usuario.getPerfil() + "]");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();

        // Painel ATC (Controle Operacional)
        JPanel panelATC = iniciarPainelATC();
        abas.addTab("Painel Operacional (ATC)", panelATC);

        // Painel Analista (Histórico e Auditoria)
        JPanel panelAnalista = iniciarPainelAnalista();
        abas.addTab("Histórico & Auditoria (Analista)", panelAnalista);

        add(abas, BorderLayout.CENTER);

        // Bloqueio de abas baseado nos perfis do Diagrama de Classes

    }

    private JPanel iniciarPainelATC() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Declaração de Incidente em Curso"));

        form.add(new JLabel("Nível de Emergência:"));
        cbNivel = new JComboBox<>(NivelEmergencia.values());
        form.add(cbNivel);

        form.add(new JLabel("Coordenadas Geográficas (Ex: 23S, 45W):"));
        txtCoordenadas = new JTextField();
        form.add(txtCoordenadas);

        btnRegistrar = new JButton("Declarar e Analisar Alternativas (UC-01)");
        form.add(btnRegistrar);

        btnAtualizar = new JButton("Atualizar / Recalcular Rotas (UC-04)");
        btnAtualizar.setEnabled(false);
        form.add(btnAtualizar);

        principal.add(form, BorderLayout.NORTH);

        // Tabela de Rotas Alternativas
        modelAlternativas = new DefaultTableModel(new Object[]{"Aeródromo / Pista Disponível", "Distância Estimada (km)", "Tempo de Voo (min)", "Status Viabilidade"}, 0);
        tblAlternativas = new JTable(modelAlternativas);

        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Ordenação de Pistas Viáveis (Cálculo < 5s) - [UC-02]"));
        tabelaPanel.add(new JScrollPane(tblAlternativas), BorderLayout.CENTER);

        principal.add(tabelaPanel, BorderLayout.CENTER);

        // Eventos
        btnRegistrar.addActionListener(e -> acaoRegistrar());
        btnAtualizar.addActionListener(e -> acaoAtualizar());

        return principal;
    }

    private JPanel iniciarPainelAnalista() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros de Auditoria"));

        filtros.add(new JLabel("Data (AAAA-MM-DD):"));
        txtFiltroData = new JTextField(10);
        filtros.add(txtFiltroData);

        filtros.add(new JLabel("Nível:"));
        cbFiltroNivel = new JComboBox<>(new String[]{"TODOS", "URGENCIA", "PERIGO", "ANGUSTIA"});
        filtros.add(cbFiltroNivel);

        btnFiltrar = new JButton("Consultar Registros (UC-03)");
        filtros.add(btnFiltrar);

        principal.add(filtros, BorderLayout.NORTH);

        modelHistorico = new DefaultTableModel(new Object[]{"ID Registro", "Data/Hora Ocorrência", "Severidade", "Coordenadas", "Pista Vetorada"}, 0);
        tblHistorico = new JTable(modelHistorico);
        principal.add(new JScrollPane(tblHistorico), BorderLayout.CENTER);

        btnFiltrar.addActionListener(e -> acaoFiltrar());

        return principal;
    }

    private void acaoRegistrar() {
        try {
            emergenciaAtual = new Emergencia();
            emergenciaAtual.setTimestamp(LocalDateTime.now());
            emergenciaAtual.setNivel((NivelEmergencia) cbNivel.getSelectedItem());
            emergenciaAtual.setCoordenadas(txtCoordenadas.getText());

            gestaoCtrl.registrar(emergenciaAtual);
            atualizarTabelaAlternativas();

            btnAtualizar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Emergência inserida e gravada com sucesso! Código gerado: ID " + emergenciaAtual.getIdEmergencia());
        } catch (Exception ex) {
            tratarExcecaoTerritorio(ex, true);
        }
    }

    private void acaoAtualizar() {
        if (emergenciaAtual == null) return;
        try {
            emergenciaAtual.setNivel((NivelEmergencia) cbNivel.getSelectedItem());
            emergenciaAtual.setCoordenadas(txtCoordenadas.getText());

            gestaoCtrl.atualizar(emergenciaAtual);
            atualizarTabelaAlternativas();
            JOptionPane.showMessageDialog(this, "Registro ID " + emergenciaAtual.getIdEmergencia() + " atualizado no banco de dados.");
        } catch (Exception ex) {
            tratarExcecaoTerritorio(ex, false);
        }
    }

    private void tratarExcecaoTerritorio(Exception ex, boolean isNovoRegistro) {
        if (ex.getMessage() != null && ex.getMessage().contains("ALERTA_TERRITORIO")) {
            // Materialização do balão <<extend>> Confirmar Alerta do Território do Caso de Uso 1
            int op = JOptionPane.showConfirmDialog(this,
                    "Atenção: Posição fora do espaço aéreo brasileiro monitorado!\nDeseja forçar o processamento internacional?",
                    "Alerta de Território Extranacional", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (op == JOptionPane.YES_OPTION) {
                try {
                    if (isNovoRegistro) {
                        emergenciaAtual.setAerodromoDefinido("FALSO_POSITIVO_INTERNACIONAL");
                        // Força salvamento manual pulando regras locais
                        atualizarTabelaAlternativas();
                    } else {
                        atualizarTabelaAlternativas();
                    }
                } catch (Exception ignored) {}
            }
        } else {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabelaAlternativas() {
        modelAlternativas.setRowCount(0);
        List<AlternativaPouso> list = analiseCtrl.solicitarAnalise(txtCoordenadas.getText());

        if (!list.isEmpty()) {
            emergenciaAtual.setAerodromoDefinido(list.get(0).getAerodromoId());
        }

        for (AlternativaPouso a : list) {
            modelAlternativas.addRow(new Object[]{
                    a.getAerodromoId(),
                    String.format("%.2f", a.getDistancia()),
                    String.format("%.1f", a.getTempoEstimadoVoo()),
                    a.getViabilidade() ? "DISPONÍVEL (VMC)" : "INDISPONÍVEL (IMC / RADAR)"
            });
        }
    }

    private void acaoFiltrar() {
        modelHistorico.setRowCount(0);
        List<Emergencia> dados = historicoCtrl.buscarComFiltros(txtFiltroData.getText(), cbFiltroNivel.getSelectedItem().toString());
        for (Emergencia e : dados) {
            modelHistorico.addRow(new Object[]{
                    e.getIdEmergencia(),
                    e.getTimestamp(),
                    e.getNivel(),
                    e.getCoordenadas(),
                    e.getAerodromoDefinido()
            });
        }
    }
}