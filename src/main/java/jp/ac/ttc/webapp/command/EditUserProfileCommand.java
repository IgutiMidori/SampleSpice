package jp.ac.ttc.webapp.command;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.UserDao;

public class EditUserProfileCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        UserBean sessionUser = (UserBean) reqc.getSessionAttribute("userBean");
        
        // Safety check: if session expired
        if (sessionUser == null) {
            reqc.setSessionAttribute("errorMessage", "セッションが切れました。もう一度ログインしてください。");
            resc.setRedirect(true);
            resc.setTarget("login");
            return resc;
        }

        int userId = sessionUser.getUserId();
        UserDao dao = new UserDao();

        // 1. Get parameters
        String newName = getFirstParam(reqc, "name");
        String rawTel = getFirstParam(reqc, "tel"); 
        String curPass = getFirstParam(reqc, "currentPassword");
        String newPass = getFirstParam(reqc, "newPassword");
        String conPass = getFirstParam(reqc, "confirmPassword");

        // 2. PHONE VALIDATION
        if (rawTel != null && !rawTel.trim().isEmpty()) {
            // Check if it's exactly 11 digits and contains ONLY numbers
            if (rawTel.length() != 11 || !rawTel.matches("\\d+")) {
                System.out.println("[REASON] Validation failed: Phone number " + rawTel + " is invalid.");
                
                // FIX: Use 'phoneError' to trigger the Phone Modal
                reqc.setSessionAttribute("phoneError", "電話番号の形式が正しくありません (ハイフンなしの11桁の数字)");
                
                resc.setRedirect(true);
                resc.setTarget("sendInfo"); // Redirect back to profile page
                return resc;
            }
        }

        // 3. PASSWORD CHANGE LOGIC
        String passwordToUpdate = null;
        
        if (curPass != null && !curPass.isEmpty()) {
            
            // Check 1: Does current password match DB?
            if (!encoder.matches(curPass, sessionUser.getPasswordHash())) {
                // FIX: Use 'passError' to trigger the Password Modal
                reqc.setSessionAttribute("passError", "現在のパスワードが間違っています。");
                
                resc.setRedirect(true);
                resc.setTarget("sendInfo");
                return resc;
            }
            
            // Check 2: Do new passwords match?
            if (newPass == null || !newPass.equals(conPass)) {
                // FIX: Use 'passError' to trigger the Password Modal
                reqc.setSessionAttribute("passError", "新しいパスワードが一致しません。");
                
                resc.setRedirect(true);
                resc.setTarget("sendInfo");
                return resc;
            }
            
            // Check 3: Minimum length
            if (newPass.length() < 4) {
                 reqc.setSessionAttribute("passError", "パスワードは4文字以上で設定してください。");
                 resc.setRedirect(true);
                 resc.setTarget("sendInfo");
                 return resc;
            }

            passwordToUpdate = encoder.encode(newPass);
        }

        // 4. DATABASE UPDATE
        UserBean updatedInfo = dao.editUser(userId, newName, rawTel, passwordToUpdate);

        if (updatedInfo != null) {
            // Update Session
            if (updatedInfo.getUserName() != null) sessionUser.setUserName(updatedInfo.getUserName());
            if (updatedInfo.getPasswordHash() != null) sessionUser.setPasswordHash(updatedInfo.getPasswordHash());
            
            if (rawTel != null && rawTel.length() == 11) {
                String formatted = rawTel.substring(0, 3) + "-" + 
                                   rawTel.substring(3, 7) + "-" + 
                                   rawTel.substring(7);
                sessionUser.setPhoneNumber(formatted);
            }

            reqc.setSessionAttribute("userBean", sessionUser);
            
            // Success Message (Generic top alert)
            reqc.setSessionAttribute("successMessage", "情報を更新しました！");
            
            System.out.println("[SUCCESS] Profile updated for: " + sessionUser.getUserName());
        } else {
            reqc.setSessionAttribute("errorMessage", "更新に失敗しました。システムエラー。");
        }

        resc.setRedirect(true);
        resc.setTarget("sendInfo");
        return resc;
    }

    private String getFirstParam(RequestContext reqc, String key) {
        String[] values = reqc.getParameter(key);
        return (values != null && values.length > 0) ? values[0] : null;
    }
}