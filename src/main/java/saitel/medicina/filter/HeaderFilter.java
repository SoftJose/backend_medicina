package saitel.medicina.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import saitel.medicina.service.SesionService;


@Component
@RequiredArgsConstructor
public class HeaderFilter extends OncePerRequestFilter {

    private final SesionService sesionService;

    private static final ThreadLocal<Map<String, String>> headerStorage = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "usuario, sesion, app, ip, Content-Type");
            return;
        }

        String usuario = request.getHeader("usuario");
        String sesion = request.getHeader("sesion");
        String app = request.getHeader("app");
        String ip = Optional.ofNullable(request.getHeader("ip")).orElse("");

        // Almacenar los headers
        Map<String, String> headers = new HashMap<>();
        headers.put("usuario", usuario);
        headers.put("sesion", sesion);
        headers.put("app", app);
        headers.put("ip", ip);
        headerStorage.set(headers);

        // Excluir Actuator para pruebas de k8s
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/actuator") || requestUri.startsWith("/encuesta") || requestUri.startsWith("/encuesta.html") || requestUri.startsWith("/img")) {
            filterChain.doFilter(request, response);
            return;
        }

        if(!sesionService.verificarSesion(usuario, sesion, app)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesion caducada. Ha iniciado sesion en otro dispositivo.");
            return;
        };
        filterChain.doFilter(request, response);
    }

    public static Map<String, String> getHeaders() {
        return headerStorage.get();
    }

    public static void setHeaders(Map<String, String> headersMap) {
        headerStorage.set(headersMap);
    }
}
