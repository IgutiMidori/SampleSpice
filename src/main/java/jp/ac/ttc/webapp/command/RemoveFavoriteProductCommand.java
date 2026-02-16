package jp.ac.ttc.webapp.command;


import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.FavoriteProductDao;


public class RemoveFavoriteProductCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        FavoriteProductDao dao = new FavoriteProductDao();   //dao取得
        RequestContext reqc = getRequestContext();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //productIdを取得
        int productId = -1;
        if(reqc.hasParameter("productId") == true){
            if(reqc.getParameter("productId")[0].isBlank() == false){
                productId = Integer.valueOf(reqc.getParameter("productId")[0]);
            }
        }

        //お気に入り登録解除
        dao.removeFavoriteProduct(userId, productId);

        //コミット
        MySqlConnectionManager.getInstance().commit();
        //トランザクション終了
        MySqlConnectionManager.getInstance().closeConnection();

        //Redirect設定
        resc.setRedirect(true);
        resc.setTarget("/spiceEC/all/showProductDetail?productId=" + productId);
        return resc;
    }
}