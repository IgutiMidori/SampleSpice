package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;

public class EditProductCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        String[] productIds  = getRequestContext().getParameter("productId");

        if(productIds == null) {
            resc.setRedirect(true);
            resc.setTarget("/spiceEC/admin/editProduct");
            return resc;
        }

        List<ProductBean> productList = new ArrayList<>();
        AdminProductDao dao = new AdminProductDao();
        
        MySqlConnectionManager.getInstance().beginTransaction();
        for(String productIdStr : productIds) {
            ProductBean product = new ProductBean();

            String[] productNames = getRequestContext().getParameter("productName_" + productIdStr);
            String productName = (productNames != null && productNames.length > 0) ? productNames[0] : null;

            String[] stocks = getRequestContext().getParameter("stockQuantity_" + productIdStr);
            String stockStr = (stocks != null && stocks.length > 0) ? stocks[0] : null;


            String[] orginContries = getRequestContext().getParameter("originCountry_" + productIdStr);
            String originCountry = (orginContries != null && orginContries.length > 0) ? orginContries[0] : null;
            
            String[] prices = getRequestContext().getParameter("price_" + productIdStr);
            String priceStr = (prices != null && prices.length > 0) ? prices[0] : null;

            String[] activeFlags = getRequestContext().getParameter("activeFlag_" + productIdStr);
            String activeFlagStr = (activeFlags != null && activeFlags.length > 0) ? activeFlags[0] :null;

            System.out.println("parameters: " + stockStr + ", " + originCountry + ", " + priceStr + ", " + activeFlagStr);

            int productId = Integer.parseInt(productIdStr);    
            int addStock = Integer.parseInt(stockStr);
            int nowStock = dao.getStockQuantityByProductId(productId);
            int newStock = nowStock + addStock;
            int price = Integer.parseInt(priceStr);
            boolean activeFlag = Boolean.parseBoolean(activeFlagStr);


            product.setProductId(productId);
            product.setProductName(productName);
            product.setStockQuantity(newStock);
            product.setOriginCountry(originCountry);
            product.setPrice(price);
            product.setActiveFlag(activeFlag);

            dao.editProduct(product);
            dao.editProductStatus(product);
            
            productList.add(product);
        }
        MySqlConnectionManager.getInstance().commit();
        MySqlConnectionManager.getInstance().closeConnection();

        
        getRequestContext().setSessionAttribute("productList", productList);
        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/editProduct");
        return resc;
    }
}
