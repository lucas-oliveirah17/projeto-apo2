package br.com.chronos.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.chronos.model.Profissional;
import br.com.chronos.util.DBConnection;

public class ProfissionalRepository {

    private Connection connection;
    private final String table = "profissionais";
    
    private UsuarioRepository usuarioRepository;

    public ProfissionalRepository() {
        this.connection = new DBConnection().getConnection();
        this.usuarioRepository = new UsuarioRepository();
    }

    /**
     * Retorna todos os profissionais do banco.
     */
    public List<Profissional> findAll() {
        List<Profissional> profissionais = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " WHERE ativo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                profissionais.add(mapResultSetToProfissional(rs));
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar profissionais", e);
        }
        
        return profissionais;
    }

    public Profissional findById(Long id) {
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProfissional(rs);
                }
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar profissional por ID", e);
        }
        
        return null;
    }
    
    /**
     * Busca o Profissional pelo ID do Usuário vinculado.
     */
    public Profissional findByUsuarioId(Long usuarioId) {
        String sql = "SELECT * FROM " + table + " WHERE usuario_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProfissional(rs);
                }
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar profissional por ID de Usuário", e);
        }
        
        return null;
    }
    
    /**
     * Salva ou atualiza o usuário no banco.
     */
    public Profissional save(Profissional profissional) {
        if (profissional.getId() == null) {
            return insert(profissional);
        } else {
            return update(profissional);
        }
    }

    /**
     * Deleta (SOFT DELETE) o profissional do banco.
     */
    public void deleteById(Long id) {
        String sql = "UPDATE " + table + " SET ativo = false WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar profissional", e);
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o profissional no banco
     */
    private Profissional insert(Profissional profissional) {
        String sql = "INSERT INTO " + table + " (usuario_id, especialidades, ativo) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, profissional.getUsuario().getId());
            stmt.setString(2, profissional.getEspecialidades());
            stmt.setBoolean(3, profissional.isAtivo());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profissional.setId(generatedKeys.getLong(1));
                    }
                }
            }
            
            return profissional;
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir profissional", e);
        }
    }

    /**
     * Atualiza o usuário no banco
     */
    private Profissional update(Profissional profissional) {
        String sql = "UPDATE " + table + " SET usuario_id=?, especialidades=?, ativo=? WHERE id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, profissional.getUsuario().getId());
            stmt.setString(2, profissional.getEspecialidades());
            stmt.setBoolean(3, profissional.isAtivo());
            stmt.setLong(4, profissional.getId());
            
            stmt.executeUpdate();
            
            return profissional;
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar profissional", e);
        }
    }

    private Profissional mapResultSetToProfissional(ResultSet rs) throws SQLException {
        Profissional profissional = new Profissional();
        profissional.setId(rs.getLong("id"));
        profissional.setEspecialidades(rs.getString("especialidades"));
        profissional.setAtivo(rs.getBoolean("ativo"));
        
        // RELACIONAMENTO: Busca o objeto Usuario completo usando o ID
        Long usuarioId = rs.getLong("usuario_id");
        if (usuarioId > 0) {
            profissional.setUsuario(usuarioRepository.findById(usuarioId));
        }
        
        return profissional;
    }
}