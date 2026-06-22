package controller;

import model.Usuario;
import service.AutenticacaoService;

public class AutenticacaoController {
    private final AutenticacaoService autenticacaoService = new AutenticacaoService();

    public Usuario processarAutenticacao(String login, String senha) throws Exception {
        return autenticacaoService.autenticar(login, senha);
    }
}