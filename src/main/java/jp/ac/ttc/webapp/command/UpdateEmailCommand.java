package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.authenticate.OtpAuthenticateService;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.OtpDao;

public class UpdateEmailCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        
        // ==========================================
        // FIX: SAFE CHECK FOR CANCEL PARAMETER
        // ==========================================
        String[] cancelParams = reqc.getParameter("cancel");
        
        // Only run this if "cancel" exists AND is "true"
        if (cancelParams != null && cancelParams.length > 0 && "true".equals(cancelParams[0])) {
            reqc.setSessionAttribute("pendingEmail", null);
            reqc.setSessionAttribute("otpError", null);
            
            resc.setRedirect(true);
            resc.setTarget("sendInfo");
            return resc;
        }
        // ==========================================

        // 1. Get Session Data & Input
        UserBean user = (UserBean) reqc.getSessionAttribute("userBean");
        String pendingEmail = (String) reqc.getSessionAttribute("pendingEmail");
        String[] otpParams = reqc.getParameter("inputOTP");
        String inputOtp = (otpParams != null && otpParams.length > 0) ? otpParams[0] : null;

        // 2. Validate Input
        if (user == null || pendingEmail == null) {
            reqc.setSessionAttribute("otpError", "セッションが無効です。最初からやり直してください。");
            resc.setRedirect(true);
            resc.setTarget("sendInfo");
            return resc;
        }

        if (inputOtp == null || inputOtp.isEmpty()) {
            reqc.setSessionAttribute("otpError", "認証コードを入力してください。");
            resc.setRedirect(true);
            resc.setTarget("sendInfo");
            return resc;
        }

        // 3. Verify OTP
        boolean isValid = OtpAuthenticateService.verifyRegisterOtp(pendingEmail, inputOtp);

        if (!isValid) {
            reqc.setSessionAttribute("otpError", "認証コードが間違っています。");
            resc.setRedirect(true);
            resc.setTarget("sendInfo");
            return resc;
        }

        // 4. Update Email
        OtpDao dao = new OtpDao();
        if (dao.updateEmail(user.getUserId(), pendingEmail)) {
            user.setEmail(pendingEmail);
            reqc.setSessionAttribute("userBean", user);
            reqc.setSessionAttribute("pendingEmail", null); 
            reqc.setSessionAttribute("successMessage", "メールアドレスを更新しました！");
        } else {
            reqc.setSessionAttribute("otpError", "データベースエラーが発生しました。");
        }

        resc.setRedirect(true);
        resc.setTarget("sendInfo");
        return resc;
    }
}