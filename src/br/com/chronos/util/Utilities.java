package br.com.chronos.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import br.com.chronos.dto.ErrorResponse;

public class Utilities {
    private static final Gson gson = new Gson();
	
	public Utilities() {
	
	}
	
	// --- MÉTODOS HTTP / JSON
	/**
     * Lê o corpo da requisição (JSON) e converte para um Objeto Java.
     */
    public static <T> T lerJson(HttpServletRequest request, Class<T> classe) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return gson.fromJson(sb.toString(), classe);
    }
    
    /**
     * Configura a resposta como JSON e envia o objeto serializado.
     */
    public static void enviarJson(HttpServletResponse response, Object objeto, int status) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(objeto));
        out.flush();
    }
    
    /**
     * Cria um ErrorResponse padronizado e envia com o status de erro.
     */
    public static void enviarErro(HttpServletResponse response, String mensagem, int status) throws IOException {
        ErrorResponse erro = new ErrorResponse(status, mensagem);
        enviarJson(response, erro, status);
    }
	
	// --- VALIDAÇÕES ---
	/*
	 *  Desenvolvido por Profº Cleber S. Oliveira 
	 *  cleber@ifsp.edu.br / cleber,gulhos@gmail.com
	 *  São Paulo, Brasil, 05 de Agosto de 2019  
	 *  
	 *  Métodos de Checagem e Validação
	 */
	public static boolean checkEmail( String email ) {
		String  patternEmail = "^[a-zA-Z0-9]+[.a-zA-Z0-9]*@[a-zA-Z0-9]+[.a-zA-Z0-9]*[.a-zA-Z0-9]*[.a-zA-Z0-9]*$";
	    Pattern padraoEmail= Pattern.compile(patternEmail, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = padraoEmail.matcher(email);  
	    return( matcher.find() );    
	}
	
	public static String onlyNumbers(String text) {
        if (text == null) return null;
        return text.replaceAll("[^0-9]", "");
    }
}
