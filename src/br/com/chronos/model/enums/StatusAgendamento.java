package br.com.chronos.model.enums;

public enum StatusAgendamento {
    CONFIRMADO(1, "Confirmado"),
    PENDENTE(2, "Pendente"),
    CONCLUIDO(3, "Concluído"),
    CANCELADO_PELO_CLIENTE(4, "Cancelado pelo Cliente"),
    CANCELADO_PELO_PROFISSIONAL(5, "Cancelado pelo Profissional"),
    CANCELADO_PELO_ADMINISTRADOR(6, "Cancelado pelo Administrador"),
    NAO_COMPARECEU(7, "Não Compareceu");
    
    private final int codigo;
    private final String descricao;
    
    private StatusAgendamento(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public static StatusAgendamento toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        
        for (StatusAgendamento x : StatusAgendamento.values()) {
            if (codigo.equals(x.getCodigo())) {
                return x;
            }
        }
        
        throw new IllegalArgumentException("Id inválido: " + codigo);
    }
    
    public boolean isConfirmado() {
        return this == CONFIRMADO;
    }
    
    public boolean isPendente() {
        return this == PENDENTE;
    }
    
    public boolean isConcluido() {
        return this == CONCLUIDO;
    }
    
    public boolean isCanceladoPeloCliente() {
        return this == CANCELADO_PELO_CLIENTE;   
    }
    
    public boolean isCanceladoPeloProfissional() {
        return this == CANCELADO_PELO_PROFISSIONAL;
    }
    
    public boolean isCanceladoPeloAdministrador() {
        return this == CANCELADO_PELO_ADMINISTRADOR;    
    }
    
    public boolean isNaoCompareceu() {
        return this == NAO_COMPARECEU;      
    }
    
}
