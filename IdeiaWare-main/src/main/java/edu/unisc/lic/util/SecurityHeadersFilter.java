package edu.unisc.lic.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

// SEC-26: Referrer-Policy (nao coberto pelo HttpHeaderSecurityFilter nativo do Tomcat, SEC-01).
// K.2: sem CSP de proposito -- exige 'unsafe-inline' + QA visual manual de cada tela (ver relatorio).
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
        // no-op
    }

    @Override
    public void destroy() {
        // no-op
    }
}
