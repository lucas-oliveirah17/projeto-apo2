package br.com.chronos.model;

import java.time.LocalDateTime;

import br.com.chronos.model.enums.PerfilUsuario;

public class Usuario implements Entidade { 
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private PerfilUsuario perfil;
    private LocalDateTime criadoEm;
    private boolean ativo;
    
    private String codigo2FA;
    private LocalDateTime validade2FA;
    
    public Usuario() {
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }
    
    public Usuario(
            Long id, 
            String nome, 
            String email, 
            String senha, 
            String telefone, 
            PerfilUsuario perfil, 
            boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getCodigo2FA() {
        return codigo2FA;
    }

    public void setCodigo2FA(String codigo2fa) {
        codigo2FA = codigo2fa;
    }

    public LocalDateTime getValidade2FA() {
        return validade2FA;
    }

    public void setValidade2FA(LocalDateTime validade2fa) {
        validade2FA = validade2fa;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id 
                + ", nome=" + nome 
                + ", email=" + email 
                + ", senha=" + senha 
                + ", telefone=" + telefone 
                + ", perfil=" + perfil 
                + ", criadoEm=" + criadoEm 
                + ", ativo=" + ativo 
                + ", codigo2FA=" + codigo2FA 
                + ", validade2FA=" + validade2FA + "]";
    }
    
}
