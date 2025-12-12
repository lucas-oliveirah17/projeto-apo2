package br.com.chronos.model;

import java.time.LocalDateTime;

import br.com.chronos.model.enums.StatusAgendamento;

public class Agendamento {
    private Long id;
    private Usuario cliente;
    private Profissional profissional;
    private Servico servico;
    
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusAgendamento status;
    
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private boolean ativo;
    
    public Agendamento() {
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
        this.status = StatusAgendamento.PENDENTE;
    }
    
    public Agendamento(
            Long id, 
            Usuario cliente, 
            Profissional profissional, 
            Servico servico, 
            LocalDateTime dataHoraInicio, 
            LocalDateTime dataHoraFim, 
            StatusAgendamento status) {
        this();
        this.id = id;
        this.cliente = cliente;
        this.profissional = profissional;
        this.servico = servico;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Agendamento [id=" + id 
                + ", cliente=" + cliente 
                + ", profissional=" + profissional 
                + ", servico=" + servico 
                + ", dataHoraInicio=" + dataHoraInicio 
                + ", dataHoraFim=" + dataHoraFim 
                + ", status=" + status
                + ", criadoEm=" + criadoEm 
                + ", atualizadoEm=" + atualizadoEm 
                + ", ativo=" + ativo + "]";
    }
    
}
