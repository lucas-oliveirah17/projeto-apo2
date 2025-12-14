package br.com.chronos.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import br.com.chronos.dto.UsuarioRequest;
import br.com.chronos.dto.UsuarioResponse;
import br.com.chronos.exception.EntityNotFoundException;
import br.com.chronos.model.Usuario;
import br.com.chronos.repository.UsuarioRepository;
import br.com.chronos.util.SendMail;
import br.com.chronos.util.Utilities;

public class UsuarioService {

    private final UsuarioRepository repository;
    
    // Configurações de E-mail
    private static final String SMTP_HOST = "sandbox.smtp.mailtrap.io"; 
    private static final String SMTP_PORT = "2525";
    private static final String SMTP_USER = "USER"; 
    private static final String SMTP_PASS = "PASSWORD";

    public UsuarioService() {
        this.repository = new UsuarioRepository();
    }

    public List<UsuarioResponse> listarTodos() {
        return repository.findAll().stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
    }

    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = repository.findById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuário de ID " + id + " não encontrado.");
        }
        return new UsuarioResponse(usuario);
    }
    
    public UsuarioResponse criar(UsuarioRequest dto) {
        // Validação de E-mail
        if (!Utilities.checkEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        
        // Verifica duplicidade (mesmo inativos)
        if (repository.findByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("O e-mail " + dto.getEmail() + " já está cadastrado.");
        }

        Usuario novoUsuario = dto.toEntity();
        novoUsuario.setCriadoEm(LocalDateTime.now());
        novoUsuario.setAtivo(true);
        
        // Criptografia da senha
        novoUsuario.setSenha(hashSenha(dto.getSenha()));

        Usuario salvo = repository.save(novoUsuario);
        return new UsuarioResponse(salvo);
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {
        Usuario usuarioExistente = repository.findById(id);
        if (usuarioExistente == null) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        // Se mudou o e-mail, verifica se já existe outro
        if (dto.getEmail() != null && !dto.getEmail().equals(usuarioExistente.getEmail())) {
            if (repository.findByEmail(dto.getEmail()) != null) {
                throw new IllegalArgumentException("O e-mail " + dto.getEmail() + " já está em uso.");
            }
            usuarioExistente.setEmail(dto.getEmail());
        }

        if (dto.getNome() != null) usuarioExistente.setNome(dto.getNome());
        if (dto.getTelefone() != null) usuarioExistente.setTelefone(dto.getTelefone());
        
        // Se a senha foi informada, atualiza o hash
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            usuarioExistente.setSenha(hashSenha(dto.getSenha()));
        }

        Usuario atualizado = repository.save(usuarioExistente);
        return new UsuarioResponse(atualizado);
    }

    public void deletar(Long id) {
        if (repository.findById(id) == null) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }
        repository.deleteById(id);
    }

    // --- LÓGICA DE AUTENTICAÇÃO (LOGIN + 2FA) ---

    public Usuario autenticarEEnviarCodigo(String email, String senha) {
        Usuario usuario = repository.findByEmail(email);
        
        if (usuario == null || !usuario.isAtivo()) {
            throw new EntityNotFoundException("Usuário inválido.");
        }
        
        // Verifica Senha (comparando Hash)
        if (!usuario.getSenha().equals(hashSenha(senha))) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        // Gera e Salva Código 2FA
        String codigo = String.format("%06d", new Random().nextInt(999999));
        usuario.setCodigo2FA(codigo);
        usuario.setValidade2FA(LocalDateTime.now().plusMinutes(5));
        repository.save(usuario);

        // Envia Email
        enviarEmailCodigo(usuario, codigo);
        
        return usuario;
    }
    
    public Usuario validarCodigo2FA(Long idUsuario, String codigoDigitado) {
        Usuario usuario = repository.findById(idUsuario);
        if (usuario == null) throw new EntityNotFoundException("Usuário não encontrado.");

        if (LocalDateTime.now().isAfter(usuario.getValidade2FA())) {
            throw new IllegalArgumentException("O código expirou.");
        }
        if (!usuario.getCodigo2FA().equals(codigoDigitado)) {
            throw new IllegalArgumentException("Código incorreto.");
        }
        
        // Limpa o código após uso
        usuario.setCodigo2FA(null);
        usuario.setValidade2FA(null);
        repository.save(usuario);
        
        return usuario;
    }

    // --- MÉTODOS AUXILIARES ---

    private void enviarEmailCodigo(Usuario usuario, String codigo) {
        SendMail mailer = new SendMail(SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS, "tls");
        String corpo = "Olá, " + usuario.getNome() + "!\nSeu código de acesso é: " + codigo;
        mailer.send("no-reply@chronos.com", usuario.getEmail(), "Código de Verificação", corpo);
    }

    private String hashSenha(String senhaPura) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senhaPura.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
}