/*
 *	Desenvolvido por Profº Cleber S. Oliveira 
 *	cleber@ifsp.edu.br / cleber,gulhos@gmail.com
 *	São Paulo, Brasil, 05 de Agosto de 2019  
 */


package br.com.chronos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	
	public Utilities() {
	
	}
	
	// Métodos de Checagem e Validação
	public boolean checkEmail( String email ) {
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
