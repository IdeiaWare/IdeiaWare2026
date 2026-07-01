package edu.unisc.lic.classes;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;

/**
 *
 * @author LucasFreitag 2024
 */
public class EnvioEmail {
    public static boolean EnviaEmail(String email, String assunto, String texto) throws IOException{
        String fromEmail = "senhaideiaware@outlook.com";

        // SEC-#5: chave do SendGrid via variavel de ambiente SENDGRID_API_KEY (era hardcoded ->
        // vazou no Git, REVOGAR a antiga). Sem a env definida (ex.: dev), nao envia -> retorna false.
        String apiKey = System.getenv("SENDGRID_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }

        // Cria o objeto de e-mail
        Email from = new Email(fromEmail);
        Email to = new Email(email);
        Content content = new Content("text/plain", texto);
        Mail mail = new Mail(from, assunto, to, content);

        // Envia o e-mail
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        return true;
    }
}
