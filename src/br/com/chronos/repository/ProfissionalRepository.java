package br.com.chronos.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import br.com.chronos.model.Profissional;

public class ProfissionalRepository extends BaseRepository<Profissional> {

    private static final String TABLE = "profissionais";
    
    private UsuarioRepository usuarioRepository;

    public ProfissionalRepository() {
        super(TABLE);
        this.usuarioRepository = new UsuarioRepository();
    }

    
    /**
     * Busca o Profissional pelo ID do Usuário vinculado.
     */
    public Profissional findByUsuarioId(Long usuarioId) {
        String sql = "SELECT * FROM " + tableName + " WHERE usuario_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar profissional por ID de Usuário", e);
        }
        
        return null;
    }
    
    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o profissional no banco
     */
    @Override
    protected Profissional insert(Profissional profissional) {
        String sql = "INSERT INTO " + tableName + " (usuario_id, especialidades, ativo) VALUES (?, ?, ?)";
        
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
    @Override
    protected Profissional update(Profissional profissional) {
        String sql = "UPDATE " + tableName + " SET usuario_id=?, especialidades=?, ativo=? WHERE id=?";
        
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

    @Override
    protected Profissional mapResultSet(ResultSet rs) throws SQLException {
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