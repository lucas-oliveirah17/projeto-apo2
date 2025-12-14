<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Novo Agendamento - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cadastro.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                
                <div class="d-flex align-items-center gap-3 mb-4">
                    <a href="gestaoAgendamento.jsp" class="btn btn-outline-secondary btn-sm">
                        <i data-lucide="arrow-left"></i>
                    </a>
                    <h2 class="fw-bold mb-0" id="pageTitle">Novo Agendamento</h2>
                </div>

                <div class="card card-cadastro">
                    <div class="card-body p-4">
                        
                        <form id="formAgendamento">
                            <div class="mb-3">
                                <label class="form-label">Cliente</label>
                                <select class="form-select form-control-dark" name="clienteId" id="selectCliente" required>
                                    <option value="" disabled selected>Carregando...</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Profissional</label>
                                <select class="form-select form-control-dark" name="profissionalId" id="selectProfissional" required>
                                    <option value="" disabled selected>Carregando...</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Serviço</label>
                                <select class="form-select form-control-dark" name="servicoId" id="selectServico" required>
                                    <option value="" disabled selected>Carregando...</option>
                                </select>
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Data e Hora</label>
                                <input type="datetime-local" class="form-control form-control-dark" name="dataHoraInicio" required>
                            </div>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary">Agendar</button>
                            </div>
                        </form>

                    </div>
                </div>

            </div>
        </div>
    </div>

    <script src="../js/components/FormManager.js"></script>

    <script>
        // Função para converter "dd/MM/yyyy HH:mm" -> "yyyy-MM-ddTHH:mm"
        // Necessário porque o input datetime-local só aceita formato ISO
        function converterDataParaInput(dataBr) {
            if (!dataBr) return "";
            const [data, hora] = dataBr.split(' ');
            const [dia, mes, ano] = data.split('/');
            return ano + '-' + mes + '-' + dia + 'T' + hora;
        }

        async function carregarListasAuxiliares() {
            try {
                // Busca tudo em paralelo
                const [usuarios, profissionais, servicos] = await Promise.all([
                    Api.get('/usuarios'),
                    Api.get('/profissionais'),
                    Api.get('/servicos')
                ]);

                // 1. Preencher Clientes (Filtra apenas Perfil 3 - Cliente e Ativos)
                const selectCliente = document.getElementById('selectCliente');
                selectCliente.innerHTML = '<option value="" disabled selected>Selecione o Cliente</option>';
                usuarios
                    .filter(u => u.perfilId === 3 && u.ativo)
                    .forEach(u => {
                        const opt = document.createElement('option');
                        opt.value = u.id;
                        opt.textContent = u.nome;
                        selectCliente.appendChild(opt);
                    });

                // 2. Preencher Profissionais (Mostra nome vindo do usuario vinculado)
                const selectProf = document.getElementById('selectProfissional');
                selectProf.innerHTML = '<option value="" disabled selected>Selecione o Profissional</option>';
                profissionais
                    .filter(p => p.ativo)
                    .forEach(p => {
                        const opt = document.createElement('option');
                        opt.value = p.id;
                        // O endpoint /profissionais retorna { nome: "Fulano", ... } graças ao DTO
                        opt.textContent = p.nome + ' (' + p.especialidades + ')';
                        selectProf.appendChild(opt);
                    });

                // 3. Preencher Serviços
                const selectServico = document.getElementById('selectServico');
                selectServico.innerHTML = '<option value="" disabled selected>Selecione o Serviço</option>';
                servicos
                    .filter(s => s.ativo)
                    .forEach(s => {
                        const opt = document.createElement('option');
                        opt.value = s.id;
                        opt.textContent = s.nome + ' (R$ ' + parseFloat(s.preco).toFixed(2).replace('.', ',') + ')';
                        selectServico.appendChild(opt);
                    });

            } catch (error) {
                console.error("Erro ao carregar listas", error);
                alert("Erro ao carregar dados do formulário.");
            }
        }

        document.addEventListener("DOMContentLoaded", async () => {
            // 1. Carrega os dropdowns
            await carregarListasAuxiliares();

            // 2. Inicia o FormManager
            new FormManager({
                endpoint: "/agendamentos",
                formId: "formAgendamento",
                redirectPage: "gestaoAgendamento.jsp",
                // Hook executado quando os dados da edição chegam do Backend
                onLoad: (data) => {
                    // Como os selects já foram carregados no passo 1, o FormManager seleciona os IDs automaticamente
                    if (data.dataHoraInicio) {
                        const inputData = document.getElementsByName('dataHoraInicio')[0];
                        inputData.value = converterDataParaInput(data.dataHoraInicio);
                    }
                }
            });
            
            lucide.createIcons();
        });
    </script>
</body>
</html>