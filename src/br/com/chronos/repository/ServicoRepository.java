package br.com.chronos.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.chronos.model.Servico;

public class ServicoRepository extends BaseRepository<Servico> {

    private static final String TABLE = "servicos";

    public ServicoRepository() {
        super(TABLE);
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o serviço no banco
     */
    @Override
    protected Servico insert(Servico servico) {
        String sql = "INSERT INTO " + tableName + " (nome, descricao, duracao_minutos, preco, ativo) VALUES (?, ?, ?, ?, ?)";
        
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
    @Override
    protected Servico update(Servico servico) {
        String sql = "UPDATE " + tableName + " SET nome=?, descricao=?, duracao_minutos=?, preco=?, ativo=? WHERE id=?";
        
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

    @Override
    protected Servico mapResultSet(ResultSet rs) throws SQLException {
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