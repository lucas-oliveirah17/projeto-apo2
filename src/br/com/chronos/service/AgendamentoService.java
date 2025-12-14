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

    public List<AgendamentoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(AgendamentoResponse::new)
                .collect(Collectors.toList());
    }

    public List<AgendamentoResponse> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(AgendamentoResponse::new)
                .collect(Collectors.toList());
    }
    
    public List<AgendamentoResponse> listarPorProfissional(Long profissionalId) {
        return repository.findByProfissionalId(profissionalId).stream()
                .map(AgendamentoResponse::new)
                .collect(Collectors.toList());
    }
    
    public AgendamentoResponse buscarPorId(Long id) {
        // Chama o repositório
        Agendamento agendamento = repository.findById(id);
        
        if (agendamento == null) {
            throw new EntityNotFoundException("Agendamento não encontrado com id: " + id);
        }
        
        // Converte para DTO (Response)
        return new AgendamentoResponse(agendamento);
    }

    public AgendamentoResponse criar(AgendamentoRequest dto) {
        // Busca e Valida Entidades
        Usuario cliente = usuarioRepository.findById(dto.getClienteId());
        if (cliente == null) throw new EntityNotFoundException("Cliente não encontrado.");
        if (cliente.getPerfil() != PerfilUsuario.CLIENTE) throw new IllegalArgumentException("Usuário não é um cliente.");

        Profissional profissional = profissionalRepository.findById(dto.getProfissionalId());
        if (profissional == null) throw new EntityNotFoundException("Profissional não encontrado.");

        Servico servico = servicoRepository.findById(dto.getServicoId());
        if (servico == null) throw new EntityNotFoundException("Serviço não encontrado.");

        // Calcula Horários
        Agendamento temp = dto.toEntity(); 
        LocalDateTime inicio = temp.getDataHoraInicio();
        
        if (inicio == null) throw new IllegalArgumentException("Data de início obrigatória.");
        
        // Verifica se a data é no passado
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar em datas passadas.");
        }
        
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        // Verifica Conflitos de Horário (Lógica em Memória)
        validarConflitoHorario(profissional.getId(), inicio, fim, null);

        // Monta e Salva
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
        Agendamento existente = repository.findById(id);
        if (existente == null) throw new EntityNotFoundException("Agendamento não encontrado.");
        
        // Atualiza Cliente (se mudou)
        if (dto.getClienteId() != null) {
            Usuario c = usuarioRepository.findById(dto.getClienteId());
            if (c != null) existente.setCliente(c);
        }

        // Atualiza Profissional (se mudou)
        if (dto.getProfissionalId() != null) {
            Profissional p = profissionalRepository.findById(dto.getProfissionalId());
            if (p != null) existente.setProfissional(p);
        }

        // Atualiza Serviço (se mudou)
        if (dto.getServicoId() != null) {
            Servico s = servicoRepository.findById(dto.getServicoId());
            if (s != null) existente.setServico(s);
        }
        
        // Atualiza Data/Hora e Recalcula Fim
        if (dto.getDataHoraInicio() != null) {
            LocalDateTime inicio = LocalDateTime.parse(dto.getDataHoraInicio());
            existente.setDataHoraInicio(inicio);
            
            // Recalcula o fim baseado na duração do serviço (novo ou existente)
            int duracao = existente.getServico().getDuracaoMinutos();
            existente.setDataHoraFim(inicio.plusMinutes(duracao));
        } else if (dto.getServicoId() != null) {
            // Se mudou só o serviço, mas manteve a hora de inicio, tem que recalcular o fim também
            int duracao = existente.getServico().getDuracaoMinutos();
            existente.setDataHoraFim(existente.getDataHoraInicio().plusMinutes(duracao));
        }

        // Valida conflito excluindo o próprio agendamento (id)
        validarConflitoHorario(existente.getProfissional().getId(), existente.getDataHoraInicio(), existente.getDataHoraFim(), id);
        
        Agendamento atualizado = repository.save(existente);
        return new AgendamentoResponse(atualizado);
    }
    
    public void cancelar(Long id) {
        Agendamento a = repository.findById(id);
        if (a == null) throw new EntityNotFoundException("Agendamento não encontrado.");
        
        a.setStatus(StatusAgendamento.CANCELADO_PELO_ADMINISTRADOR);
        repository.save(a); 
    }
    
    private void validarConflitoHorario(Long profissionalId, LocalDateTime inicio, LocalDateTime fim, Long ignorarAgendamentoId) {
        List<Agendamento> agendaProfissional = repository.findByProfissionalId(profissionalId);
        
        boolean conflito = agendaProfissional.stream()
            .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO_PELO_ADMINISTRADOR 
                      && a.getStatus() != StatusAgendamento.CANCELADO_PELO_CLIENTE 
                      && a.getStatus() != StatusAgendamento.CANCELADO_PELO_PROFISSIONAL)
            // Se for atualização, não compara com ele mesmo
            .filter(a -> ignorarAgendamentoId == null || !a.getId().equals(ignorarAgendamentoId))
            .anyMatch(a -> {
                return inicio.isBefore(a.getDataHoraFim()) && fim.isAfter(a.getDataHoraInicio());
            });

        if (conflito) {
            throw new IllegalArgumentException("O Profissional já possui agendamento neste horário.");
        }
    }
}