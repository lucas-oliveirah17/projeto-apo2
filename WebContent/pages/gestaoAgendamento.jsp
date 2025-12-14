<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="pt-br">

    <head>
        <jsp:include page="../components/head.jsp" />
        <title>Gestão de Agendamentos - Chronos</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gestao.css">
    </head>

    <body>
        <jsp:include page="../components/navbar.jsp" />

        <div class="container mt-5">
            <div class="d-flex justify-content-between align-items-center mb-4 page-header">
                <div>
                    <h2 class="fw-bold">Agendamentos</h2>
                    <p>Gerencie os agendamentos marcados</p>
                </div>
                <a href="cadastroAgendamento.jsp" class="btn btn-primary">
                    <i data-lucide="plus"></i> Novo Agendamento
                </a>
            </div>

            <div id="tabelaAgendamentos"></div>
        </div>

        <script src="../js/components/TableManager.js"></script>

        <script>
            document.addEventListener("DOMContentLoaded", () => {
                window.currentTableManager = new TableManager({
                    entityName: "Agendamento",
                    endpoint: "/agendamentos",
                    containerId: "tabelaAgendamentos",
                    columns: [
                        { label: "ID", key: "id" },
                        { label: "Cliente", key: "clienteNome" },
                        { label: "Profissional", key: "profissionalNome" },
                        { label: "Serviço", key: "servicoNome" },
                        { label: "Data", key: "data" },
                        { label: "Hora", key: "hora" }
                    ]
                });

                window.currentTableManager.init();
            });
        </script>
    </body>

    </html>