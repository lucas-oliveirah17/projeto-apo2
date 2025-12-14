/**
 * Serviço de Autenticação
 * Gerencia Login, 2FA e Logout
 */

const AuthService = {
    
    // Login (E-mail e Senha)
    login: async (email, senha) => {
        return await Api.post("/auth/login", { email, senha });
    },

    // Validar Código 2FA
    validar2FA: async (usuarioId, codigo) => {
        const payload = {
            usuarioId: parseInt(usuarioId),
            codigo: codigo
        };
        return await Api.post("/auth/validar-2fa", payload);
    },
    
    // Logout
    logout: async () => {
        try {
            // Avisa o servidor para matar a sessão Java
            await Api.post("/auth/logout", {});
        } catch (e) {
            console.warn("Logout no servidor falhou, limpando localmente...", e);
        } finally {
            // Limpa o navegador
            localStorage.removeItem("usuario"); // Dados do usuário
            localStorage.removeItem("tempUsuarioId"); // ID temporário do login
            
            window.location.href = "../pages/home.jsp"; 
        }
    },

    // Método auxiliar para pegar o usuário logado
    getUsuarioLogado: () => {
        const userStr = localStorage.getItem("usuario");
        return userStr ? JSON.parse(userStr) : null;
    },

    // Verifica se está logado
    isAuthenticated: () => {
        return !!localStorage.getItem("usuario");
    }
};