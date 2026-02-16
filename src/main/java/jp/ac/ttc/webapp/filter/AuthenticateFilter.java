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

public class AuthenticateFilter implements Filter {
    //非ログインユーザーでもアクセスできるページ
    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/all/login",
        "/all/loginProcess",
        "/all/register",
        "/all/start",
        "/all/",
        "/"
    );


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        // Authentication logic would go here
        HttpSession session = ((HttpServletRequest) request).getSession();
        String path = ((HttpServletRequest) request).getServletPath();
                System.out.println("AuthenticateFilter: Accessing path: " + path);
                
        if(PUBLIC_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        if(path.startsWith("/css/") || path.startsWith("/scripts/") || path.startsWith("/images/") ) {
            System.out.println("静的コンテンツ:" + path + "at AuthenticateFilter");
            chain.doFilter(request, response);
            return;
        }
        if(path.contains("/signin")||path.contains("/createUserProcess")||path.contains("/verifySigninOtp")){
            chain.doFilter(request, response);
            return;
        }
        
        if(PUBLIC_PATHS.contains(path.substring(1))) {
            chain.doFilter(request, response);
            return;
        }

        if(path.startsWith("/user")) {
            if(session.getAttribute("user") == null || !((String)session.getAttribute("user")).equals("OK")) {
                // Redirect to login page if not authenticated
                session.setAttribute("redirectAfterLogin", path);
                System.out.println("AuthenticateFilter: Redirect to after login for path: " + path);
                ((HttpServletResponse) response).sendRedirect("/spiceEC/all/login");
                return;
            }
        }
        // Continue the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
