package br.com.chronos.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
    
    private Properties properties = new Properties();
    private Session session = null;        
    
    /**
     * @param auth Options: "none" (port 25), "tls" (port 587 or 2525), "ssl" (port 465)
     */
    public SendMail(String smtpHost, String smtpPort, String username, String password, String auth) {
        System.out.println(">>> Configurando SendMail com Auth: " + auth); // Log de Debug
        
        this.properties.put("mail.smtp.host", smtpHost);
        this.properties.put("mail.smtp.port", smtpPort);
        
        // Força TLS 1.2
        this.properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); 
        // Para confiar em qualquer certificado
        this.properties.put("mail.smtp.ssl.trust", "*");
        
        if (auth.equalsIgnoreCase("tls")) {
            this.properties.put("mail.smtp.starttls.enable", "true");
            this.properties.put("mail.smtp.auth", "true");
        }

        if (auth.equalsIgnoreCase("ssl")) {
            this.properties.put("mail.smtp.socketFactory.port", smtpPort);
            this.properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            this.properties.put("mail.smtp.auth", "true");
        }
        
        if (auth.equalsIgnoreCase("none")) {
            this.properties.put("mail.smtp.auth", "false");
        }
        
        this.session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
    
    public void send(String mailFrom, String mailTo, String mailSubject, String mailBody) {
        try {
            System.out.println(">>> Tentando enviar e-mail para: " + mailTo); // Log de Debug
            
            Message message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
            message.setSubject(mailSubject);
            message.setText(mailBody);
            
            Transport.send(message);
            
            System.out.println("Email Enviado para: " + mailTo);
            
        } catch (MessagingException e) {
            // Logar o erro e relançar para o Service tratar (ou derrubar a requisição)
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}

/*	
 *	Exemplo para Google: 
 *		Host:	smtp.gmail.com 
 *		porta:	465, se estiver usando o SSL 
 *		porta: 	587, se estiver usando o TLS
 *	
 *	Secure Socket Layer (SSL) é um padrão global em tecnologia de segurança desenvolvida pela Netscape em 1994. 
 *	Ele cria um canal criptografado entre um servidor web e um navegador (browser) para garantir que todos os 
 *	dados transmitidos sejam sigilosos e seguros. 
 * 
 * 	O TLS (Transport Layer Security) é um protocolo criptográfico cuja função é conferir segurança para a 
 * 	comunicação na Internet para serviços como email (SMTP), navegação por páginas (HTTP) e outros tipos de 
 * 	transferência de dados.
 * 
 * 	O TLS tem a capacidade de trabalhar em portas diferentes e usa algoritmos de criptografia mais fortes como 
 * 	o keyed-Hashing for Message Authentication Code (HMAC) enquanto o SSL apenas Message Authentication Code (MAC). 
 * 
 */
