// Constante com o caminho base da sua API
const API_BASE = "/projeto-apo2/api";

/**
 * Função responsável pelo Login
 */
async function fazerLogin(event) {
    event.preventDefault(); // Evita que o formulário recarregue a página

    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;
    const feedback = document.getElementById("feedback");

    // Limpa mensagens anteriores
    feedback.style.display = 'none';
    feedback.innerText = '';

    const payload = {
        email: email,
        senha: senha
    };

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            // Salva o ID do usuário temporariamente para o passo 2
            localStorage.setItem("tempUsuarioId", data.usuarioId);
            
            // Redireciona para a tela de validar código
            window.location.href = "validar-2fa.jsp";
        } else {
            // Erro (ex: Senha incorreta)
            mostrarErro(data.message || "Erro ao fazer login.");
        }

    } catch (error) {
        console.error(error);
        mostrarErro("Erro de conexão com o servidor.");
    }
}

/**
 * Função responsável pelo 2FA (Passo 2)
 */
async function validar2FA(event) {
    event.preventDefault();

    const codigo = document.getElementById("codigo").value;
    const usuarioId = localStorage.getItem("tempUsuarioId"); 

    if (!usuarioId) {
        mostrarErro("Sessão inválida. Faça login novamente.");
        setTimeout(() => window.location.href = "login.jsp", 2000);
        return;
    }

    const payload = {
        usuarioId: parseInt(usuarioId),
        codigo: codigo
    };

    try {
        const response = await fetch(`${API_BASE}/auth/validar-2fa`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            // Limpa o ID temporário
            localStorage.removeItem("tempUsuarioId");
            
            // Salva dados do usuário logado
            sessionStorage.setItem("usuarioLogado", JSON.stringify(data));

            alert("Bem-vindo(a), " + data.nome + "!");
            
            // Redireciona para a Home
            window.location.href = "gestaoAgendamento.jsp"; 
        } else {
            mostrarErro(data.message || "Código inválido.");
        }

    } catch (error) {
        console.error(error);
        mostrarErro("Erro de conexão.");
    }
}

// Função auxiliar para mostrar erros na tela
function mostrarErro(mensagem) {
    const feedback = document.getElementById("feedback");
    feedback.innerText = mensagem;
    feedback.style.display = 'block';
    feedback.className = 'alert alert-danger';
}