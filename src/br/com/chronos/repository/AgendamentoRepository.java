package br.com.chronos.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.chronos.model.Agendamento;
import br.com.chronos.model.enums.StatusAgendamento;

public class AgendamentoRepository extends BaseRepository<Agendamento> {

    private static final String TABLE = "agendamentos";
    
    // Repositórios auxiliares para montar os relacionamentos
    private UsuarioRepository usuarioRepository;
    private ProfissionalRepository profissionalRepository;
    private ServicoRepository servicoRepository;

    public AgendamentoRepository() {
        super(TABLE);
        this.usuarioRepository = new UsuarioRepository();
        this.profissionalRepository = new ProfissionalRepository();
        this.servicoRepository = new ServicoRepository();
    }
    
    /**
     * Busca agendamentos de um cliente específico
     */
    public List<Agendamento> findByClienteId(Long clienteId) {
        List<Agendamento> agendamentos = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE cliente_id = ? AND ativo = true ORDER BY data_hora_inicio DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, clienteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agendamentos.add(mapResultSet(rs));
                }
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar agendamento por ID de Cliente", e);
        }
        
        return agendamentos;
    }
    
    /**
     * Busca agendamentos de um profissional específico
     */
    public List<Agendamento> findByProfissionalId(Long profissionalId) {
        List<Agendamento> agendamentos = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE profissional_id = ? AND ativo = true ORDER BY data_hora_inicio DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, profissionalId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agendamentos.add(mapResultSet(rs));
                }
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar agendamento por ID de Profissional", e);
        }
        
        return agendamentos;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Insere o usuário no banco
     */
    @Override
    protected Agendamento insert(Agendamento agendamento) {
        String sql = "INSERT INTO " + tableName + " (cliente_id, profissional_id, servico_id, data_hora_inicio, data_hora_fim, status, ativo, criado_em, atualizado_em) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {    
            stmt.setLong(1, agendamento.getCliente().getId());
            stmt.setLong(2, agendamento.getProfissional().getId());
            stmt.setLong(3, agendamento.getServico().getId());
            
            stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getDataHoraInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(agendamento.getDataHoraFim()));
            
            stmt.setInt(6, agendamento.getStatus().getCodigo()); // Salva o ID do Enum
            stmt.setBoolean(7, agendamento.isAtivo());
            
            stmt.setTimestamp(8, Timestamp.valueOf(agendamento.getCriadoEm()));
            stmt.setTimestamp(9, agendamento.getAtualizadoEm() != null ? Timestamp.valueOf(agendamento.getAtualizadoEm()) : null);

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        agendamento.setId(generatedKeys.getLong(1));
                    }
                }
            }
            
            return agendamento;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir agendamento", e);
        }
    }

    /**
     * Atualiza o usuário no banco
     */
    @Override
    protected Agendamento update(Agendamento agendamento) {
        // Geralmente no update não mudamos o cliente ou criado_em
        String sql = "UPDATE " + tableName + " SET profissional_id=?, servico_id=?, data_hora_inicio=?, data_hora_fim=?, status=?, atualizado_em=?, ativo=? WHERE id=?";
       
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, agendamento.getProfissional().getId());
            stmt.setLong(2, agendamento.getServico().getId());
            
            stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHoraInicio()));
            stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getDataHoraFim()));
            
            stmt.setInt(5, agendamento.getStatus().getCodigo());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now())); // Atualiza data de modificação
            stmt.setBoolean(7, agendamento.isAtivo());
            
            stmt.setLong(8, agendamento.getId());
            
            stmt.executeUpdate();
            
            return agendamento;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar agendamento", e);
        }
    }

    @Override
    protected Agendamento mapResultSet(ResultSet rs) throws SQLException {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(rs.getLong("id"));
        
        // Conversão de Datas
        agendamento.setDataHoraInicio(rs.getTimestamp("data_hora_inicio").toLocalDateTime());
        agendamento.setDataHoraFim(rs.getTimestamp("data_hora_fim").toLocalDateTime());
        
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) agendamento.setCriadoEm(criadoEm.toLocalDateTime());
        
        Timestamp atualizadoEm = rs.getTimestamp("atualizado_em");
        if (atualizadoEm != null) agendamento.setAtualizadoEm(atualizadoEm.toLocalDateTime());

        // Conversão de Enum
        agendamento.setStatus(StatusAgendamento.toEnum(rs.getInt("status")));
        agendamento.setAtivo(rs.getBoolean("ativo"));
        
        // RELACIONAMENTOS (Preenchendo os objetos completos)
        Long clienteId = rs.getLong("cliente_id");
        Long profissionalId = rs.getLong("profissional_id");
        Long servicoId = rs.getLong("servico_id");
        
        if (clienteId > 0) agendamento.setCliente(usuarioRepository.findById(clienteId));
        if (profissionalId > 0) agendamento.setProfissional(profissionalRepository.findById(profissionalId));
        if (servicoId > 0) agendamento.setServico(servicoRepository.findById(servicoId));

        return agendamento;
    }
}