<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Home - Chronos</title>
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 text-center">
                
                <h1 class="display-4 mb-4 fw-bold">Página Inicial</h1>

                <div id="conteudoHome" class="mt-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            const usuario = AuthService.getUsuarioLogado();
            const container = document.getElementById("conteudoHome");

            if (usuario) {
                container.innerHTML = `
                    <div class="p-5 rounded-3 border border-secondary" style="background: rgba(30, 41, 59, 0.5);">
                        <h2 class="text-primary mb-3">Bem-vindo de volta, \${usuario.nome}!</h2>
                        <p class="lead text-light">Perfil: \${usuario.perfil}</p>
                        <hr class="my-4 border-secondary">
                        <p>Acesse o menu de gestão acima para controlar agendamentos.</p>
                        <a class="btn btn-primary btn-lg" href="gestaoAgendamento.jsp" role="button">
                            <i data-lucide="calendar"></i> Ir para Agendamentos
                        </a>
                    </div>
                `;
            } else {
                container.innerHTML = `
                    <div class="p-5 rounded-3 border border-secondary" style="background: rgba(30, 41, 59, 0.5);">
                        <h2 class="mb-3">Você não está autenticado.</h2>
                        <p class="lead text-light">Por favor, faça login para acessar o sistema.</p>
                        <a href="login.jsp" class="btn btn-primary btn-lg px-4">
                            Login <i data-lucide="log-in" class="ms-2"></i>
                        </a>
                    </div>
                `;
            }
            lucide.createIcons(); // Renderiza ícones injetados
        });
    </script>
</body>
</html>