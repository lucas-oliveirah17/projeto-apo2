<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Gestão de Usuários - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gestao.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="d-flex justify-content-between align-items-center mb-4 page-header">
            <div>
                <h2 class="fw-bold">Usuários</h2>
                <p>Gerencie os acessos do sistema</p>
            </div>
            <a href="cadastroUsuario.jsp" class="btn btn-primary">
                <i data-lucide="plus"></i> Novo Usuário
            </a>
        </div>

        <div id="tabelaUsuarios"></div>
    </div>

    <script src="../js/components/TableManager.js"></script>
    
    <script>
        document.addEventListener("DOMContentLoaded", () => {
            window.currentTableManager = new TableManager({
                entityName: "Usuário",
                endpoint: "/usuarios",
                containerId: "tabelaUsuarios",
                columns: [
                    { label: "ID", key: "id" },
                    { label: "Nome", key: "nome" },
                    { label: "E-mail", key: "email" },
                    { label: "Perfil", key: "perfil" } 
                ]
            });

            window.currentTableManager.init();
        });
    </script>
</body>
</html>