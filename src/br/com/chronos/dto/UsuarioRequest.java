package br.com.chronos.dto;

import br.com.chronos.model.Usuario;
import br.com.chronos.model.enums.PerfilUsuario;

public class UsuarioRequest {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private Integer perfilId; // 1: Administrador, 2: Profissional, 3: Cliente

    public Usuario toEntity() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        usuario.setSenha(this.senha);
        usuario.setTelefone(this.telefone);
        usuario.setPerfil(PerfilUsuario.toEnum(this.perfilId));
        return usuario;
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

    public Integer getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(Integer perfilId) {
        this.perfilId = perfilId;
    }
    
}