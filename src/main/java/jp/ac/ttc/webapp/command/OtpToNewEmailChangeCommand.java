package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.authenticate.OtpAuthenticateService;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.OtpDao;

public class OtpToNewEmailChangeCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        UserBean user = (UserBean) reqc.getSessionAttribute("userBean");
        
        // 1. Get the email parameter safely
        String[] emailParams = reqc.getParameter("newEmail");
        String newEmail = (emailParams != null && emailParams.length > 0) ? emailParams[0] : null;

        System.out.println("[DEBUG-CMD] Checking availability for: " + newEmail);
        
        // 2. Validate Input
        if (newEmail == null || newEmail.trim().isEmpty()) {
             resc.setResult("ERROR: メールアドレスを入力してください");
             resc.setTarget("ajaxResponse");
             return resc;
        }

        OtpDao dao = new OtpDao();

        // 3. CHECK: Is email already taken?
        if (!dao.isEmailAvailable(newEmail)) {
            System.out.println("[DEBUG-CMD] ABORT: Email already registered.");
            // Send simple error string back to AJAX
            resc.setResult("ERROR: このメールアドレスは既に使用されています");
            resc.setTarget("ajaxResponse"); // Make sure you have the ajaxResponse.jsp created previously
            return resc;
        }

        // 4. Generate and Send OTP
        boolean isSent = OtpAuthenticateService.generateAndSendOtp(reqc, newEmail, user.getUserId());
        
        if (isSent) {
            reqc.setSessionAttribute("pendingEmail", newEmail);
            System.out.println("[DEBUG-CMD] Success: OTP sent.");
            resc.setResult("SUCCESS");
        } else {
            resc.setResult("ERROR: メールの送信に失敗しました。時間をおいて再度お試しください。");
        }
        resc.setTarget("ajaxResponse");
        return resc;
    }
}