package jp.ac.ttc.webapp.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.ttc.webapp.authenticate.OtpAuthenticateService;
import jp.ac.ttc.webapp.bean.UserBean;

public class OtpVerificationFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        UserBean user = (UserBean) req.getSession().getAttribute("userBean");
        String inputOTPStr = req.getParameter("inputOTP");
        String path = req.getServletPath();

        if (path.equals("/all/updateEmail") || path.equals("/all/otpToNewEmail")) {
            chain.doFilter(request, response);
            return;
        }
        if (user != null && inputOTPStr != null) {
            try {
                int status = OtpAuthenticateService.verifyUserAction(user.getUserId(), inputOTPStr);

                if (status == 1) {
                    System.out.println("[DEBUG-FILTER] OTP Valid. Moving to Command.");
                    chain.doFilter(request, response);
                    return;
                } else if (status == 2) {
                    System.out.println("[DEBUG-FILTER] OTP Expired.");
                    res.sendRedirect("sendInfo");
                    return;
                }
            } catch (NumberFormatException e) { }
        }
        System.out.println("[DEBUG-FILTER] OTP Invalid or Missing.");
        res.sendRedirect("sendInfo");
    }
}