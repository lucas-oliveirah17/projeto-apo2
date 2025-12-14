package br.com.chronos.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.chronos.dto.UsuarioRequest;
import br.com.chronos.dto.UsuarioResponse;
import br.com.chronos.service.UsuarioService;
import br.com.chronos.util.Utilities;

@WebServlet("/api/usuarios/*")
public class UsuarioController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private UsuarioService service;

    public UsuarioController() {
        this.service = new UsuarioService();
    }

    // LISTAR TODOS OU BUSCAR POR ID (GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo(); // Pega o que vem depois de /usuarios

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar Todos
                List<UsuarioResponse> lista = service.listarTodos();
                Utilities.enviarJson(response, lista, HttpServletResponse.SC_OK);
            } else {
                // Buscar por ID (remove a barra inicial)
                Long id = Long.parseLong(pathInfo.substring(1));
                UsuarioResponse usuario = service.buscarPorId(id);
                Utilities.enviarJson(response, usuario, HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // CRIAR (POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Ler o JSON do corpo da requisição
            UsuarioRequest dto = Utilities.lerJson(request, UsuarioRequest.class);
            
            UsuarioResponse criado = service.criar(dto);
            
            Utilities.enviarJson(response, criado, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);        }
    }

    // ATUALIZAR (PUT)
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("ID do usuário é obrigatório.");
            }
            Long id = Long.parseLong(pathInfo.substring(1));
            
            UsuarioRequest dto = Utilities.lerJson(request, UsuarioRequest.class);
            UsuarioResponse atualizado = service.atualizar(id, dto);
            
            Utilities.enviarJson(response, atualizado, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // DELETAR (DELETE)
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("ID é obrigatório.");
            }
            
            Long id = Long.parseLong(pathInfo.substring(1));
            
            service.deletar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}