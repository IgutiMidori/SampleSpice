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

public class RegistrationOtpFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        // Retrieve the email we are currently verifying from the session
        String email = (String) req.getSession().getAttribute("verifyingEmail");
        String inputOTPStr = req.getParameter("inputOTP");

        // Use the service to verify the OTP
        if (OtpAuthenticateService.verifyRegisterOtp(email, inputOTPStr)) {
            chain.doFilter(request, response);
            return;
        }
        
        // If verification fails, redirect back to signin
        ((HttpServletResponse) response).sendRedirect("signin");
    }
}