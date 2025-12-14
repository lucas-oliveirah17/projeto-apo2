package br.com.chronos.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.chronos.dto.ServicoRequest;
import br.com.chronos.dto.ServicoResponse;
import br.com.chronos.exception.EntityNotFoundException;
import br.com.chronos.model.Servico;
import br.com.chronos.repository.ServicoRepository;

public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService() {
        this.repository = new ServicoRepository();
    }

    public List<ServicoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(ServicoResponse::new)
                .collect(Collectors.toList());
    }

    public ServicoResponse buscarPorId(Long id) {
        Servico servico = repository.findById(id);
        if (servico == null) {
            throw new EntityNotFoundException("Serviço não encontrado.");
        }
        return new ServicoResponse(servico);
    }
    
    // Método auxiliar interno para outros Services usarem
    public Servico findEntidadeById(Long id) {
        Servico servico = repository.findById(id);
        if (servico == null) throw new EntityNotFoundException("Serviço ID " + id + " não encontrado.");
        return servico;
    }

    public ServicoResponse criar(ServicoRequest dto) {        
        Servico novo = dto.toEntity();
        novo.setAtivo(true);
        
        Servico salvo = repository.save(novo);
        return new ServicoResponse(salvo);
    }

    public ServicoResponse atualizar(Long id, ServicoRequest dto) {
        Servico existente = repository.findById(id);
        if (existente == null) throw new EntityNotFoundException("Serviço não encontrado.");

        if (dto.getNome() != null) existente.setNome(dto.getNome());
        if (dto.getDescricao() != null) existente.setDescricao(dto.getDescricao());
        if (dto.getDuracaoMinutos() != null) existente.setDuracaoMinutos(dto.getDuracaoMinutos());
        if (dto.getPreco() != null) existente.setPreco(dto.getPreco());

        Servico atualizado = repository.save(existente);
        return new ServicoResponse(atualizado);
    }

    public void deletar(Long id) {
        if (repository.findById(id) == null) throw new EntityNotFoundException("Serviço não encontrado.");
        repository.deleteById(id);
    }
}