package br.unisc.toolkit.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ErrorController {
 
	// @RequestMapping (sem method) aceita GET e POST. Antes era so @GetMapping,
	// entao um erro durante um POST (ex: export) gerava 405 na propria pagina de
	// erro, mascarando a causa real.
	@RequestMapping("/error")
    public String renderErrorPage(Model theModel, HttpServletRequest httpRequest) {
         
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);
 
        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        theModel.addAttribute("errorMsg", errorMsg);
        
        return "error";
    }
     
    private int getErrorCode(HttpServletRequest httpRequest) {
        // TK-06: acessar /error diretamente (sem erro real) deixava o atributo
        // status_code nulo, e (Integer) null -> int causava NPE na propria pagina
        // de erro. Usa 500 como padrao quando nao ha codigo.
        Object statusCode = httpRequest.getAttribute("javax.servlet.error.status_code");
        return statusCode != null ? (Integer) statusCode : 500;
    }
}
