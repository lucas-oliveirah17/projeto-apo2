package br.com.chronos.dto;

import java.time.format.DateTimeFormatter;
import br.com.chronos.model.Agendamento;

public class AgendamentoResponse {

    private Long id;
    private String nomeCliente;
    private String nomeProfissional;
    private String nomeServico;
    private String dataHoraInicio;
    private String dataHoraFim;
    private String status;
    private String preco;

    public AgendamentoResponse(Agendamento a) {
        this.id = a.getId();
        
        if (a.getCliente() != null) {
            this.nomeCliente = a.getCliente().getNome();
        }
        
        if (a.getProfissional() != null && a.getProfissional().getUsuario() != null) {
            this.nomeProfissional = a.getProfissional().getUsuario().getNome();
        }
        
        if (a.getServico() != null) {
            this.nomeServico = a.getServico().getNome();
            this.preco = a.getServico().getPreco().toString();
        }
        
        // Formata para padrão brasileiro: "25/12/2025 14:30"
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        if (a.getDataHoraInicio() != null) {
            this.dataHoraInicio = a.getDataHoraInicio().format(fmt);
        }
        if (a.getDataHoraFim() != null) {
            this.dataHoraFim = a.getDataHoraFim().format(fmt);
        }
        
        this.status = (a.getStatus() != null) ? a.getStatus().getDescricao() : "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(String dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public String getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(String dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

}