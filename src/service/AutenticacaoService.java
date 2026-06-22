package service;

import model.LogAuditoria;
import model.Usuario;
import repository.LogRepository;
import repository.UsuarioRepository;

import java.time.LocalDateTime;

public class AutenticacaoService {
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final LogRepository logRepository = new LogRepository();
    private int tentativasInvalidas = 0;

    public Usuario autenticar(String credenciais, String senha) throws Exception {
        if (tentativasInvalidas >= 3) {
            logRepository.salvarLog(new LogAuditoria(credenciais, "TENTATIVA EM CONTA BLOQUEADA", LocalDateTime.now()));
            throw new Exception("Conta temporariamente bloqueada por excesso de tentativas!");
        }

        Usuario usuario = usuarioRepository.buscarPorCredenciais(credenciais);

        if (usuario != null && usuario.getSenha().equals(senha)) {
            tentativasInvalidas = 0; // Reseta o contador em caso de sucesso
            logRepository.salvarLog(new LogAuditoria(credenciais, "LOGIN BEM SUCEDIDO", LocalDateTime.now()));
            return usuario;
        } else {
            tentativasInvalidas++;
            logRepository.salvarLog(new LogAuditoria(credenciais, "FALHA DE AUTENTICACAO (Tentativa " + tentativasInvalidas + ")", LocalDateTime.now()));

            if (tentativasInvalidas >= 3) {
                logRepository.salvarLog(new LogAuditoria(credenciais, "CONTA BLOQUEADA", LocalDateTime.now()));
                throw new Exception("Credenciais inválidas! Conta bloqueada após 3 tentativas mal-sucedidas.");
            }
            throw new Exception("Credenciais inválidas! Restam " + (3 - tentativasInvalidas) + " tentativas.");
        }
    }
}