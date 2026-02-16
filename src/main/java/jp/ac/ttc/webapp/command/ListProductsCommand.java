package jp.ac.ttc.webapp.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.ProductDao;
import jp.ac.ttc.webapp.dao.SpiceDao;


public class ListProductsCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        ProductDao productDao = new ProductDao();   //dao取得
        RequestContext reqc = getRequestContext();
        Map<String, Object> result = new HashMap<>();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //現在のページ数を取得
        int currentPage = 1;
        if(reqc.hasParameter("page")) {
            currentPage = Integer.parseInt(reqc.getParameter("page")[0]);
        }

        //PageBeanインスタンス化
        PageBean page = new PageBean(currentPage, productDao.getProductCount(null, null, null, null, null), PageBean.PRODUCT_PAGE_SIZE);

        //商品データを全件取得
        List<ProductBean> products = productDao.getProducts(userId, PageBean.PRODUCT_PAGE_SIZE, page.getOffset());


        //セッションにspiceIdとspiceNameが登録されてなければ、セッションに登録する
        if(reqc.hasSessionAttribute("spiceIdAndName") == false){
            SpiceDao spiceDao = new SpiceDao();
            List<SpiceBean> spiceIdAndName = spiceDao.getSpicesIdAndNames();
            reqc.setSessionAttribute("spiceIdAndName", spiceIdAndName);
        }

        //セッションに原産国のデータが保存されていなければ、セッションに登録
        if(reqc.hasSessionAttribute("originCountries") == false){
            List<String> countries = productDao.getOriginCountries();
            reqc.setSessionAttribute("originCountries", countries);
        }


        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる
        result.put("products", products);
        result.put("page", page);
        resc.setResult(result);  //商品データをセット
        String url = "listProduct";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}