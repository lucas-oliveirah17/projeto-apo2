package br.com.chronos.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.chronos.dto.AgendamentoRequest;
import br.com.chronos.dto.AgendamentoResponse;
import br.com.chronos.service.AgendamentoService;
import br.com.chronos.util.Utilities;

@WebServlet("/api/agendamentos/*")
public class AgendamentoController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private AgendamentoService service;

    public AgendamentoController() {
        this.service = new AgendamentoService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Filtro opcional: ?clienteId=1
                String clienteIdParam = request.getParameter("clienteId");
                List<AgendamentoResponse> lista;
                
                if (clienteIdParam != null) {
                    lista = service.listarPorCliente(Long.parseLong(clienteIdParam));
                } else {
                    lista = service.listarTodos();
                }
                Utilities.enviarJson(response, lista, HttpServletResponse.SC_OK);
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                
                AgendamentoResponse agendamento = service.buscarPorId(id);
                
                Utilities.enviarJson(response, agendamento, HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            AgendamentoRequest dto = Utilities.lerJson(request, AgendamentoRequest.class);
            AgendamentoResponse criado = service.criar(dto);
            Utilities.enviarJson(response, criado, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // DELETE = Cancelar
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) throw new IllegalArgumentException("ID obrigatório.");

            Long id = Long.parseLong(pathInfo.substring(1));
            service.cancelar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            Utilities.enviarErro(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
}