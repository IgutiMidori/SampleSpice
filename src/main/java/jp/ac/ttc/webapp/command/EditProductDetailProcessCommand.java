package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;

public class EditProductDetailProcessCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String productIdStr = reqc.getParameter("productId")[0];
        String activeFlagStr = reqc.getParameter("activeFlag")[0];
        String capacityStr = reqc.getParameter("capacity")[0];
        String originCountry = reqc.getParameter("originCountry")[0];
        String productDescription = reqc.getParameter("productDescription")[0];
        String priceStr = reqc.getParameter("price")[0];
        String stockQuantityStr = reqc.getParameter("stockQuantity")[0];

        System.out.println("Received parameters: productId=" + productIdStr + ", activeFlag=" + activeFlagStr + ", capacity=" + capacityStr + ", originCountry=" + originCountry + ", productDescription=" + productDescription + ", price=" + priceStr + ", stockQuantity=" + stockQuantityStr);

        int productId = Integer.parseInt(productIdStr);
        boolean activeFlag = Boolean.parseBoolean(activeFlagStr);
        int capacity = Integer.parseInt(capacityStr);
        int price = Integer.parseInt(priceStr);
        int stockQuantity = Integer.parseInt(stockQuantityStr);

        MySqlConnectionManager.getInstance().beginTransaction();
        AdminProductDao dao = new AdminProductDao();
        dao.editProductDetail(productId, activeFlag, capacity, originCountry, productDescription, price, stockQuantity);
        MySqlConnectionManager.getInstance().commit();
        MySqlConnectionManager.getInstance().closeConnection();

        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/editProductDetail?productId=" + productIdStr);
        return resc;
    }
}
