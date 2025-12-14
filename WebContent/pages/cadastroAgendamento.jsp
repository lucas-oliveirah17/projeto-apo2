<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="pt-br">

    <head>
        <jsp:include page="../components/head.jsp" />
        <title>Cadastro de Agendamento - Chronos</title>
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
                                    <select class="form-select form-control-dark" name="clienteId" id="selectCliente"
                                        required>
                                        <option value="">Selecione um cliente...</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Profissional</label>
                                    <select class="form-select form-control-dark" name="profissionalId"
                                        id="selectProfissional" required>
                                        <option value="">Selecione um profissional...</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Serviço</label>
                                    <select class="form-select form-control-dark" name="servicoId" id="selectServico"
                                        required>
                                        <option value="">Selecione um serviço...</option>
                                    </select>
                                </div>

                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Data</label>
                                        <input type="date" class="form-control form-control-dark" name="data" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Hora</label>
                                        <input type="time" class="form-control form-control-dark" name="hora" required>
                                    </div>
                                </div>

                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">Salvar</button>
                                </div>
                            </form>

                        </div>
                    </div>

                </div>
            </div>
        </div>

        <script src="../js/components/FormManager.js"></script>

        <script>
            document.addEventListener("DOMContentLoaded", async () => {
                new FormManager({
                    endpoint: "/agendamentos",
                    formId: "formAgendamento",
                    redirectPage: "gestaoAgendamento.jsp"
                });
                lucide.createIcons();

                // Populate Selects
                await populateSelect("/usuarios", "selectCliente", "CLIENTE"); // Assuming endpoint supports filtering or we filter clients
                await populateSelect("/profissionais", "selectProfissional");
                await populateSelect("/servicos", "selectServico");
            });

            async function populateSelect(endpoint, elementId, filterProfile = null) {
                try {
                    let data = await Api.get(endpoint);

                    // If it's the User endpoint and we need to filter for CLIENTE
                    if (filterProfile && Array.isArray(data)) {
                        data = data.filter(user => user.perfil === filterProfile);
                    }

                    const select = document.getElementById(elementId);
                    data.forEach(item => {
                        const option = document.createElement("option");
                        option.value = item.id;
                        option.textContent = item.nome;
                        select.appendChild(option);
                    });
                } catch (error) {
                    console.error(`Error loading options for ${elementId}:`, error);
                }
            }
        </script>
    </body>

    </html>