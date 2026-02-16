package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.CartDao;


public class AddCartItemCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        CartDao cartDao= new CartDao();

        int productId = Integer.parseInt(reqc.getParameter("productId")[0]);
        int product_quantity = Integer.parseInt(reqc.getParameter("product_quantity")[0]);
        // カートに登録した時に表示する画像を受け取る 
        String imageUrl = reqc.getParameter("imageUrl")[0];
        
        // userIdをセッションから取得
        int userId = 0;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }
        System.out.println(userId);

        try{
            // トランザクション開始
            MySqlConnectionManager.getInstance().beginTransaction();

            // もしカートがなかったらcartBeanのフィールドに0がはいる
            CartBean cartBean = (CartBean)cartDao.getCart(userId);

            int cartId;
            // カートがなかったら作成
            if(cartBean.getCartId() == 0){
                cartId = cartDao.createCart(userId);
            } else{
                // カートがあったらそのcartIdを取得
                cartId = cartBean.getCartId();
            }
            System.out.println(cartId);

            // カートにアイテム追加
            cartDao.addCartItem(cartId,productId, product_quantity);

            // データベースの小計を更新
            cartDao.refreshSubtotal(cartId);

            MySqlConnectionManager.getInstance().commit();
            MySqlConnectionManager.getInstance().closeConnection();

            // resc.setResult(spiceList);  //取得した結果をセット
            resc.setResult(imageUrl);
            String url = "addCartResult";  //転送先URL
            resc.setTarget(url);    //転送先URLをセット
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