<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Validação 2FA - Chronos</title>
    <link rel="stylesheet" href="../css/login.css">
</head>
<body>
    <div class="container login-wrapper">
        <div class="login-card text-center">
            
            <div class="mb-4 text-primary">
                <i data-lucide="shield-check" width="64" height="64"></i>
            </div>

            <h3 class="fw-bold text-light">Verificação de Segurança</h3>
            <p class="text-light mb-4">Enviamos um código de 6 dígitos para o seu e-mail.</p>

            <div id="feedback" class="alert alert-danger d-none" role="alert"></div>

            <form onsubmit="handle2FA(event)">
                <div class="mb-4 px-5">
                    <input type="text" id="codigo" maxlength="6" required 
                           class="form-control form-control-dark text-center fs-3 letter-spacing-lg" 
                           placeholder="000000" autocomplete="off">
                </div>

                <button type="submit" class="btn btn-primary w-100">Validar Código</button>
                
                <div class="mt-3">
                    <a href="login.jsp" class="text-light small text-decoration-none">Voltar para Login</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        async function handle2FA(e) {
            e.preventDefault();
            const codigo = document.getElementById("codigo").value;
            const usuarioId = localStorage.getItem("tempUsuarioId");
            const feedback = document.getElementById("feedback");

            if(!usuarioId) {
                window.location.href = "login.jsp";
                return;
            }

            try {
                const data = await AuthService.validar2FA(usuarioId, codigo);
                
                // Sucesso Final: Salva usuário e vai para Home
                localStorage.setItem("usuario", JSON.stringify(data));
                localStorage.removeItem("tempUsuarioId");
                
                window.location.href = "home.jsp";

            } catch (error) {
                feedback.innerText = error.message || "Código incorreto.";
                feedback.classList.remove('d-none');
            }
        }
        lucide.createIcons();
    </script>
</body>
</html>