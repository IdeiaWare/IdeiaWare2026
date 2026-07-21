package edu.unisc.lic.util;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// RATE-LIMIT: limite de tentativas por IP+endpoint em login/reset/cadastro
public class RateLimitFilter implements Filter {

    private static final Set<String> LIMITADO = new HashSet<>(Arrays.asList(
            "/LogInServlet", "/ResetPasswordServlet", "/CadastroUsuarioServlet"
    ));

    // RATE-LIMIT-TUNE: 10 tentativas / 2min
    private static final int LIMITE_TENTATIVAS = 10;
    private static final long JANELA_MS = 120_000;

    private static final ConcurrentHashMap<String, Deque<Long>> TENTATIVAS = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();
        if ("POST".equalsIgnoreCase(request.getMethod()) && path != null && LIMITADO.contains(path)) {
            String chave = path + "|" + request.getRemoteAddr();
            if (excedeuLimite(chave)) {
                response.sendError(429, "Muitas tentativas. Aguarde um pouco antes de tentar de novo.");
                return;
            }
        }

        chain.doFilter(req, resp);
    }

    private boolean excedeuLimite(String chave) {
        long agora = System.currentTimeMillis();
        Deque<Long> tentativas = TENTATIVAS.computeIfAbsent(chave, k -> new ArrayDeque<>());
        synchronized (tentativas) {
            while (!tentativas.isEmpty() && agora - tentativas.peekFirst() > JANELA_MS) {
                tentativas.pollFirst();
            }
            if (tentativas.size() >= LIMITE_TENTATIVAS) {
                return true;
            }
            tentativas.addLast(agora);
            return false;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
