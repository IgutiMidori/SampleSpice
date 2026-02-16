package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.ResponseContext;

public class UserTopCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc) {
        UserBean user = (UserBean) getRequestContext().getSessionAttribute("userBean");

        getRequestContext().setSessionAttribute("userBean", user);
        String url = "userTop";
        resc.setTarget(url);
        return resc;
    }
    
}
