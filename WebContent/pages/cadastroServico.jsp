<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Cadastro de Serviço - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cadastro.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                
                <div class="d-flex align-items-center gap-3 mb-4">
                    <a href="gestaoServico.jsp" class="btn btn-outline-secondary btn-sm">
                        <i data-lucide="arrow-left"></i>
                    </a>
                    <h2 class="fw-bold mb-0" id="pageTitle">Novo Serviço</h2>
                </div>

                <div class="card card-cadastro">
                    <div class="card-body p-4">
                        
                        <form id="formServico">
                            <div class="mb-3">
                                <label class="form-label">Nome do Serviço</label>
                                <input type="text" class="form-control form-control-dark" name="nome" placeholder="Ex: Corte de Cabelo" required>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Duração (minutos)</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-dark text-light border-secondary"><i data-lucide="clock"></i></span>
                                        <input type="number" class="form-control form-control-dark" name="duracaoMinutos" placeholder="30" min="1" required>
                                    </div>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Preço (R$)</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-dark text-light border-secondary">R$</span>
                                        <input type="number" step="0.01" class="form-control form-control-dark" name="preco" placeholder="0,00" min="0" required>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Descrição</label>
                                <textarea class="form-control form-control-dark" name="descricao" rows="3" placeholder="Detalhes sobre o serviço..."></textarea>
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
        document.addEventListener("DOMContentLoaded", () => {
            new FormManager({
                endpoint: "/servicos",
                formId: "formServico",
                redirectPage: "gestaoServico.jsp"
            });
            lucide.createIcons();
        });
    </script>
</body>
</html>