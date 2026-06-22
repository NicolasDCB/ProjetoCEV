package model;

public class Usuario {
    private Integer idUsuario;
    private String credenciais; // Usado para o login/idFuncional conforme diagramas
    private String senha;
    private PerfilAcesso perfil;

    public Usuario() {}

    public Usuario(Integer idUsuario, String credenciais, String senha, PerfilAcesso perfil) {
        this.idUsuario = idUsuario;
        this.credenciais = credenciais;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Getters e Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getCredenciais() { return credenciais; }
    public void setCredenciais(String credenciais) { this.credenciais = credenciais; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilAcesso getPerfil() { return perfil; }
    public void setPerfil(PerfilAcesso perfil) { this.perfil = perfil; }
}