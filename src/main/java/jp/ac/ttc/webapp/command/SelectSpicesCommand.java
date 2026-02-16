package jp.ac.ttc.webapp.command;


import java.util.List;

import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.SpiceDao;


public class SelectSpicesCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        // Admin top page logic can be implemented here
        MySqlConnectionManager.getInstance().beginTransaction();
        SpiceDao selectSpiceDao = new SpiceDao();
        List<SpiceBean> spicesList = selectSpiceDao.getSpicesIdAndNames();
        MySqlConnectionManager.getInstance().closeConnection();
        resc.setResult(spicesList);

        String url = "selectSpices"; 
        resc.setTarget(url);
        return resc;
    }
    
}
