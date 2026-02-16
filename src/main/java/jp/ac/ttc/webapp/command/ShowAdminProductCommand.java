package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;

public class ShowAdminProductCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        // 実装予定
        RequestContext reqc = getRequestContext();
        String[] productIds = reqc.getParameter("productId");
        String productId = (productIds != null && productIds.length > 0) ? productIds[0] : null;
        if(productId == null || productId.isEmpty()) {
            resc.setRedirect(true);
            resc.setTarget("/spiceEC/admin/adminProductList");
            return resc;
        }

        ProductBean product;
        AdminProductDao dao = new AdminProductDao();
        product = dao.getProductById(Integer.parseInt(productId));
        reqc.setSessionAttribute("showProduct", product.getProductId());
        resc.setResult(product);
        resc.setTarget("showAdminProduct");
        return resc;
    }
}
