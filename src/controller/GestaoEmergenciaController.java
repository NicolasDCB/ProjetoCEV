package controller;

import model.Emergencia;
import service.EmergenciaService;

public class GestaoEmergenciaController {
    private final EmergenciaService emergenciaService = new EmergenciaService();

    public void registrar(Emergencia em) throws Exception {
        emergenciaService.registrarEmergencia(em);
    }

    public void atualizar(Emergencia em) throws Exception {
        emergenciaService.atualizarEmergencia(em);
    }
}