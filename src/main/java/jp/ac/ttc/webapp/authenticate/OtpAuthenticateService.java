package jp.ac.ttc.webapp.authenticate;

import java.util.Random;

import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.dao.OtpDao;
import jp.ac.ttc.webapp.util.MailUtility;

public class OtpAuthenticateService {
    
    public static boolean generateAndSendOtp(RequestContext reqc, String email, int userId) {
        int otp = new Random().nextInt(900000) + 100000;
        if (MailUtility.sendOTP(email, otp)) {
            OtpDao dao = new OtpDao();
            // Pass the email here!
            dao.saveOTP(userId, email, otp); 
            return true;
        } 
        return false;
    }

    /**
     * Centralized verification logic moved from the Filter.
     * Checks the OTP table for the code assigned to this email.
     */
    public static boolean verifyRegisterOtp(String email, String inputOTPStr) {
        if (email == null || inputOTPStr == null) return false;
        
        try {
            int inputOTP = Integer.parseInt(inputOTPStr);
            OtpDao dao = new OtpDao();
            
            // Step 1: Logic remains in OtpDao but is called through this service
            // This checks the mfa_otp table for temporary storage
            return dao.verifyRegisterOTP(email, inputOTP) == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static int verifyUserAction(int userId,String inputOTPStr){
        if(inputOTPStr == null)return 0;
        try{
            int inputOTP = Integer.parseInt(inputOTPStr);
            OtpDao dao = new OtpDao();
            return dao.verifyOTPStatus(userId, inputOTP);
        }catch(NumberFormatException e){
        return 0;
        }
    }

}