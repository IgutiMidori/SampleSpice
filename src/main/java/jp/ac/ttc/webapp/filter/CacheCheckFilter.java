package jp.ac.ttc.webapp.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CacheCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String uri = req.getRequestURI();

        // --- 3. DEFINE PROTECTED PAGES ---
        // Add any URL that requires a login
        boolean isProtectedPage = 
                uri.contains("/all/sendInfo");

        // Check if user is logged in (This attribute was set up by SessionSetupFilter)
        boolean isLoggedIn = "OK".equals(session.getAttribute("user"));

        // --- 4. ENFORCE SECURITY ---
        if (isProtectedPage && !isLoggedIn) {
            // User is trying to access a private page but is NOT logged in.
            res.sendRedirect(req.getContextPath() + "/all/login");
            return; // STOP execution here. Do not show the page.
        }

        // If allowed, continue to the Servlet/JSP
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
