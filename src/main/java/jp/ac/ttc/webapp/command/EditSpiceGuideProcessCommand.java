package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminSpiceDao;

public class EditSpiceGuideProcessCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String spiceIdStr = reqc.getParameter("spiceId")[0];
        String originCountry = reqc.getParameter("originCountry")[0];
        String priceRange = reqc.getParameter("priceRange")[0];
        String effect = reqc.getParameter("effect")[0];
        String exampleDishes = reqc.getParameter("exampleDishes")[0];
        String overview = reqc.getParameter("overview")[0];
        int spiceId = Integer.parseInt(spiceIdStr);

        SpiceBean bean = new SpiceBean();
        bean.setSpiceId(spiceId);
        bean.setOriginCountry(originCountry);
        bean.setPriceRange(priceRange);
        bean.setEffect(effect);
        bean.setExampleDishes(exampleDishes);
        bean.setOverview(overview);

        MySqlConnectionManager.getInstance().beginTransaction();
        AdminSpiceDao dao = new AdminSpiceDao();
        dao.editSpiceGuide(bean);
        MySqlConnectionManager.getInstance().commit();
        MySqlConnectionManager.getInstance().closeConnection();

        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/editSpiceGuide?spiceId=" + spiceIdStr );
        return resc;
    }
}
