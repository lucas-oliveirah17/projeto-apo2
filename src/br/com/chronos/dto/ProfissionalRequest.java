package br.com.chronos.dto;

import br.com.chronos.model.Profissional;
import br.com.chronos.model.Usuario;

public class ProfissionalRequest {

    private Long usuarioId;
    private String especialidades;

    public Profissional toEntity() {
        Profissional p = new Profissional();
        
        Usuario u = new Usuario();
        u.setId(this.usuarioId); // Cria um usuário "fake" só com ID para o relacionamento
        
        p.setUsuario(u);
        p.setEspecialidades(this.especialidades);
        return p;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

}