package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.CartDao;

public class ChangeProductQuantityCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        CartDao cartDao= new CartDao();

        // アイテムidと数量を取得
        int cartItemId = Integer.parseInt(reqc.getParameter("cartItemId")[0]);
        int quantity = Integer.parseInt(reqc.getParameter("quantity")[0]);

        // データベースの小計を更新するためにuserIdを取得
        int userId = 0;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        try{
            // トランザクション開始
            MySqlConnectionManager.getInstance().beginTransaction();

            // カートアイテム削除
            cartDao.updateProductQuantity(cartItemId, quantity);

            // データベースの小計を更新
            // userIdをセッションから取得
            CartBean cartBean = (CartBean)cartDao.getCart(userId);
            cartDao.refreshSubtotal(cartBean.getCartId());

            MySqlConnectionManager.getInstance().commit();
            MySqlConnectionManager.getInstance().closeConnection();

            // resc.setResult(spiceList);  //取得した結果をセット
            //転送先URL
            String url = "/spiceEC/all/ShowCartItems";  
            resc.setRedirect(true);
            resc.setTarget(url);  
            //転送先URLをセット,カート一覧へ飛ぶ
        } catch(Exception e){
            e.printStackTrace();
            
            // 異常終了したらrollbackする
            MySqlConnectionManager.getInstance().rollback();
            // 異常終了した後の処理はまだない
        } finally{
            MySqlConnectionManager.getInstance().closeConnection();
        }
        return resc;
    }
}