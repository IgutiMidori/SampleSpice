package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminProductDao;

public class ListAdminProductsCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String[] pages = reqc.getParameter("page");
        PageBean bean;
        Map<String, Object> results = new HashMap<>();
        AdminProductDao dao = new AdminProductDao();

        MySqlConnectionManager.getInstance().beginTransaction();
         //ページング情報を設定
        int productCount = dao.getProductCount();
        if(pages == null) {
            bean = new PageBean(1, productCount, PageBean.PRODUCT_PAGE_SIZE);
        } else{
            String page = pages[0];
            bean = new PageBean(Integer.parseInt(page), productCount, PageBean.PRODUCT_PAGE_SIZE);
        }

        // AdminProductDaoを使って管理者用商品リストを取得
        
        List<ProductBean> adminProductList = dao.getAllProducts(PageBean.PRODUCT_PAGE_SIZE, bean.getOffset());
        MySqlConnectionManager.getInstance().closeConnection();

        // 取得した商品リストをレスポンスコンテキストに設定
        results.put("adminProductList", adminProductList);
        results.put("pageBean", bean);
        resc.setResult(results);

        resc.setTarget("adminProductList");

        return resc;
    }
}
