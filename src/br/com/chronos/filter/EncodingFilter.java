package br.com.chronos.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*") // Aplica para TODAS as rotas da aplicação
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Força a leitura dos parâmetros enviados pelo form em UTF-8
        req.setCharacterEncoding("UTF-8");
        
        // Força a resposta para o navegador ser em UTF-8
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json; charset=UTF-8");

        // Continua o fluxo normal da requisição
        chain.doFilter(request, response);
    }
}