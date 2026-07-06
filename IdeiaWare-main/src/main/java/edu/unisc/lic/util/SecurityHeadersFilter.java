package edu.unisc.lic.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * SEC-26 (2026-07-06): headers de seguranca que o HttpHeaderSecurityFilter nativo do
 * Tomcat (web.xml, SEC-01) nao cobre -- aquele so faz X-Content-Type-Options/
 * X-Frame-Options/HSTS. Referrer-Policy evita vazar a URL completa (que pode ter dado
 * sensivel em query string, ex.: token de reset de senha se um dia existir) pra sites
 * de terceiros quando o usuario clica num link externo (ex.: os links de CDN/fontes).
 *
 * NAO inclui Content-Security-Policy aqui de proposito: a maioria das paginas usa
 * <script>/<style> inline (teria que ser 'unsafe-inline', que reduz bastante o
 * ganho real) + carrega de 4 CDNs diferentes (cdnjs, code.jquery.com,
 * fonts.googleapis.com, maxcdn.bootstrapcdn.com) -- montar e testar uma CSP
 * corretamente exige QA visual manual de cada tela (fontes/icones/scripts quebram
 * silenciosamente se faltar um dominio), decisao deliberada de nao fazer "de
 * passagem" numa varredura automatizada.
 */
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // sem configuracao
    }

    @Override
    public void destroy() {
        // nada a liberar
    }
}
