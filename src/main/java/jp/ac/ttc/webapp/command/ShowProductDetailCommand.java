package jp.ac.ttc.webapp.command;


import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.ProductDao;


public class ShowProductDetailCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        //トランザクション開始
        MySqlConnectionManager.getInstance().beginTransaction();
        ProductDao dao = new ProductDao();

        //userIdを取得
        int userId = -1;
        if(getRequestContext().hasSessionAttribute("user") == true){
            userId = ((UserBean)getRequestContext().getSessionAttribute("userBean")).getUserId();
        }

        //商品IDを取得
        int productId = Integer.parseInt(getRequestContext().getParameter("productId")[0]);

        //商品データ1件を取得
        ProductBean product = dao.getProductDetail(userId, productId);
        
        //コネクションを閉じる
        MySqlConnectionManager.getInstance().closeConnection();

        //取得結果をResponseContextに格納
        resc.setResult(product);

        //転送先URLの変数を作成
        String url = "detailProduct";  //仮url

        //転送先URLを指定
        resc.setTarget(url);

        return resc;
    }
}