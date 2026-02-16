package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;


public class SendEditProductCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        Map<String, Object> result = new HashMap<>();
        AdminProductDao dao = new AdminProductDao();

        int currentPage = 1;
        if(reqc.hasParameter("page")) {
            currentPage = Integer.parseInt(reqc.getParameter("page")[0]);
        }
        
        MySqlConnectionManager.getInstance().beginTransaction();
        PageBean bean = new PageBean(currentPage, dao.getProductCount(), PageBean.PRODUCT_PAGE_SIZE);
        List<ProductBean> productList = new ArrayList<>();
        productList = dao.getAllProducts(PageBean.PRODUCT_PAGE_SIZE, bean.getOffset());
        MySqlConnectionManager.getInstance().closeConnection();

        result.put("page", bean);
        result.put("productList", productList);
        resc.setResult(result);
        resc.setTarget("editProduct");
        return resc;
    }
    
}
