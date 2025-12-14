package br.com.chronos.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.chronos.dto.ProfissionalRequest;
import br.com.chronos.dto.ProfissionalResponse;
import br.com.chronos.exception.EntityNotFoundException;
import br.com.chronos.model.Profissional;
import br.com.chronos.model.Usuario;
import br.com.chronos.model.enums.PerfilUsuario;
import br.com.chronos.repository.ProfissionalRepository;
import br.com.chronos.repository.UsuarioRepository;

public class ProfissionalService {

    private final ProfissionalRepository repository;
    private final UsuarioRepository usuarioRepository;

    public ProfissionalService() {
        this.repository = new ProfissionalRepository();
        this.usuarioRepository = new UsuarioRepository();
    }

    public List<ProfissionalResponse> listarTodos() {
        return repository.findAll().stream()
                .map(ProfissionalResponse::new)
                .collect(Collectors.toList());
    }

    public ProfissionalResponse buscarPorId(Long id) {
        Profissional p = repository.findById(id);
        if (p == null) throw new EntityNotFoundException("Profissional não encontrado.");
        return new ProfissionalResponse(p);
    }
    
    public Profissional findEntidadeById(Long id) {
        Profissional p = repository.findById(id);
        if (p == null) throw new EntityNotFoundException("Profissional ID " + id + " não encontrado.");
        return p;
    }

    public ProfissionalResponse criar(ProfissionalRequest dto) {
        if (dto.getUsuarioId() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId());
        if (usuario == null) throw new EntityNotFoundException("Usuário não encontrado.");

        // Verifica Perfil
        if (usuario.getPerfil() != PerfilUsuario.PROFISSIONAL) {
            throw new IllegalArgumentException("O usuário selecionado não tem perfil de Profissional.");
        }

        // Verifica se já é profissional
        if (repository.findByUsuarioId(dto.getUsuarioId()) != null) {
            throw new IllegalArgumentException("Este usuário já está cadastrado como profissional.");
        }

        Profissional novo = dto.toEntity();
        novo.setUsuario(usuario); // Garante o objeto completo
        novo.setAtivo(true);

        return new ProfissionalResponse(repository.save(novo));
    }

    public ProfissionalResponse atualizar(Long id, ProfissionalRequest dto) {
        Profissional existente = repository.findById(id);
        if (existente == null) throw new EntityNotFoundException("Profissional não encontrado.");

        if (dto.getEspecialidades() != null) {
            existente.setEspecialidades(dto.getEspecialidades());
        }

        return new ProfissionalResponse(repository.save(existente));
    }

    public void deletar(Long id) {
        if (repository.findById(id) == null) throw new EntityNotFoundException("Profissional não encontrado.");
        repository.deleteById(id);
    }
}