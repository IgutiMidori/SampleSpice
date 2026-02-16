package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.UserDao;

public class FinalizeSigninCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
    RequestContext reqc = getRequestContext();
    UserBean tempUser = (UserBean) reqc.getSessionAttribute("tempRegisterUser");

    if (tempUser != null) {
        UserDao userDao = new UserDao();
        // You will need to create this method in UserDao to perform the INSERT
        if (userDao.registerFinalUser(tempUser)) { 
            System.out.println("[DEBUG-FINALIZE] User " + tempUser.getEmail() + " successfully registered.");
            reqc.setSessionAttribute("tempRegisterUser", null);
            reqc.setSessionAttribute("verifyingEmail", null);
            resc.setTarget("login"); 
            return resc;
        }
    }
    System.out.println("[DEBUG-FINALIZE] Activation failed: No user data in session.");
    resc.setTarget("signIn");
    return resc;
    }
}