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

import jp.ac.ttc.webapp.authenticate.NonLoginUserService;

public class LoginCheckFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // --- 1. PREVENT CACHING (Fixes Back Button Issue) ---
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // --- 2. SETUP GUEST USER (Prevents Null Pointer Errors) ---
        HttpSession session = req.getSession();

        if (session.getAttribute("user") == null) {
            session.setAttribute("user", "NO");
        }

        // If user is not logged in, ensure the "Guest" bean is loaded
        if ("NO".equals(session.getAttribute("user")) || session.getAttribute("userBean") == null) {
            session.setAttribute("userBean", NonLoginUserService.getNonLoginUser());
        }
        System.out.println("user:" + (String)session.getAttribute("user"));
        System.out.println("after login path:" + session.getAttribute("redirectAfterLogin"));
        // Pass to the next filter (LoginCheckFilter)

        String path = ((HttpServletRequest)request).getServletPath();
        if(path.startsWith("/css/") || path.startsWith("/scripts/") || path.startsWith("/images/") ) {
            System.out.println("静的コンテンツ" + path + "at LoginCheckFilter");
            chain.doFilter(request, response);
            return;
        }

        // 次のフィルタ／ターゲットへ処理を渡す
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}