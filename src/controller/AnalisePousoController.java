package controller;

import model.AlternativaPouso;
import service.AnalisePousoService;
import java.util.List;

public class AnalisePousoController {
    private final AnalisePousoService analiseService = new AnalisePousoService();

    public List<AlternativaPouso> solicitarAnalise(String coordenadas) {
        return analiseService.analisarAlternativas(coordenadas);
    }
}