package br.unisc.toolkit.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ErrorController {
 
	@RequestMapping("/error")
    public String renderErrorPage(Model theModel, HttpServletRequest httpRequest) {
         
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);
 
        switch (httpErrorCode) {
            case 400: {
                errorMsg = "A requisição não pôde ser entendida.";
                break;
            }
            case 401: {
                errorMsg = "Você precisa estar autenticado para acessar isso.";
                break;
            }
            // TK-27: caso 403, gerado pelo CsrfInterceptor.
            case 403: {
                errorMsg = "Ação bloqueada por segurança (token inválido ou expirado). Recarregue a página e tente novamente.";
                break;
            }
            case 404: {
                errorMsg = "Página não encontrada.";
                break;
            }
            case 500: {
                errorMsg = "Ocorreu um erro interno. Tente novamente mais tarde.";
                break;
            }
        }
        theModel.addAttribute("errorMsg", errorMsg);
        
        return "error";
    }
     
    private int getErrorCode(HttpServletRequest httpRequest) {
        // TK-06: usa 500 como padrao pra evitar NPE.
        Object statusCode = httpRequest.getAttribute("javax.servlet.error.status_code");
        return statusCode != null ? (Integer) statusCode : 500;
    }
}
