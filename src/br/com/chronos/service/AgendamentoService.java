package br.com.chronos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.chronos.dto.AgendamentoRequest;
import br.com.chronos.dto.AgendamentoResponse;
import br.com.chronos.exception.EntityNotFoundException;
import br.com.chronos.model.Agendamento;
import br.com.chronos.model.Profissional;
import br.com.chronos.model.Servico;
import br.com.chronos.model.Usuario;
import br.com.chronos.model.enums.PerfilUsuario;
import br.com.chronos.model.enums.StatusAgendamento;
import br.com.chronos.repository.AgendamentoRepository;
import br.com.chronos.repository.ProfissionalRepository;
import br.com.chronos.repository.ServicoRepository;
import br.com.chronos.repository.UsuarioRepository;

public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ServicoRepository servicoRepository;

    public AgendamentoService() {
        this.repository = new AgendamentoRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.profissionalRepository = new ProfissionalRepository();
        this.servicoRepository = new ServicoRepository();
    }

    // ... (listarTodos, listarPorCliente, listarPorProfissional, buscarPorId MANTIDOS IGUAIS) ...
    public List<AgendamentoResponse> listarTodos() {
        return repository.findAll().stream().map(AgendamentoResponse::new).collect(Collectors.toList());
    }

    public List<AgendamentoResponse> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream().map(AgendamentoResponse::new).collect(Collectors.toList());
    }
    
    public List<AgendamentoResponse> listarPorProfissional(Long profissionalId) {
        return repository.findByProfissionalId(profissionalId).stream().map(AgendamentoResponse::new).collect(Collectors.toList());
    }
    
    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento agendamento = repository.findById(id);
        if (agendamento == null) throw new EntityNotFoundException("Agendamento não encontrado com id: " + id);
        return new AgendamentoResponse(agendamento);
    }
    // ...

    public AgendamentoResponse criar(AgendamentoRequest dto) {
        Usuario cliente = usuarioRepository.findById(dto.getClienteId());
        if (cliente == null) throw new EntityNotFoundException("Cliente não encontrado.");
        if (cliente.getPerfil() != PerfilUsuario.CLIENTE) throw new IllegalArgumentException("Usuário não é um cliente.");

        Profissional profissional = profissionalRepository.findById(dto.getProfissionalId());
        if (profissional == null) throw new EntityNotFoundException("Profissional não encontrado.");

        Servico servico = servicoRepository.findById(dto.getServicoId());
        if (servico == null) throw new EntityNotFoundException("Serviço não encontrado.");

        Agendamento temp = dto.toEntity(); 
        LocalDateTime inicio = temp.getDataHoraInicio();
        
        if (inicio == null) throw new IllegalArgumentException("Data de início obrigatória.");
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar em datas passadas.");
        }
        
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        validarConflitoHorario(profissional.getId(), inicio, fim, null);

        Agendamento novo = new Agendamento();
        novo.setCliente(cliente);
        novo.setProfissional(profissional);
        novo.setServico(servico);
        novo.setDataHoraInicio(inicio);
        novo.setDataHoraFim(fim);
        novo.setStatus(StatusAgendamento.CONFIRMADO);
        novo.setAtivo(true);
        novo.setCriadoEm(LocalDateTime.now());

        return new AgendamentoResponse(repository.save(novo));
    }
    
    public AgendamentoResponse atualizar(Long id, AgendamentoRequest dto) {
        // ... (MANTIDO IGUAL AO SEU CÓDIGO ANTERIOR) ...
        Agendamento existente = repository.findById(id);
        if (existente == null) throw new EntityNotFoundException("Agendamento não encontrado.");
        
        if (dto.getClienteId() != null) {
            Usuario c = usuarioRepository.findById(dto.getClienteId());
            if (c != null) existente.setCliente(c);
        }
        if (dto.getProfissionalId() != null) {
            Profissional p = profissionalRepository.findById(dto.getProfissionalId());
            if (p != null) existente.setProfissional(p);
        }
        if (dto.getServicoId() != null) {
            Servico s = servicoRepository.findById(dto.getServicoId());
            if (s != null) existente.setServico(s);
        }
        
        if (dto.getDataHoraInicio() != null) {
            LocalDateTime inicio = LocalDateTime.parse(dto.getDataHoraInicio());
            existente.setDataHoraInicio(inicio);
            int duracao = existente.getServico().getDuracaoMinutos();
            existente.setDataHoraFim(inicio.plusMinutes(duracao));
        } else if (dto.getServicoId() != null) {
            int duracao = existente.getServico().getDuracaoMinutos();
            existente.setDataHoraFim(existente.getDataHoraInicio().plusMinutes(duracao));
        }

        validarConflitoHorario(existente.getProfissional().getId(), existente.getDataHoraInicio(), existente.getDataHoraFim(), id);
        
        Agendamento atualizado = repository.save(existente);
        return new AgendamentoResponse(atualizado);
    }
    
    // --- NOVO MÉTODO PARA AÇÕES RÁPIDAS ---
    public AgendamentoResponse alterarStatus(Long id, String acao) {
        Agendamento a = repository.findById(id);
        if (a == null) throw new EntityNotFoundException("Agendamento não encontrado.");

        switch (acao.toLowerCase()) {
            case "confirmar":
                a.setStatus(StatusAgendamento.CONFIRMADO);
                break;
            case "pendente":
                a.setStatus(StatusAgendamento.PENDENTE);
                break;
            case "concluir":
                a.setStatus(StatusAgendamento.CONCLUIDO);
                break;
            case "cancelar":
                a.setStatus(StatusAgendamento.CANCELADO_PELO_ADMINISTRADOR);
                break;
            default:
                throw new IllegalArgumentException("Ação desconhecida: " + acao);
        }
        
        Agendamento salvo = repository.save(a);
        return new AgendamentoResponse(salvo);
    }
    // --------------------------------------

    public void excluir(Long id) {        
        if (repository.findById(id) == null) throw new EntityNotFoundException("Agendamento não encontrado.");
        repository.deleteById(id);
    }
    
    private void validarConflitoHorario(Long profissionalId, LocalDateTime inicio, LocalDateTime fim, Long ignorarAgendamentoId) {
        List<Agendamento> agendaProfissional = repository.findByProfissionalId(profissionalId);
        
        boolean conflito = agendaProfissional.stream()
            .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO_PELO_ADMINISTRADOR 
                      && a.getStatus() != StatusAgendamento.CANCELADO_PELO_CLIENTE 
                      && a.getStatus() != StatusAgendamento.CANCELADO_PELO_PROFISSIONAL)
            .filter(a -> ignorarAgendamentoId == null || !a.getId().equals(ignorarAgendamentoId))
            .anyMatch(a -> {
                return inicio.isBefore(a.getDataHoraFim()) && fim.isAfter(a.getDataHoraInicio());
            });

        if (conflito) {
            throw new IllegalArgumentException("O Profissional já possui agendamento neste horário.");
        }
    }
}