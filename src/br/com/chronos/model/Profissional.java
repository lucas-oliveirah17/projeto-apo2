package br.com.chronos.model;

public class Profissional {
    private Long id;
    private Usuario usuario;
    private String especialidades;
    private boolean ativo;
    
    public Profissional() {
        this.ativo = true;
    }
    
    public Profissional(
            Long id, 
            Usuario usuario, 
            String especialidades, 
            boolean ativo) {
        this.id = id;
        this.usuario = usuario;
        this.especialidades = especialidades;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    @Override
    public String toString() {
        return "Profissional [id=" + id 
                + ", usuario=" + usuario 
                + ", especialidades=" + especialidades 
                + ", ativo=" + ativo + "]";
    }
    
}
