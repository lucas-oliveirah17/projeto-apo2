package br.com.chronos.model.enums;

public enum PerfilUsuario {
    ADMINISTRADOR(1, "Administrador"),
    PROFISSIONAL(2, "Profissional"),
    CLIENTE(3, "Cliente");
    
    private final int codigo;
    private final String descricao;
    
    private PerfilUsuario(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public int getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static PerfilUsuario toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        
        for (PerfilUsuario x : PerfilUsuario.values()) {
            if (codigo.equals(x.getCodigo())) {
                return x;
            }
        }
        
        throw new IllegalArgumentException("Id inválido: " + codigo);
    }
    
    public boolean isAdministrador() {
        return this == ADMINISTRADOR;
    }
    
    public boolean isProfissional() {
        return this == PROFISSIONAL;
    }
    
    public boolean isCliente() {
        return this == CLIENTE;
    }
    
}
