package br.com.chronos.model;

import java.math.BigDecimal;

public class Servico implements Entidade {
    
    private Long id;
    private String nome;
    private String descricao;
    private Integer duracaoMinutos;
    private BigDecimal preco;
    private boolean ativo;
    
    public Servico() {
        this.ativo = true;
    }
    
    public Servico(
            Long id, 
            String nome, 
            String descricao, 
            Integer duracaoMinutos, 
            BigDecimal preco, 
            boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.duracaoMinutos = duracaoMinutos;
        this.preco = preco;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Servico [id=" + id 
                + ", nome=" + nome 
                + ", descricao=" + descricao 
                + ", duracaoMinutos=" + duracaoMinutos 
                + ", preco=" + preco 
                + ", ativo=" + ativo + "]";
    }

}
