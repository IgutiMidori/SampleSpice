package jp.ac.ttc.webapp.command;

import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.OtpDao;
import jp.ac.ttc.webapp.dao.UserDao;
import jp.ac.ttc.webapp.util.MailUtility;

/**
 * Handles the complete Forgot Password lifecycle:
 * 1. Request OTP
 * 2. Verify OTP
 * 3. Update Password
 */
public class ChangePasswordCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        // Step determines which block of logic to execute based on the JSP hidden input [cite: 2, 3, 5]
        String[] stepParams = reqc.getParameter("step");
        if (stepParams == null || stepParams.length == 0) {
            resc.setTarget("forgotPassword");
            return resc;
        }
        
        String step = stepParams[0]; 
        OtpDao otpDao = new OtpDao();
        UserDao userDao = new UserDao();

        // STEP 1: User enters email and requests OTP [cite: 2]
        if ("request".equals(step)) {
            String email = reqc.getParameter("email")[0];
            UserBean user = userDao.findUserByEmail(email); 

            if (user != null) {
                int otp = new Random().nextInt(900000) + 100000;
                System.out.println("[DEBUG-FORGOT] OTP: " + otp + " for " + email);
                
                if (MailUtility.sendOTP(email, otp)) {
                    // Links OTP to the existing User ID
                    otpDao.saveOTP(user.getUserId(), email, otp); 
                    reqc.setSessionAttribute("resetEmail", email);
                    reqc.setSessionAttribute("otpError", null); // Clear old errors
                    resc.setTarget("forgotPassword"); 
                }
            } else {
                reqc.setSessionAttribute("otpError", "Email address not found.");
                resc.setTarget("forgotPassword");
            }
        } 
        
        // STEP 2: User enters OTP [cite: 3]
        else if ("verify".equals(step)) {
            String email = (String) reqc.getSessionAttribute("resetEmail");
            String inputOtp = reqc.getParameter("inputOTP")[0];
            
            // Verifies code and checks the 30-second expiry
            if (otpDao.verifyRegisterOTP(email, Integer.parseInt(inputOtp)) == 1) {
                System.out.println("[DEBUG-FORGOT] OTP Valid for: " + email);
                // Sets the flag that allows the JSP to show Step 3 [cite: 4, 5]
                reqc.setSessionAttribute("otpVerified", true);
                reqc.setSessionAttribute("otpError", null);
                resc.setTarget("forgotPassword"); 
            } else {
                reqc.setSessionAttribute("otpError", "Invalid or Expired OTP.");
                resc.setTarget("forgotPassword");
            }
        }

        // STEP 3: User enters new password [cite: 5]
        else if ("update".equals(step)) {
            String email = (String) reqc.getSessionAttribute("resetEmail");
            Boolean verified = (Boolean) reqc.getSessionAttribute("otpVerified");
            String newPass = reqc.getParameter("password")[0];
            String confirmPass = reqc.getParameter("confirmpassword")[0];

            // Security check: ensure they actually verified the OTP first [cite: 4]
            if (Boolean.TRUE.equals(verified) && newPass.equals(confirmPass) && email != null) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                userDao.updateUserPassword(email, encoder.encode(newPass));
                
                // Clean up ALL session data so the memory box is empty for next time
                reqc.setSessionAttribute("resetEmail", null);
                reqc.setSessionAttribute("otpVerified", null);
                reqc.setSessionAttribute("otpError", null);
                
                System.out.println("[DEBUG-FORGOT] Password updated for: " + email);
                resc.setTarget("login");
            } else {
                reqc.setSessionAttribute("otpError", "Passwords do not match.");
                resc.setTarget("forgotPassword");
            }
        }

        return resc;
    }
}