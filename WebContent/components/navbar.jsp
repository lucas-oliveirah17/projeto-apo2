<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css">

<nav class="navbar">
    <div class="navbar-container">
        <div class="navbar-brand" onclick="window.location.href='${pageContext.request.contextPath}/pages/home.jsp'">
            <span class="brand-text">Chronos</span>
        </div>

        <div class="navbar-links">
            <a href="${pageContext.request.contextPath}/pages/home.jsp" class="nav-item">
                <i data-lucide="home"></i> Home
            </a>

            <div id="navGroupGestao" class="nav-group hidden">
                <span style="color: var(--text-muted); margin: 0 10px;">|</span>
                
                <a href="${pageContext.request.contextPath}/pages/gestaoAgendamento.jsp" class="nav-item">
                    <i data-lucide="calendar"></i> Agendamentos
                </a>
                <a href="${pageContext.request.contextPath}/pages/gestaoProfissional.jsp" class="nav-item">
                    <i data-lucide="users"></i> Profissionais
                </a>
                <a href="${pageContext.request.contextPath}/pages/gestaoServico.jsp" class="nav-item">
                    <i data-lucide="scissors"></i> Serviços
                </a>
                <a href="${pageContext.request.contextPath}/pages/gestaoUsuario.jsp" class="nav-item">
                    <i data-lucide="user-cog"></i> Usuários
                </a>
            </div>

            <div class="nav-auth">
                <button id="btnLogin" class="btn btn-primary" onclick="window.location.href='${pageContext.request.contextPath}/pages/login.jsp'">
                    Login
                </button>

                <button id="btnLogout" class="btn btn-secondary hidden" onclick="AuthService.logout()">
                    <i data-lucide="log-out"></i> Sair
                </button>
            </div>
        </div>
    </div>
</nav>

<script>
    // Script "Inline" para controlar o estado da Navbar
    document.addEventListener("DOMContentLoaded", () => {
        const usuario = AuthService.getUsuarioLogado();
        
        const navGestao = document.getElementById("navGroupGestao");
        const btnLogin = document.getElementById("btnLogin");
        const btnLogout = document.getElementById("btnLogout");

        if (usuario) {
            // LOGADO
            navGestao.classList.remove("hidden");
            btnLogin.classList.add("hidden");
            btnLogout.classList.remove("hidden");
        } else {
            // NÃO LOGADO
            navGestao.classList.add("hidden");
            btnLogin.classList.remove("hidden");
            btnLogout.classList.add("hidden");
        }

        // Renderiza os ícones
        lucide.createIcons();
    });
</script>