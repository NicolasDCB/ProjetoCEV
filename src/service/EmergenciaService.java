package service;

import model.Emergencia;
import repository.EmergenciaRepository;

import java.util.List;

public class EmergenciaService {
    private final EmergenciaRepository emergenciaRepository = new EmergenciaRepository();

    public void registrarEmergencia(Emergencia emergencia) throws Exception {
        validarCampos(emergencia);
        emergenciaRepository.salvar(emergencia);
    }

    public void atualizarEmergencia(Emergencia emergencia) throws Exception {
        validarCampos(emergencia);
        emergenciaRepository.atualizar(emergencia);
    }

    public List<Emergencia> consultarHistorico(String data, String nivel) {
        return emergenciaRepository.buscarComFiltros(data, nivel);
    }

    private void validarCampos(Emergencia emergencia) throws Exception {
        if (emergencia.getCoordenadas() == null || emergencia.getCoordenadas().trim().isEmpty()) {
            throw new Exception("Erro: O campo de Coordenadas é de preenchimento obrigatório!");
        }

        // Simulação de validação de território nacional (Exige formato com direções cardeais Ex: 23S, 45W)
        String coord = emergencia.getCoordenadas().toUpperCase();
        if (!coord.contains("S") && !coord.contains("N")) {
            throw new Exception("ALERTA_TERRITORIO: Coordenadas informadas parecem fora do território monitorado nacional. Deseja prosseguir com a análise externa?");
        }
    }
}