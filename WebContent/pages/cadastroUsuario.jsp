<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Cadastro de Usuário - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cadastro.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                
                <div class="d-flex align-items-center gap-3 mb-4">
                    <a href="gestaoUsuario.jsp" class="btn btn-outline-secondary btn-sm">
                        <i data-lucide="arrow-left"></i>
                    </a>
                    <h2 class="fw-bold mb-0" id="pageTitle">Novo Usuário</h2>
                </div>

                <div class="card card-cadastro">
                    <div class="card-body p-4">
                        
                        <form id="formUsuario">
                            <div class="mb-3">
                                <label class="form-label">Nome Completo</label>
                                <input type="text" class="form-control form-control-dark" name="nome" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">E-mail</label>
                                <input type="email" class="form-control form-control-dark" name="email" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Senha</label>
                                <input type="password" class="form-control form-control-dark" name="senha" placeholder="Preencha apenas para alterar">
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Perfil</label>
                                <select class="form-select form-control-dark" name="perfil" required>
                                    <option value="ADMIN">Administrador</option>
                                    <option value="CLIENTE" selected>Cliente</option>
                                    <option value="PROFISSIONAL">Profissional</option>
                                </select>
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
                endpoint: "/usuarios",
                formId: "formUsuario",
                redirectPage: "gestaoUsuario.jsp"
            });
            lucide.createIcons();
        });
    </script>
</body>
</html>