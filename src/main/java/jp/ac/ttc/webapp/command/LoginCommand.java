package jp.ac.ttc.webapp.command;

import com.google.gson.Gson;

import jp.ac.ttc.webapp.authenticate.AuthenticateService;
import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.CartDao;

public class LoginCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String names[] = reqc.getParameter("name");
        String passes[] = reqc.getParameter("password");
        // JSPで required になっているので取得できるはずですが、念のため配列チェックしても良いです
        String emails[] = reqc.getParameter("email"); 
        
        // 1. ゲストカートデータの取得 (JSON形式)
        String[] guestCartDataParams = reqc.getParameter("guestCartData");
        String guestCartJson = (guestCartDataParams != null && guestCartDataParams.length > 0) 
                               ? guestCartDataParams[0] 
                               : null;
        
        // リダイレクトパラメータの取得
        String[] redirectParams = reqc.getParameter("redirect");
        String customRedirect = (redirectParams != null && redirectParams.length > 0) 
                                ? redirectParams[0] 
                                : null;

        String name = names[0];
        String pass = passes[0];
        String email = (emails != null && emails.length > 0) ? emails[0] : ""; // null回避

        // 2. 認証処理
        AuthenticateService authService = new AuthenticateService();
        
        // 【修正1】引数に email を追加しました (エラー: 指定された型に適用できません の修正)
        UserBean user = authService.authenticateUser(name, pass, email);

        if (user != null) {
            // --- 認証成功 ---
            reqc.setSessionAttribute("user", "OK");
            reqc.setSessionAttribute("userBean", user);
            
            // 【修正2】getName() が無いエラーへの対応
            // UserBeanの中身が不明なため、もし getUserName() も無ければこの行は削除してください
            // reqc.setSessionAttribute("userName", user.getUserName()); 
            
            // とりあえず名前は入力されたものを使うか、UserBeanのメソッドを確認してください
            reqc.setSessionAttribute("userName", name); 

            // 3. ゲストカートの結合処理
            boolean isCartMerged = false;
            if (guestCartJson != null && !guestCartJson.isEmpty() && !guestCartJson.equals("[]")) {
                boolean success = mergeGuestCart(user.getUserId(), guestCartJson);
                if (success) {
                    isCartMerged = true;
                    reqc.setSessionAttribute("cartMerged", "true");
                }
            }

            // --- リダイレクト先の決定 ---
            String targetUrl;

            if (customRedirect != null && !customRedirect.isEmpty()) {
                if ("sendInputPurchase".equals(customRedirect)) {
                    // allフォルダから userフォルダへ移動するため ../user/ を指定
                    targetUrl = "../user/sendInputPurchase"; 
                } else if ("showCart".equals(customRedirect)) {
                    targetUrl = "ShowCartItems";
                } else {
                    targetUrl = customRedirect;
                }
            } else if (isCartMerged) {
                targetUrl = "../user/sendInfo"; 
            } else {
                targetUrl = "productList"; 
            }

            resc.setRedirect(true);
            resc.setTarget(targetUrl);

        } else {
            // --- 認証失敗 ---
            reqc.setSessionAttribute("errorMessage", "IDまたはパスワードが間違っています。");
            resc.setRedirect(true);
            resc.setTarget("login"); 
        }

        return resc;
    }

    // カート結合用ヘルパーメソッド
    private boolean mergeGuestCart(int userId, String jsonString) {
        CartDao cartDao = new CartDao();
        boolean success = false;
        
        try {
            Gson gson = new Gson();
            GuestCartItem[] guestItems = gson.fromJson(jsonString, GuestCartItem[].class);
            
            if (guestItems == null || guestItems.length == 0) return false;

            MySqlConnectionManager.getInstance().beginTransaction();

            CartBean cartBean = cartDao.getCart(userId);
            int cartId = cartBean.getCartId();
            
            if (cartId == 0) {
                cartId = cartDao.createCart(userId);
            }

            for (GuestCartItem item : guestItems) {
                if (item.getId() > 0 && item.getQuantity() > 0) {
                    cartDao.addCartItem(cartId, item.getId(), item.getQuantity());
                }
            }

            cartDao.refreshSubtotal(cartId);
            
            MySqlConnectionManager.getInstance().commit();
            success = true;
            System.out.println("Guest cart merged for user: " + userId);

        } catch (Exception e) {
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        } finally {
            MySqlConnectionManager.getInstance().closeConnection();
        }
        return success;
    }

    private static class GuestCartItem {
        private int id;
        private int quantity;
        public int getId() { return id; }
        public int getQuantity() { return quantity; }
    }
}