package br.com.chronos.exception;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import br.com.chronos.dto.ErrorResponse;

public class GlobalExceptionHandler {
public static void handle(HttpServletResponse response, Exception e) throws IOException {
        
        // Padrão: Erro 500
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = "Ocorreu um erro interno no servidor.";

        // Entidade não encontrado (404)
        if (e instanceof EntityNotFoundException) {
            status = HttpServletResponse.SC_NOT_FOUND;
            message = e.getMessage();
        }
        
        // Erro de Regra de Negócio / Argumentos (400)
        else if (e instanceof IllegalArgumentException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = e.getMessage();
        }

        // Configura e envia o JSON
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(status, message);
        String json = new Gson().toJson(errorResponse);

        response.getWriter().write(json);
    }

}
