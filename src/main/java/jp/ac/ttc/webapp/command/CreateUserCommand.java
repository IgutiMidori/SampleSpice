package jp.ac.ttc.webapp.command;

import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.OtpDao;
import jp.ac.ttc.webapp.util.MailUtility;

public class CreateUserCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        
        String name = reqc.getParameter("name")[0];
        String email = reqc.getParameter("email")[0];
        String tel = reqc.getParameter("tel")[0];
        String pass = reqc.getParameter("password")[0];
        String confirmPass = reqc.getParameter("confirmpassword")[0];

        // 1. Password Match Check
        if (!pass.equals(confirmPass)) {
            System.out.println("[DEBUG-COMMAND] Password mismatch for: " + email);
            resc.setTarget("signIn"); 
            return resc;
        }

        // 2. Email Availability Check
        OtpDao otpDao = new OtpDao();
        if (!otpDao.isEmailAvailable(email)) {
            System.out.println("[DEBUG-COMMAND] Registration failed: Email " + email + " already exists.");
            resc.setTarget("signIn");
            return resc;
        }

        // 3. Prepare temporary UserBean
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserBean tempUser = new UserBean();
        tempUser.setUserName(name);
        tempUser.setEmail(email);
        tempUser.setPhoneNumber(tel);
        tempUser.setPasswordHash(encoder.encode(pass));

        // 4. Generate OTP (30s expiry logic is handled in the DAO)
        int otp = new Random().nextInt(900000) + 100000;
        System.out.println("[DEBUG-COMMAND] Generated OTP: " + otp + " for: " + email);

        if (MailUtility.sendOTP(email, otp)) {
            // Save OTP to DB for verification
            otpDao.saveOTP(null, email, otp); // userId = 0 for temporary registration
            
            // Store data in session to avoid saving to users table yet
            reqc.setSessionAttribute("tempRegisterUser", tempUser);
            reqc.setSessionAttribute("verifyingEmail", email);
            
            System.out.println("[DEBUG-COMMAND] OTP sent and user data cached in session for: " + email);
            resc.setTarget("signIn"); // 
            return resc;
        } else {
            System.out.println("[DEBUG-COMMAND] ERROR: MailUtility failed to send email to: " + email);
            resc.setTarget("signIn");
            
        }
        return resc;
    }
}