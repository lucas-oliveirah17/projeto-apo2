<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                <p>Controle de horários e atendimentos</p>
            </div>
            <a href="cadastroAgendamento.jsp" class="btn btn-primary">
                <i data-lucide="calendar-plus"></i> Novo Agendamento
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
                    { label: "Data/Hora", key: "dataHoraInicio" },
                    { label: "Cliente", key: "nomeCliente" },
                    { label: "Profissional", key: "nomeProfissional" },
                    { label: "Serviço", key: "nomeServico" },
                    { 
                        label: "Status", 
                        key: "status",
                        format: function(val) {
                            var color = 'secondary';
                            if (val === 'Pendente') color = 'warning';
                            if (val === 'Confirmado') color = 'success';
                            if (val === 'Concluído') color = 'primary';
                            if (val === 'Cancelado pelo Administrador' || val === 'Cancelado pelo Cliente') color = 'danger';
                            
                            return '<span class="badge bg-' + color + '">' + (val || '-') + '</span>';
                        }
                    }
                ],
                renderActions: function(item) {
                    return '<div class="btn-group" role="group">' +
                        '<button class="btn btn-sm btn-outline-success" onclick="window.alterarStatus(' + item.id + ', \'confirmar\')" title="Confirmar">' +
                            '<i data-lucide="check" size="16"></i>' +
                        '</button>' +
                        '<button class="btn btn-sm btn-outline-warning" onclick="window.alterarStatus(' + item.id + ', \'pendente\')" title="Pendente">' +
                            '<i data-lucide="clock" size="16"></i>' +
                        '</button>' +
                        '<button class="btn btn-sm btn-outline-primary" onclick="window.alterarStatus(' + item.id + ', \'concluir\')" title="Concluir">' +
                            '<i data-lucide="check-circle" size="16"></i>' +
                        '</button>' +
                        '<button class="btn btn-sm btn-outline-secondary" onclick="window.alterarStatus(' + item.id + ', \'cancelar\')" title="Cancelar Agendamento">' +
                            '<i data-lucide="x-circle" size="16"></i>' +
                        '</button>' +
                    '</div>' +
                    '<div class="btn-group ms-2" role="group">' +
                        '<button class="btn btn-sm btn-outline-info" onclick="window.editItem(' + item.id + ')" title="Editar">' +
                            '<i data-lucide="edit-2" size="16"></i>' +
                        '</button>' +
                        '<button class="btn btn-sm btn-outline-danger" onclick="window.deleteItem(' + item.id + ')" title="Excluir (Soft Delete)">' +
                            '<i data-lucide="trash-2" size="16"></i>' +
                        '</button>' +
                    '</div>';
                }
            });

            window.currentTableManager.init();
        });

        // Função global para chamar o Backend trocando o status
        window.alterarStatus = async function(id, acao) {
            try {
                // Chama PUT /api/agendamentos/1?acao=confirmar
                await Api.put('/agendamentos/' + id + '?acao=' + acao, {});
                window.currentTableManager.init(); // Recarrega tabela
            } catch (error) {
                console.error(error);
                alert("Erro ao alterar status: " + (error.message || "Erro desconhecido"));
            }
        };
    </script>
</body>
</html>