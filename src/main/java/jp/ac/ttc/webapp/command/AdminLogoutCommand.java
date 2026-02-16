package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;

public class AdminLogoutCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        reqc.removeSessionAttribute("admin");
        reqc.removeSessionAttribute("adminUser");
        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/login");
        return resc;
    }
}
