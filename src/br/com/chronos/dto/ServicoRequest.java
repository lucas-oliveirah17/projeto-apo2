package br.com.chronos.dto;

import java.math.BigDecimal;
import br.com.chronos.model.Servico;

public class ServicoRequest {

    private String nome;
    private String descricao;
    private Integer duracaoMinutos;
    private BigDecimal preco;

    public Servico toEntity() {
        Servico s = new Servico();
        s.setNome(this.nome);
        s.setDescricao(this.descricao);
        s.setDuracaoMinutos(this.duracaoMinutos);
        s.setPreco(this.preco);
        return s;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

}