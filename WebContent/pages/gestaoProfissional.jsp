<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Gestão de Profissionais - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gestao.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="d-flex justify-content-between align-items-center mb-4 page-header">
            <div>
                <h2 class="fw-bold">Profissionais</h2>
                <p>Gerencie os profissionais da clínica</p>
            </div>
            <a href="cadastroProfissional.jsp" class="btn btn-primary">
                <i data-lucide="plus"></i> Novo Profissional
            </a>
        </div>

        <div id="tabelaProfissionais"></div>
    </div>

    <script src="../js/components/TableManager.js"></script>
    
    <script>
        document.addEventListener("DOMContentLoaded", () => {
            window.currentTableManager = new TableManager({
                entityName: "Profissional",
                endpoint: "/profissionais",
                containerId: "tabelaProfissionais",
                columns: [
                    { label: "ID", key: "id" },
                    { label: "Nome", key: "nome" },
                    { label: "Especialidade", key: "especialidade" },
                    { label: "Email", key: "email" },
                    { label: "Telefone", key: "telefone" }
                ]
            });

            window.currentTableManager.init();
        });
    </script>
</body>
</html>
