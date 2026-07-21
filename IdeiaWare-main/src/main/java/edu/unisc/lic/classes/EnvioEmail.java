package edu.unisc.lic.classes;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;

public class EnvioEmail {
    public static boolean EnviaEmail(String email, String assunto, String texto) throws IOException{
        String fromEmail = System.getenv("SENDGRID_FROM_EMAIL");
        if (fromEmail == null || fromEmail.isEmpty()) {
            fromEmail = "senhaideiaware@outlook.com";
        }

        String apiKey = System.getenv("SENDGRID_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            // LOG-SENDGRID: loga quando chave nao configurada
            System.err.println("EnvioEmail: SENDGRID_API_KEY nao configurada no ambiente.");
            return false;
        }

        Email from = new Email(fromEmail);
        Email to = new Email(email);
        Content content = new Content("text/plain", texto);
        Mail mail = new Mail(from, assunto, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);

        boolean aceito = response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        if (aceito) {
            // LOG-SENDGRID: loga X-Message-Id pra rastrear entrega
            String messageId = response.getHeaders() != null ? response.getHeaders().get("X-Message-Id") : null;
            System.out.println("EnvioEmail: SendGrid aceitou o envio, status=" + response.getStatusCode()
                    + " messageId=" + messageId);
        } else {
            // LOG-SENDGRID: loga motivo da recusa
            System.err.println("EnvioEmail: SendGrid recusou o envio, status=" + response.getStatusCode()
                    + " body=" + response.getBody());
        }
        return aceito;
    }
}
