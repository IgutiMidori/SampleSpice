package jp.ac.ttc.webapp.command;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdministratorDao;

public class AdminLoginCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String[] idStrs = reqc.getParameter("id");
        String[] names = reqc.getParameter("name");
        String[] passwords = reqc.getParameter("password");
        
        String idStr = idStrs[0];
        String name = names[0];
        String password = passwords[0];

        AdministratorDao dao = new AdministratorDao();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        UserBean adminUser = dao.findByNameAndId(name, idStr);
        if(encoder.matches(password, adminUser.getPasswordHash())) {
            //認証成功
            System.out.println("成功");
            adminUser.setRole("ADMIN");
            reqc.setSessionAttribute("admin", "OK");
            reqc.setSessionAttribute("adminUser", adminUser);
            String redirect = (String)reqc.getSessionAttribute("redirectAfterLogin");
            if(redirect != null) {
                resc.setRedirect(true);
                resc.setTarget("/spiceEC" + redirect);
            } else {
                resc.setTarget("adminTop");
            }
        } else {
            //認証失敗
            System.out.println("失敗");
            resc.setTarget("adminLogin");
        }

        return resc;
    }
}
