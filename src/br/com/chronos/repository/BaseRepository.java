package br.com.chronos.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.chronos.model.Entidade;
import br.com.chronos.util.DBConnection;

/**
 * Classe genérica que implementa o CRUD básico para qualquer entidade.
 */
public abstract class BaseRepository<T extends Entidade> {

    protected Connection connection;
    protected final String tableName;

    public BaseRepository(String tableName) {
        this.connection = new DBConnection().getConnection();
        this.tableName = tableName;
    }

    /**
     * Retorna todas as entidades da tabela do banco.
     */
    public List<T> findAll() {
        List<T> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE ativo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {   
            while (rs.next()) {
                lista.add(mapResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar todos em " + tableName, e);
        }
        
        return lista;
    }

    /**
     * Retorna a entidade pelo ID do banco.
     */
    public T findById(Long id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar por ID em " + tableName, e);
        }
        
        return null;
    }
    
    /**
     * Salva ou atualiza o usuário no banco.
     */
    public T save(T entidade) {
        if (entidade.getId() == null) {
            return insert(entidade);
        } else {
            return update(entidade);
        }
    }

    /**
     * Deleta (SOFT DELETE) o usuário do banco.
     */
    public void deleteById(Long id) {
        String sql = "UPDATE " + tableName + " SET ativo = false WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar em " + tableName, e);
        }
    }
    
    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================
    protected abstract T insert(T entidade);
    protected abstract T update(T entidade);
    protected abstract T mapResultSet(ResultSet rs) throws SQLException;


}