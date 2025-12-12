package br.com.chronos.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import br.com.chronos.model.Usuario;
import br.com.chronos.model.enums.PerfilUsuario;

public class UsuarioRepository extends BaseRepository<Usuario> {
    
    private static final String TABLE = "usuarios";

    public UsuarioRepository() {
        super(TABLE);
    }

    /**
     * Retorna o usuário pelo Email do banco.
     */
    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM " + tableName + " WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar usuário por e-mail", e);
        }
        
        return null;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o usuário no banco
     */
    @Override
    protected Usuario insert(Usuario usuario) {
        String sql = "INSERT INTO " + tableName + " (nome, email, senha, telefone, perfil, ativo, criado_em) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTelefone());
            stmt.setInt(5, usuario.getPerfil().getCodigo()); // Salva o ID do Enum (1, 2, 3)
            stmt.setBoolean(6, usuario.isAtivo());
            stmt.setTimestamp(7, Timestamp.valueOf(usuario.getCriadoEm()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getLong(1));
                    }
                }
            }
            
            return usuario;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir usuário", e);
        }
    }

    /**
     * Atualiza o usuário no banco
     */
    @Override
    protected Usuario update(Usuario usuario) {
        // Atualiza inclusive os campos de 2FA se eles estiverem preenchidos no objeto
        String sql = "UPDATE " + tableName + " SET nome=?, email=?, senha=?, telefone=?, perfil=?, ativo=?, codigo_2fa=?, validade_2fa=? WHERE id=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTelefone());
            stmt.setInt(5, usuario.getPerfil().getCodigo());
            stmt.setBoolean(6, usuario.isAtivo());
            
            // Tratamento de nulos para o 2FA
            stmt.setString(7, usuario.getCodigo2FA());
            stmt.setTimestamp(8, usuario.getValidade2FA() != null ? Timestamp.valueOf(usuario.getValidade2FA()) : null);
            
            stmt.setLong(9, usuario.getId());

            stmt.executeUpdate();
            
            return usuario;
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar usuário", e);
        }
    }

    @Override
    protected Usuario mapResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setTelefone(rs.getString("telefone"));
        
        // Conversão do Enum
        usuario.setPerfil(PerfilUsuario.toEnum(rs.getInt("perfil")));
        
        usuario.setAtivo(rs.getBoolean("ativo"));
        
        // Conversão de Datas
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) usuario.setCriadoEm(criadoEm.toLocalDateTime());
        
        // Dados do 2FA
        usuario.setCodigo2FA(rs.getString("codigo_2fa"));
        Timestamp validade = rs.getTimestamp("validade_2fa");
        if (validade != null) usuario.setValidade2FA(validade.toLocalDateTime());

        return usuario;
    }
}