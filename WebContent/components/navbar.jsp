<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css">

<nav class="navbar">
    <div class="navbar-container">
        <div class="navbar-brand" onclick="window.location.href='${pageContext.request.contextPath}/pages/home.jsp'">
            <span class="brand-text">Chronos</span>
        </div>

        <div class="mobile-toggle" onclick="toggleMobileMenu()">
            <i data-lucide="menu" id="iconMenu"></i>
            <i data-lucide="x" id="iconClose" class="hidden"></i>
        </div>

        <div class="navbar-links" id="navbarLinks">
            
            <a href="${pageContext.request.contextPath}/pages/home.jsp" class="nav-item">
                <i data-lucide="home" size="18"></i> Home
            </a>

            <div id="navGroupGestao" class="nav-group hidden">
                <span class="nav-group-title">Gestão</span>
                
                <span class="nav-separator">|</span>
                
                <a href="${pageContext.request.contextPath}/pages/gestaoAgendamento.jsp" class="nav-item">
                    <i data-lucide="calendar" size="18"></i> Agendamentos
                </a>
                <a href="${pageContext.request.contextPath}/pages/gestaoProfissional.jsp" class="nav-item">
                    <i data-lucide="id-card" size="18"></i> Profissionais
                </a>
                <a href="${pageContext.request.contextPath}/pages/gestaoServico.jsp" class="nav-item">
                    <i data-lucide="scissors" size="18"></i> Serviços
                </a>
                <a href="${pageContext.request.contextPath}/pages/gestaoUsuario.jsp" class="nav-item">
                    <i data-lucide="user-cog" size="18"></i> Usuários
                </a>
            </div>

            <div class="nav-auth">
                <button id="btnLogin" class="btn btn-primary" onclick="window.location.href='${pageContext.request.contextPath}/pages/login.jsp'">
                    Login
                </button>

                <button id="btnLogout" class="btn btn-secondary logout-btn hidden" onclick="AuthService.logout()">
                    <i data-lucide="log-out" size="18"></i> Sair
                </button>
            </div>
        </div>
    </div>
</nav>

<script>
    // Função para alternar o menu mobile
    function toggleMobileMenu() {
        const links = document.getElementById('navbarLinks');
        const iconMenu = document.getElementById('iconMenu');
        const iconClose = document.getElementById('iconClose');
        
        // Alterna classe active no menu
        links.classList.toggle('active');
        
        // Alterna ícones
        if (links.classList.contains('active')) {
            iconMenu.classList.add('hidden');
            iconClose.classList.remove('hidden');
        } else {
            iconMenu.classList.remove('hidden');
            iconClose.classList.add('hidden');
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        // 1. Checa Login
        const usuario = AuthService.getUsuarioLogado();
        const navGestao = document.getElementById("navGroupGestao");
        const btnLogin = document.getElementById("btnLogin");
        const btnLogout = document.getElementById("btnLogout");

        if (usuario) {
            navGestao.classList.remove("hidden");
            btnLogin.classList.add("hidden");
            btnLogout.classList.remove("hidden");
        } else {
            navGestao.classList.add("hidden");
            btnLogin.classList.remove("hidden");
            btnLogout.classList.add("hidden");
        }

        // 2. Destaca o link ativo (Simulação do NavLink do React)
        const currentPage = window.location.pathname.split("/").pop();
        const links = document.querySelectorAll('.nav-item');
        
        links.forEach(link => {
            if (link.getAttribute('href').includes(currentPage)) {
                link.classList.add('active');
            }
        });

        // 3. Renderiza ícones
        lucide.createIcons();
    });
</script>