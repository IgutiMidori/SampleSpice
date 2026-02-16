package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminSpiceDao;

public class EditSpiceGuideCommand extends AbstractCommand{
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String spiceIdStr = reqc.getParameter("spiceId")[0];
        int spiceId = Integer.parseInt(spiceIdStr);
        
        MySqlConnectionManager.getInstance().beginTransaction();
        AdminSpiceDao dao = new AdminSpiceDao();
        SpiceBean bean = dao.getSpiceDetail(spiceId);
        bean.setSpiceId(spiceId);
        MySqlConnectionManager.getInstance().closeConnection();

        resc.setResult(bean);
        resc.setTarget("editSpiceGuide");
        return resc;
    }
}
