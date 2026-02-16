package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;

public class EditProductDetailCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        
        int productId = Integer.parseInt(reqc.getParameter("productId")[0]);
        MySqlConnectionManager.getInstance().beginTransaction();
        AdminProductDao dao = new AdminProductDao();
        ProductBean product = dao.getProductById(productId);
        MySqlConnectionManager.getInstance().closeConnection();

        resc.setResult(product);
        resc.setTarget("editProductDetail");
        return resc;
    }
}
