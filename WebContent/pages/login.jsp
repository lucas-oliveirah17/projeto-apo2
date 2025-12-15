<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Login - Chronos</title>
    <link rel="stylesheet" href="../css/login.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container login-wrapper">
        <div class="login-card fade-in">
            <div class="text-center mb-4">
                <h2 class="login-title mb-1">Chronos</h2>
                <p class="text-light small">Entre para gerenciar sua barbearia</p>
            </div>

            <div id="feedback" class="alert alert-danger d-none text-center" role="alert"></div>

            <form onsubmit="handleLogin(event)">
                <div class="mb-3">
                    <label for="email" class="form-label text-light small">E-mail</label>
                    <input type="email" class="form-control form-control-dark" id="email" required placeholder="seu@email.com">
                </div>

                <div class="mb-4">
                    <label for="senha" class="form-label text-light small">Senha</label>
                    <input type="password" class="form-control form-control-dark" id="senha" required placeholder="••••••••">
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 d-flex align-items-center justify-content-center gap-2">
                    <span id="btnText">Entrar</span> 
                    <i data-lucide="log-in" size="18"></i>
                </button>
            </form>

            <div class="mt-4 text-center">
                <p class="text-light small">Não tem uma conta? <a href="#" class="text-primary text-decoration-none fw-bold">Cadastre-se</a></p>
            </div>
        </div>
    </div>

    <script>
        async function handleLogin(e) {
            e.preventDefault();
            const email = document.getElementById("email").value;
            const senha = document.getElementById("senha").value;
            const feedback = document.getElementById("feedback");
            const btnText = document.getElementById("btnText");
            
            feedback.classList.add('d-none'); // Esconde erro anterior
            btnText.innerText = "Entrando...";

            try {
                const data = await AuthService.login(email, senha);
                
                // Sucesso: ID temporário salvo, vai para 2FA
                localStorage.setItem("tempUsuarioId", data.usuarioId);
                window.location.href = "validar-2fa.jsp";

            } catch (error) {
                feedback.innerText = error.message || "Credenciais inválidas.";
                feedback.classList.remove('d-none');
            } finally {
                btnText.innerText = "Entrar";
            }
        }
        lucide.createIcons();
    </script>
</body>
</html>