package br.com.chronos.dto;

import br.com.chronos.model.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String perfil; // Ex: "Cliente"
    private Integer perfilId;
    private boolean ativo;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.telefone = usuario.getTelefone();
        if (usuario.getPerfil() != null) {
            this.perfil = usuario.getPerfil().getDescricao();
            this.perfilId = usuario.getPerfil().getCodigo(); // Adicionado
        }
        this.ativo = usuario.isAtivo();
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
    
    public Integer getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(Integer perfilId) {
        this.perfilId = perfilId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

}