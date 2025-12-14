package br.com.chronos.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.chronos.dto.ProfissionalRequest;
import br.com.chronos.dto.ProfissionalResponse;
import br.com.chronos.service.ProfissionalService;
import br.com.chronos.util.Utilities;

@WebServlet("/api/profissionais/*")
public class ProfissionalController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private ProfissionalService service;

    public ProfissionalController() {
        this.service = new ProfissionalService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<ProfissionalResponse> lista = service.listarTodos();
                Utilities.enviarJson(response, lista, HttpServletResponse.SC_OK);
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                Utilities.enviarJson(response, service.buscarPorId(id), HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ProfissionalRequest dto = Utilities.lerJson(request, ProfissionalRequest.class);
            ProfissionalResponse criado = service.criar(dto);
            Utilities.enviarJson(response, criado, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) throw new IllegalArgumentException("ID obrigatório.");

            Long id = Long.parseLong(pathInfo.substring(1));
            ProfissionalRequest dto = Utilities.lerJson(request, ProfissionalRequest.class);
            
            Utilities.enviarJson(response, service.atualizar(id, dto), HttpServletResponse.SC_OK);
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) throw new IllegalArgumentException("ID obrigatório.");

            Long id = Long.parseLong(pathInfo.substring(1));
            service.deletar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
}