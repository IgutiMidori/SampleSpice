package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminSpiceDao;

public class ListAdminSpicesCommand extends AbstractCommand{
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        Map<String, Object> result = new HashMap<>();

        int currentPage = 1;
        if(reqc.hasParameter("page")) {
            currentPage = Integer.parseInt(reqc.getParameter("page")[0]);
        }

        MySqlConnectionManager.getInstance().beginTransaction();
        AdminSpiceDao dao = new AdminSpiceDao();
        PageBean pageBean = new PageBean(currentPage, dao.getSpicesCount(), PageBean.SPICE_PAGE_SIZE);

        List<SpiceBean> spices = dao.getSpices(PageBean.SPICE_PAGE_SIZE, pageBean.getOffset());
        MySqlConnectionManager.getInstance().closeConnection();

        result.put("spices", spices);
        result.put("pageBean", pageBean);
        resc.setResult(result);
        resc.setTarget("adminSpiceList");
        return resc;
    }
}
