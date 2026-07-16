package edu.unisc.lic.util;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class RateLimitFilterTest {

    private final RateLimitFilter filtro = new RateLimitFilter();

    private HttpServletRequest requestPost(String path, String ip) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn(path);
        when(request.getRemoteAddr()).thenReturn(ip);
        return request;
    }

    @Test
    public void ateOLimite_todasPassam() throws Exception {
        String ip = "10.0.0.1";
        for (int i = 0; i < 10; i++) {
            HttpServletRequest request = requestPost("/LogInServlet", ip);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            filtro.doFilter(request, response, chain);

            verify(chain, times(1)).doFilter(request, response);
            verify(response, never()).sendError(org.mockito.ArgumentMatchers.anyInt(), anyString());
        }
    }

    @Test
    public void acimaDoLimite_bloqueiaCom429ENaoChamaChain() throws Exception {
        String ip = "10.0.0.2";
        for (int i = 0; i < 10; i++) {
            filtro.doFilter(requestPost("/LogInServlet", ip), mock(HttpServletResponse.class), mock(FilterChain.class));
        }

        HttpServletRequest request = requestPost("/LogInServlet", ip);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filtro.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response, times(1)).sendError(eq(429), anyString());
    }

    @Test
    public void ipsDiferentes_naoInterferemEntreSi() throws Exception {
        for (int i = 0; i < 10; i++) {
            filtro.doFilter(requestPost("/ResetPasswordServlet", "10.0.0.3"), mock(HttpServletResponse.class), mock(FilterChain.class));
        }

        HttpServletRequest request = requestPost("/ResetPasswordServlet", "10.0.0.4");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filtro.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(org.mockito.ArgumentMatchers.anyInt(), anyString());
    }

    @Test
    public void pathForaDaLista_nuncaBloqueia() throws Exception {
        String ip = "10.0.0.5";
        for (int i = 0; i < 10; i++) {
            HttpServletRequest request = requestPost("/GerenciarIdeiaServlet", ip);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            filtro.doFilter(request, response, chain);

            verify(chain, times(1)).doFilter(request, response);
            verify(response, never()).sendError(org.mockito.ArgumentMatchers.anyInt(), anyString());
        }
    }

    @Test
    public void get_nuncaBloqueiaMesmoAcimaDoLimite() throws Exception {
        String ip = "10.0.0.6";
        for (int i = 0; i < 15; i++) {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getMethod()).thenReturn("GET");
            when(request.getServletPath()).thenReturn("/LogInServlet");
            when(request.getRemoteAddr()).thenReturn(ip);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            filtro.doFilter(request, response, chain);

            verify(chain, times(1)).doFilter(request, response);
            verify(response, never()).sendError(org.mockito.ArgumentMatchers.anyInt(), anyString());
        }
    }
}
