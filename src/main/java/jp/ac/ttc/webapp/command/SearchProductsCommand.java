package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
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


public class SearchProductsCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        ProductDao dao = new ProductDao();   //dao取得
        RequestContext reqc = getRequestContext();
        Map<String, Object> result = new HashMap<>();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //入力された検索条件を取得する
        String productName;
        Integer spiceId;
        Integer lowCapacity;
        Integer highCapacity;
        String originCountry;

        //商品名のnullチェック
        if(reqc.hasParameter("productName") == true){
            productName = reqc.getParameter("productName")[0];
            if(reqc.getParameter("productName")[0].isBlank()){
                productName = null;
            }
        }else{
            productName = null;
        }
        //スパイスIDのnullチェック
        if(reqc.hasParameter("spiceId") == true){
            if(reqc.getParameter("spiceId")[0].isBlank()){
                spiceId = null;
            }else{
                spiceId = Integer.valueOf(reqc.getParameter("spiceId")[0]);
            }
        }else{
            spiceId = null;
        }
        //内容量のnullチェック
        if(reqc.hasParameter("capacity") == true){
            String[] capacity = reqc.getParameter("capacity")[0].split("~");
            if(reqc.getParameter("capacity")[0].isBlank()){
                lowCapacity = null;
                highCapacity = null;
            }else{
                lowCapacity = Integer.valueOf(capacity[0]);
                highCapacity = Integer.valueOf(capacity[1]);
            }
        }else{
            lowCapacity = null;
            highCapacity = null;
        }
        //原産国のnullチェック
        if(reqc.hasParameter("originCountry") == true){
            originCountry = reqc.getParameter("originCountry")[0];
            if(reqc.getParameter("originCountry")[0].isBlank()){
                originCountry = null;
            }
        }else{
            originCountry = null;
        }

        

        StringBuilder builder = new StringBuilder();
        if(productName != null && !productName.isBlank()){
            builder.append("productName=").append(productName).append("&");
        }
        if(spiceId != null){
            builder.append("spiceId=").append(spiceId).append("&");
        }
        if(lowCapacity != null && highCapacity != null){
            builder.append("capacity=").append(lowCapacity).append("~").append(highCapacity).append("&");
        }
        if(originCountry != null && !originCountry.isBlank()){
            builder.append("originCountry=").append(originCountry).append("&");
        }

        String searchParam = builder.toString();
        if(searchParam.endsWith("&")) {
            searchParam = searchParam.substring(0, searchParam.length() - 1);
        }

        result.put("searchParam", searchParam);

        //現在のページ数を取得
        int currentPage = 1;
        if(reqc.hasParameter("page")) {
            currentPage = Integer.parseInt(reqc.getParameter("page")[0]);
        }

        if(productName == null && spiceId == null && lowCapacity == null && highCapacity == null && originCountry == null) {
            if(reqc.hasParameter("page")) {
                resc.setRedirect(true);
                resc.setTarget("/spiceEC/?page=" + currentPage);
                return resc;
            }
            reqc.setSessionAttribute("searchError", "検索条件がありません");
            resc.setRedirect(true);
            resc.setTarget("/spiceEC/");
            return resc;
        }

        if(productName == null && spiceId == null) {
            if(lowCapacity != null || highCapacity != null || originCountry != null) {
                reqc.setSessionAttribute("searchError", "商品名を入力するか\r\nスパイスを選択してください");
                resc.setRedirect(true);
                resc.setTarget("/spiceEC/");
                return resc;
            }
        }

        //PageBeanをインスタンス化
        PageBean bean = new PageBean(currentPage, dao.getProductCount(productName, spiceId, lowCapacity, highCapacity, originCountry), PageBean.PRODUCT_PAGE_SIZE);

        //商品データを格納するList
        List<ProductBean> products = new ArrayList<ProductBean>();

        System.out.println("pn:" + productName + " sp:" + spiceId + " lcap:" + lowCapacity + " hcap:" + highCapacity + " ori:" + originCountry);

        //DAOで検索条件の判別を行い分岐する
        products = dao.getSearchProducts(userId, productName, spiceId, lowCapacity, highCapacity, originCountry, PageBean.PRODUCT_PAGE_SIZE, bean.getOffset());


        //セッションにspiceIdとspiceNameが登録されてなければ、セッションに登録する
        if(reqc.hasSessionAttribute("spiceIdAndName") == false){
            SpiceDao spiceDao = new SpiceDao();
            List<SpiceBean> spiceIdAndName = spiceDao.getSpicesIdAndNames();
            reqc.setSessionAttribute("spiceIdAndName", spiceIdAndName);
        }

        //セッションに原産国のデータが保存されていなければ、セッションに登録
        if(reqc.hasSessionAttribute("originCountries") == false){
            List<String> countries = dao.getOriginCountries();
            reqc.setSessionAttribute("originCountries", countries);
        }


        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる

        result.put("products", products);
        result.put("page", bean);
        resc.setResult(result);  //商品データをセット
        String url = "listProduct";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}