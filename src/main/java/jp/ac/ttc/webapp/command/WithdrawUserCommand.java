package jp.ac.ttc.webapp.command;


import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.UserDao;

public class WithdrawUserCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        UserBean user = (UserBean) reqc.getSessionAttribute("userBean");

        if (user != null) {
            int userId = user.getUserId();
            UserDao userDao = new UserDao();
            if (userDao.deleteUser(userId)) {
                getRequestContext().invalidateSession(); // Invalidate session
                resc.setRedirect(true);
                resc.setTarget("productList");
                System.out.println("[DEBUG] WithdrawUserCommand: User withdrawn successfully.");
                return resc;
            }
        }
        System.out.println("[DEBUG] WithdrawUserCommand: Withdrawal failed.");
        resc.setRedirect(true);
        resc.setTarget("userHeader");
        return resc;
    }
}