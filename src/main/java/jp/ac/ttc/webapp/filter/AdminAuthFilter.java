package jp.ac.ttc.webapp.filter;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminAuthFilter implements Filter {
    private static final Set<String> ADMIN_PAGES_BEFOR_LOGIN = Set.of(
        "/admin/login",
        "/admin/loginProcess"
    );

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws ServletException, IOException {
            HttpSession session = ((HttpServletRequest) request).getSession();
            String path = ((HttpServletRequest) request).getServletPath();
                System.out.println("AdminAuth: Accessing path: " + path);

            if(path.startsWith("/css") || path.startsWith("/scripts") || path.startsWith("/images")) {
                chain.doFilter(request, response);
                return;
            }

            if(ADMIN_PAGES_BEFOR_LOGIN.contains(path)) {
                chain.doFilter(request, response);
                return;
            }else {
                if(session.getAttribute("admin") == null) {
                    // Redirect to login page if not authenticated
                    String sesatter = (String)session.getAttribute("admin");
                    System.out.println(sesatter);
                    session.setAttribute("redirectAfterLogin", path);
                    System.out.println("AdminAuthFilter: Redirect to after login for path: " + path);
                    ((HttpServletResponse) response).sendRedirect("/spiceEC/admin/login");
                    return;
                }
            }

        chain.doFilter(request, response);
    }
    public void destroy() {
    }
}
