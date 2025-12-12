package br.com.chronos.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.chronos.model.Usuario;
import br.com.chronos.model.enums.PerfilUsuario;
import br.com.chronos.util.DBConnection;

public class UsuarioRepository {

    private Connection connection;
    private final String table = "usuarios";

    public UsuarioRepository() {
        this.connection = new DBConnection().getConnection();
    }

    /**
     * Retorna todos os usuários do banco.
     */
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " WHERE ativo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar todos os usuários", e);
        }
        
        return usuarios;
    }

    /**
     * Retorna o usuário pelo ID do banco.
     */
    public Usuario findById(Long id) {
        String sql = "SELECT * FROM " + table + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar usuário por ID", e);
        }
        
        return null;
    }

    /**
     * Retorna o usuário pelo Email do banco.
     */
    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM " + table + " WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar usuário por e-mail", e);
        }
        
        return null;
    }

    /**
     * Salva ou atualiza o usuário no banco.
     */
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            return insert(usuario);
        } else {
            return update(usuario);
        }
    }

    /**
     * Deleta (SOFT DELETE) o usuário do banco.
     */
    public void deleteById(Long id) {
        String sql = "UPDATE " + table + " SET ativo = false WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar usuário", e);
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o usuário no banco
     */
    private Usuario insert(Usuario usuario) {
        String sql = "INSERT INTO " + table + " (nome, email, senha, telefone, perfil, ativo, criado_em) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
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
    private Usuario update(Usuario usuario) {
        // Atualiza inclusive os campos de 2FA se eles estiverem preenchidos no objeto
        String sql = "UPDATE " + table + " SET nome=?, email=?, senha=?, telefone=?, perfil=?, ativo=?, codigo_2fa=?, validade_2fa=? WHERE id=?";
        
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

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
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