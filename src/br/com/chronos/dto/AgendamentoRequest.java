package br.com.chronos.dto;

import java.time.LocalDateTime;

import br.com.chronos.model.Agendamento;
import br.com.chronos.model.Profissional;
import br.com.chronos.model.Servico;
import br.com.chronos.model.Usuario;

public class AgendamentoRequest {

    private Long clienteId;
    private Long profissionalId;
    private Long servicoId;
    private String dataHoraInicio; // Espera formato ISO: "2025-12-25T14:30:00"

    public Agendamento toEntity() {
        Agendamento a = new Agendamento();
        
        Usuario c = new Usuario(); 
        c.setId(this.clienteId);
        a.setCliente(c);
        
        Profissional p = new Profissional(); 
        p.setId(this.profissionalId);
        a.setProfissional(p);
        
        Servico s = new Servico(); 
        s.setId(this.servicoId);
        a.setServico(s);
        
        if (this.dataHoraInicio != null) {
            // Converte String ISO para LocalDateTime
            a.setDataHoraInicio(LocalDateTime.parse(this.dataHoraInicio));
        }
        
        return a;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getProfissionalId() {
        return profissionalId;
    }

    public void setProfissionalId(Long profissionalId) {
        this.profissionalId = profissionalId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public String getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(String dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

}