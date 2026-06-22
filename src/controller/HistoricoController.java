package controller;

import model.Emergencia;
import service.EmergenciaService;
import java.util.List;

public class HistoricoController {
    private final EmergenciaService emService = new EmergenciaService();

    public List<Emergencia> buscarComFiltros(String data, String nivel) {
        return emService.consultarHistorico(data, nivel);
    }
}