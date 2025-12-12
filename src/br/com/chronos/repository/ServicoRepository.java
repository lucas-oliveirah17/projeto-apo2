package br.com.chronos.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.chronos.model.Servico;
import br.com.chronos.util.DBConnection;

public class ServicoRepository {

    private Connection connection;
    private final String table = "servicos";

    public ServicoRepository() {
        this.connection = new DBConnection().getConnection();
    }

    /**
     * Retorna todos os serviços do banco.
     */
    public List<Servico> findAll() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " WHERE ativo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { 
            while (rs.next()) {
                servicos.add(mapResultSetToServico(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar serviços", e);
        }
        
        return servicos;
    }

    /**
     * Retorna o servico pelo ID do banco.
     */
    public Servico findById(Long id) {
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToServico(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar serviço por ID", e);
        }
        
        return null;
    }

    /**
     * Salva ou atualiza o serviço no banco.
     */
    public Servico save(Servico servico) {
        if (servico.getId() == null) {
            return insert(servico);
        } else {
            return update(servico);
        }
    }
    
    /**
     * Deleta (SOFT DELETE) o serviço do banco.
     */
    public void deleteById(Long id) {
        String sql = "UPDATE " + table + " SET ativo = false WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar serviço", e);
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o serviço no banco
     */
    private Servico insert(Servico servico) {
        String sql = "INSERT INTO " + table + " (nome, descricao, duracao_minutos, preco, ativo) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setInt(3, servico.getDuracaoMinutos());
            stmt.setBigDecimal(4, servico.getPreco());
            stmt.setBoolean(5, servico.isAtivo());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        servico.setId(generatedKeys.getLong(1));
                    }
                }
            }
            
            return servico;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir serviço", e);
        }
    }

    /**
     * Atualiza o serviço no banco
     */
    private Servico update(Servico servico) {
        String sql = "UPDATE " + table + " SET nome=?, descricao=?, duracao_minutos=?, preco=?, ativo=? WHERE id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setInt(3, servico.getDuracaoMinutos());
            stmt.setBigDecimal(4, servico.getPreco());
            stmt.setBoolean(5, servico.isAtivo());
            stmt.setLong(6, servico.getId());
            
            stmt.executeUpdate();
           
            return servico;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar serviço", e);
        }
    }

    private Servico mapResultSetToServico(ResultSet rs) throws SQLException {
        Servico servico = new Servico();
        servico.setId(rs.getLong("id"));
        servico.setNome(rs.getString("nome"));
        servico.setDescricao(rs.getString("descricao"));
        servico.setDuracaoMinutos(rs.getInt("duracao_minutos"));
        servico.setPreco(rs.getBigDecimal("preco"));
        servico.setAtivo(rs.getBoolean("ativo"));
        
        return servico;
    }
}