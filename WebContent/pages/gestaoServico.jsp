<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Gestão de Serviços - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gestao.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="d-flex justify-content-between align-items-center mb-4 page-header">
            <div>
                <h2 class="fw-bold">Serviços</h2>
                <p>Gerencie o catálogo de serviços oferecidos</p>
            </div>
            <a href="cadastroServico.jsp" class="btn btn-primary">
                <i data-lucide="plus"></i> Novo Serviço
            </a>
        </div>

        <div id="tabelaServicos"></div>
    </div>

    <script src="../js/components/TableManager.js"></script>
    
    <script>
        document.addEventListener("DOMContentLoaded", () => {
            window.currentTableManager = new TableManager({
                entityName: "Serviço",
                endpoint: "/servicos",
                containerId: "tabelaServicos",
                columns: [
                    { label: "ID", key: "id" },
                    { label: "Nome", key: "nome" },
                    { 
                        label: "Duração", 
                        key: "duracaoMinutos",
                        format: (val) => val ? val + ' min' : '-' 
                    },
                    { 
                        label: "Preço", 
                        key: "preco",
                        format: (val) => val ? 'R$ ' + parseFloat(val).toFixed(2).replace('.', ',') : 'R$ 0,00'
                    },
                    { label: "Descrição", key: "descricao" }
                ]
            });

            window.currentTableManager.init();
        });
    </script>
</body>
</html>