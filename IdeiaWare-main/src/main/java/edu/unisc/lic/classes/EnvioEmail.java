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

        // K.1: chave via env SENDGRID_API_KEY (era hardcoded, vazou no Git -- REVOGAR a antiga).
        String apiKey = System.getenv("SENDGRID_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
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

        // Status 202 = aceito de verdade (antes, retornava true mesmo com a API recusando).
        return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }
}
