package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.CartDao;

public class ShowCartItemsCommand extends AbstractCommand {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();

        // 1. GET USER FROM SESSION
        // We check if the "userBean" exists in the session.
        UserBean user = (UserBean) reqc.getSessionAttribute("userBean");

        // define target JSP
        String url = "cartItems"; 
        resc.setTarget(url);

        // ==========================================
        // SCENARIO A: GUEST USER (Not Logged In)
        // ==========================================
        if (user == null) {
            // If user is null, we DO NOT touch the database.
            // We pass an empty list (or null) to the JSP.
            // The JSP will detect the user is not logged in and render the LocalStorage view.
            resc.setResult(new ArrayList<CartItemBean>());
            return resc;
        }

        // ==========================================
        // SCENARIO B: LOGGED-IN USER
        // ==========================================
        CartDao cartDao = new CartDao();

        try {
            // Start Transaction only for logged-in users
            MySqlConnectionManager.getInstance().beginTransaction();

            int userId = user.getUserId();

            // Get Cart for this user
            CartBean cartBean = cartDao.getCart(userId);

            int cartId;

            // If cart doesn't exist (or ID is 0), create a new one
            if (cartBean == null || cartBean.getCartId() == 0) {
                cartId = cartDao.createCart(userId);
            } else {
                cartId = cartBean.getCartId();
            }

            // Get items from DB
            List<CartItemBean> cartItems = cartDao.getCartItems(cartId);

            // Commit transaction
            MySqlConnectionManager.getInstance().commit();

            // Set result for the JSP
            resc.setResult(cartItems);

        } catch(Exception e) {
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        } finally {
            MySqlConnectionManager.getInstance().closeConnection();
        }

        return resc;
    }
}