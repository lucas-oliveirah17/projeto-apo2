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
        
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        // Verifica Conflitos de Horário (Lógica em Memória)
        List<Agendamento> agendaProfissional = repository.findByProfissionalId(profissional.getId());
        
        boolean conflito = agendaProfissional.stream().anyMatch(a -> {
            // Lógica de interseção de horários: (InicioA < FimB) e (FimA > InicioB)
            return inicio.isBefore(a.getDataHoraFim()) && fim.isAfter(a.getDataHoraInicio());
        });

        if (conflito) {
            throw new IllegalArgumentException("O Profissional já possui agendamento neste horário.");
        }

        // 4. Monta e Salva
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
    
    public void cancelar(Long id) {
        Agendamento a = repository.findById(id);
        if (a == null) throw new EntityNotFoundException("Agendamento não encontrado.");
        
        a.setStatus(StatusAgendamento.CANCELADO_PELO_ADMINISTRADOR);
        repository.save(a); 
    }
}