package br.com.chronos.setup;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import br.com.chronos.dto.AgendamentoRequest;
import br.com.chronos.dto.ProfissionalRequest;
import br.com.chronos.dto.ServicoRequest;
import br.com.chronos.dto.UsuarioRequest;
import br.com.chronos.dto.UsuarioResponse;
import br.com.chronos.dto.ProfissionalResponse;
import br.com.chronos.dto.ServicoResponse;
import br.com.chronos.model.enums.PerfilUsuario;
import br.com.chronos.service.AgendamentoService;
import br.com.chronos.service.ProfissionalService;
import br.com.chronos.service.ServicoService;
import br.com.chronos.service.UsuarioService;

public class DatabaseSeeder {

    private static final UsuarioService usuarioService = new UsuarioService();
    private static final ServicoService servicoService = new ServicoService();
    private static final ProfissionalService profissionalService = new ProfissionalService();
    private static final AgendamentoService agendamentoService = new AgendamentoService();

    public static void main(String[] args) {
        System.out.println(">>> Iniciando Seeder...");

        // 1. Trava de segurança: verifica se já tem usuários
        if (!usuarioService.listarTodos().isEmpty()) {
            System.out.println(">>> Banco de dados já populado. Abortando.");
            return;
        }

        try {
            // 2. Criar Serviços
            criarServicos();

            // 3. Criar Usuários (Admins e Clientes)
            criarAdmins();
            criarClientes();

            // 4. Criar Profissionais (Usuário + Vínculo)
            criarProfissionais();

            // 5. Criar Agendamentos Aleatórios
            criarAgendamentos();

            System.out.println("\n>>> SEED CONCLUÍDO COM SUCESSO!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(">>> Erro ao rodar o seeder: " + e.getMessage());
        }
    }

    private static void criarServicos() {
        System.out.println("Criando Serviços...");
        
        adicionarServico("Corte Masculino", "Corte com máquina e tesoura", 30, new BigDecimal("50.00"));
        adicionarServico("Barba Completa", "Barba com toalha quente e navalha", 45, new BigDecimal("45.00"));
        adicionarServico("Corte e Barba", "Combo completo", 75, new BigDecimal("90.00"));
        adicionarServico("Pezinho", "Acabamento e contorno", 15, new BigDecimal("20.00"));
        adicionarServico("Hidratação Capilar", "Tratamento para os fios", 30, new BigDecimal("60.00"));
    }

    private static void adicionarServico(String nome, String desc, int duracao, BigDecimal preco) {
        ServicoRequest dto = new ServicoRequest();
        dto.setNome(nome);
        dto.setDescricao(desc);
        dto.setDuracaoMinutos(duracao);
        dto.setPreco(preco);
        servicoService.criar(dto);
    }

    private static void criarAdmins() {
        System.out.println("Criando Admins...");
        
        adicionarUsuario("Admin Daniel", "daniel@admin.com", "admin123", "11999990001", PerfilUsuario.ADMINISTRADOR.getCodigo());
        adicionarUsuario("Admin Lucas", "lucas@admin.com", "admin123", "11999990002", PerfilUsuario.ADMINISTRADOR.getCodigo());
    }

    private static void criarClientes() {
        System.out.println("Criando Clientes...");
        for (int i = 1; i <= 20; i++) {
            String telefone = "1198888" + String.format("%04d", i);
            adicionarUsuario("Cliente " + i, "cliente" + i + "@teste.com", "123456", telefone, PerfilUsuario.CLIENTE.getCodigo());
        }
    }

    private static void criarProfissionais() {
        System.out.println("Criando Profissionais...");
        
        String[] especialidades = {
            "Especialista em Corte Clássico", "Especialista em Barba", 
            "Coloração e Design", "Cortes Modernos", "Tratamento Capilar"
        };

        for (int i = 0; i < 5; i++) {
            // 1. Cria o Usuário
            String telefone = "1197777" + String.format("%04d", i);
            UsuarioResponse u = adicionarUsuario("Barbeiro " + (i+1), "profissional" + (i+1) + "@teste.com", "123456", telefone, PerfilUsuario.PROFISSIONAL.getCodigo());
            
            // 2. Promove a Profissional
            ProfissionalRequest pDto = new ProfissionalRequest();
            pDto.setUsuarioId(u.getId());
            pDto.setEspecialidades(especialidades[i]);
            profissionalService.criar(pDto);
        }
    }

    private static UsuarioResponse adicionarUsuario(String nome, String email, String senha, String tel, Integer perfil) {
        UsuarioRequest dto = new UsuarioRequest();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setTelefone(tel);
        dto.setPerfilId(perfil);
        return usuarioService.criar(dto); // O Service já faz o hash da senha!
    }

    private static void criarAgendamentos() {
        System.out.println("Criando Agendamentos...");
        
        List<UsuarioResponse> todosUsuarios = usuarioService.listarTodos();
        // Filtra na lista apenas quem é Cliente (no response vem String "Cliente")
        List<UsuarioResponse> clientes = todosUsuarios.stream()
                .filter(u -> u.getPerfil().equalsIgnoreCase("Cliente"))
                .toList();
        
        List<ProfissionalResponse> profissionais = profissionalService.listarTodos();
        List<ServicoResponse> servicos = servicoService.listarTodos();
        
        Random rand = new Random();
        LocalDateTime agora = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        
        int sucessos = 0;
        int tentativas = 0;

        // Tenta criar 30 agendamentos (pode falhar alguns por conflito, o que é esperado)
        while (sucessos < 20 && tentativas < 100) {
            tentativas++;
            try {
                UsuarioResponse cliente = clientes.get(rand.nextInt(clientes.size()));
                ProfissionalResponse profissional = profissionais.get(rand.nextInt(profissionais.size()));
                ServicoResponse servico = servicos.get(rand.nextInt(servicos.size()));

                // Data aleatória nos próximos 7 dias
                LocalDateTime inicio = agora.plusDays(rand.nextInt(7)).plusHours(rand.nextInt(8));
                
                AgendamentoRequest dto = new AgendamentoRequest();
                dto.setClienteId(cliente.getId());
                dto.setProfissionalId(profissional.getId());
                dto.setServicoId(servico.getId());
                dto.setDataHoraInicio(inicio.toString()); // O DTO aceita String ISO

                agendamentoService.criar(dto); // O Service valida conflitos!
                sucessos++;
                
            } catch (Exception e) {
                // Ignora conflitos gerados pelo Random e tenta de novo
                // System.out.println("Conflito gerado (esperado): " + e.getMessage());
            }
        }
        System.out.println("Agendamentos criados: " + sucessos);
    }
}