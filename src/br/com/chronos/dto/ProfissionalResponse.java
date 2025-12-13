package br.com.chronos.dto;

import br.com.chronos.model.Profissional;

public class ProfissionalResponse {

    private Long id;
    private Long usuarioId;
    private String nome;
    private String email;
    private String especialidades;
    private boolean ativo;

    public ProfissionalResponse(Profissional p) {
        this.id = p.getId();
        this.especialidades = p.getEspecialidades();
        this.ativo = p.isAtivo();
        
        if (p.getUsuario() != null) {
            this.usuarioId = p.getUsuario().getId();
            this.nome = p.getUsuario().getNome();
            this.email = p.getUsuario().getEmail();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public String getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

}