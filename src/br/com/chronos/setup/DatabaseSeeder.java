package br.com.chronos.setup;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import br.com.chronos.dto.AgendamentoRequest;
import br.com.chronos.dto.ProfissionalRequest;
import br.com.chronos.dto.ProfissionalResponse;
import br.com.chronos.dto.ServicoRequest;
import br.com.chronos.dto.ServicoResponse;
import br.com.chronos.dto.UsuarioRequest;
import br.com.chronos.dto.UsuarioResponse;
import br.com.chronos.model.enums.PerfilUsuario;
import br.com.chronos.service.AgendamentoService;
import br.com.chronos.service.ProfissionalService;
import br.com.chronos.service.ServicoService;
import br.com.chronos.service.UsuarioService;

public class DatabaseSeeder {

    // Configurações do Banco (Ajuste a senha se necessário)
    private static final String DB_URL_ROOT = "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static UsuarioService usuarioService;
    private static ServicoService servicoService;
    private static ProfissionalService profissionalService;
    private static AgendamentoService agendamentoService;

    public static void main(String[] args) {
        System.out.println(">>> Iniciando Database Seeder Completo...");

        try {
            // 1. RECRIA O BANCO E AS TABELAS (DDL)
            resetDatabase();
            
            // 2. Inicializa os Services (Agora que o banco existe)
            inicializarServices();

            // 3. POPULA OS DADOS (DML)
            System.out.println(">>> Populando dados...");
            criarServicos();
            criarAdmins();
            criarClientes();
            criarProfissionais();
            criarAgendamentos();

            System.out.println("\n>>> SEED E ESTRUTURA CONCLUÍDOS COM SUCESSO! 🚀");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(">>> Erro fatal ao rodar o seeder: " + e.getMessage());
        }
    }

    private static void resetDatabase() throws SQLException {
        System.out.println(">>> Recriando estrutura do Banco de Dados...");

        try (Connection conn = DriverManager.getConnection(DB_URL_ROOT, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            // Comandos SQL (Copiados do seu script)
            String[] sqlCommands = {
                "DROP DATABASE IF EXISTS chronos_apo2",
                "CREATE DATABASE IF NOT EXISTS chronos_apo2 CHARSET utf8mb4",
                "USE chronos_apo2",
                
                // Tabela Usuários
                "CREATE TABLE usuarios (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    nome VARCHAR(100) NOT NULL," +
                "    email VARCHAR(100) NOT NULL UNIQUE," +
                "    senha VARCHAR(255) NOT NULL," +
                "    telefone VARCHAR(20)," +
                "    perfil INT NOT NULL," +
                "    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "    ativo BOOLEAN DEFAULT TRUE," +
                "    codigo_2fa VARCHAR(6)," +
                "    validade_2fa DATETIME" +
                ")",

                // Tabela Serviços
                "CREATE TABLE servicos (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    nome VARCHAR(100) NOT NULL UNIQUE," +
                "    descricao VARCHAR(255)," +
                "    duracao_minutos INT NOT NULL," +
                "    preco DECIMAL(10, 2) NOT NULL," +
                "    ativo BOOLEAN DEFAULT TRUE" +
                ")",

                // Tabela Profissionais
                "CREATE TABLE profissionais (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    usuario_id BIGINT NOT NULL UNIQUE," +
                "    especialidades VARCHAR(255)," +
                "    ativo BOOLEAN DEFAULT TRUE," +
                "    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ")",

                // Tabela Agendamentos
                "CREATE TABLE agendamentos (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    cliente_id BIGINT NOT NULL," +
                "    profissional_id BIGINT NOT NULL," +
                "    servico_id BIGINT NOT NULL," +
                "    data_hora_inicio DATETIME NOT NULL," +
                "    data_hora_fim DATETIME NOT NULL," +
                "    status INT NOT NULL DEFAULT 2," +
                "    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "    atualizado_em DATETIME ON UPDATE CURRENT_TIMESTAMP," +
                "    ativo BOOLEAN DEFAULT TRUE," +
                "    FOREIGN KEY (cliente_id) REFERENCES usuarios(id)," +
                "    FOREIGN KEY (profissional_id) REFERENCES profissionais(id)," +
                "    FOREIGN KEY (servico_id) REFERENCES servicos(id)" +
                ")"
            };

            // Executa linha a linha
            for (String sql : sqlCommands) {
                stmt.execute(sql);
            }
            System.out.println(">>> Tabelas criadas com sucesso.");
        }
    }

    private static void inicializarServices() {
        // Instancia os services apenas após o banco existir para evitar erro de conexão na ConnectionFactory
        usuarioService = new UsuarioService();
        servicoService = new ServicoService();
        profissionalService = new ProfissionalService();
        agendamentoService = new AgendamentoService();
    }

    // --- MÉTODOS DE POPULAÇÃO (Mantidos iguais aos seus) ---

    private static void criarServicos() {
        System.out.println("- Criando Serviços...");
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
        System.out.println("- Criando Admins...");
        adicionarUsuario("Admin Daniel", "daniel@admin.com", "admin123", "11999990001", PerfilUsuario.ADMINISTRADOR.getCodigo());
        adicionarUsuario("Admin Lucas", "lucas@admin.com", "admin123", "11999990002", PerfilUsuario.ADMINISTRADOR.getCodigo());
    }

    private static void criarClientes() {
        System.out.println("- Criando Clientes...");
        for (int i = 1; i <= 20; i++) {
            String telefone = "1198888" + String.format("%04d", i);
            adicionarUsuario("Cliente " + i, "cliente" + i + "@teste.com", "123456", telefone, PerfilUsuario.CLIENTE.getCodigo());
        }
    }

    private static void criarProfissionais() {
        System.out.println("- Criando Profissionais...");
        String[] especialidades = {
            "Especialista em Corte Clássico", "Especialista em Barba", 
            "Coloração e Design", "Cortes Modernos", "Tratamento Capilar"
        };

        for (int i = 0; i < 5; i++) {
            String telefone = "1197777" + String.format("%04d", i);
            UsuarioResponse u = adicionarUsuario("Barbeiro " + (i+1), "profissional" + (i+1) + "@teste.com", "123456", telefone, PerfilUsuario.PROFISSIONAL.getCodigo());
            
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
        return usuarioService.criar(dto);
    }

    private static void criarAgendamentos() {
        System.out.println("- Criando Agendamentos...");
        List<UsuarioResponse> todosUsuarios = usuarioService.listarTodos();
        List<UsuarioResponse> clientes = todosUsuarios.stream()
                .filter(u -> u.getPerfil().equalsIgnoreCase("Cliente"))
                .toList();
        
        List<ProfissionalResponse> profissionais = profissionalService.listarTodos();
        List<ServicoResponse> servicos = servicoService.listarTodos();
        
        Random rand = new Random();
        LocalDateTime agora = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        
        int sucessos = 0;
        int tentativas = 0;

        while (sucessos < 20 && tentativas < 100) {
            tentativas++;
            try {
                UsuarioResponse cliente = clientes.get(rand.nextInt(clientes.size()));
                ProfissionalResponse profissional = profissionais.get(rand.nextInt(profissionais.size()));
                ServicoResponse servico = servicos.get(rand.nextInt(servicos.size()));

                LocalDateTime inicio = agora.plusDays(rand.nextInt(7)).plusHours(rand.nextInt(8));
                
                AgendamentoRequest dto = new AgendamentoRequest();
                dto.setClienteId(cliente.getId());
                dto.setProfissionalId(profissional.getId());
                dto.setServicoId(servico.getId());
                dto.setDataHoraInicio(inicio.toString());

                agendamentoService.criar(dto);
                sucessos++;
                
            } catch (Exception e) {
                // Conflito esperado
            }
        }
        System.out.println("- Agendamentos criados: " + sucessos);
    }
}