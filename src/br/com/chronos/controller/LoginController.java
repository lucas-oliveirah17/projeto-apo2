package br.com.chronos.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import br.com.chronos.model.Usuario;
import br.com.chronos.service.UsuarioService;
import br.com.chronos.util.Utilities;

@WebServlet("/api/auth/*")
public class LoginController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private UsuarioService service;

    public LoginController() {
        this.service = new UsuarioService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/login".equals(pathInfo)) {
                handleLogin(request, response);
            } else if ("/validar-2fa".equals(pathInfo)) {
                handle2FA(request, response);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response);
            } else {
                response.setStatus(404);
            }
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = Utilities.lerJson(request, JsonObject.class);
        String email = json.get("email").getAsString();
        String senha = json.get("senha").getAsString();

        Usuario usuario = service.autenticarEEnviarCodigo(email, senha);
        
        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", "Código enviado para o e-mail.");
        resposta.addProperty("usuarioId", usuario.getId());
        
        Utilities.enviarJson(response, resposta, HttpServletResponse.SC_OK);
    }

    private void handle2FA(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = Utilities.lerJson(request, JsonObject.class);
        Long usuarioId = json.get("usuarioId").getAsLong();
        String codigo = json.get("codigo").getAsString();

        Usuario usuarioLogado = service.validarCodigo2FA(usuarioId, codigo);

        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogado", usuarioLogado);

        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", "Login realizado com sucesso!");
        resposta.addProperty("nome", usuarioLogado.getNome());
        resposta.addProperty("perfil", usuarioLogado.getPerfil().getDescricao());
        
        Utilities.enviarJson(response, resposta, HttpServletResponse.SC_OK);
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        
        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", "Logout efetuado com sucesso.");
        
        Utilities.enviarJson(response, resposta, HttpServletResponse.SC_OK);
    }
    
}