package jp.ac.ttc.webapp.command;

import java.util.List;

import jp.ac.ttc.webapp.bean.AddressBean;
import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AddressDao;
import jp.ac.ttc.webapp.dao.CartDao;
import jp.ac.ttc.webapp.dao.OrderDao;
import jp.ac.ttc.webapp.dao.ProductDao;
import jp.ac.ttc.webapp.util.MailUtility;

public class ExecutePurchaseCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();

        // 1. フォームからの入力取得
        String receiverName = reqc.getParameter("receiverName")[0];
        String receiverNameReading = reqc.getParameter("receiverNameReading")[0];
        String phoneNumber = reqc.getParameter("phoneNumber")[0];
        String postalCode = reqc.getParameter("postalCode")[0];
        String deliveryAddress = reqc.getParameter("deliveryAddress")[0];

        AddressBean addressBean = new AddressBean();
        addressBean.setReceiverName(receiverName);
        addressBean.setReceiverNameReading(receiverNameReading);
        addressBean.setPhoneNumber(phoneNumber);
        addressBean.setPostalCode(postalCode);
        addressBean.setDeliveryAddress(deliveryAddress);

        UserBean userInfo = null;
        int userId = 0;
        if (reqc.hasSessionAttribute("userBean")) {
            userInfo = ((UserBean) reqc.getSessionAttribute("userBean"));
            userId = userInfo.getUserId();
        }

        AddressDao addressDao = new AddressDao();
        CartDao cartDao = new CartDao();
        OrderDao orderDao = new OrderDao();
        ProductDao productDao = new ProductDao();

        try {
            MySqlConnectionManager.getInstance().beginTransaction();

            // カート情報・アイテム情報取得
            CartBean cartBean = cartDao.getCart(userId);
            List<CartItemBean> cartItems = cartDao.getCartItems(cartBean.getCartId());
            
            // --- 追加：合計金額の計算 ---
            int totalPrice = 0;
            for (CartItemBean item : cartItems) {
                totalPrice += item.getProduct().getPrice() * item.getProductQuantity();
            }

            // DB更新処理
            int addressId = addressDao.addAddress(addressBean, userId);
            System.out.println("住所情報を追加しました。ID: " + addressId);
            int orderRecordId = orderDao.addOrderRecord(userId, cartBean, addressId);
            System.out.println("注文レコードを追加しました。ID: " + orderRecordId);
            orderDao.addOrderItems(orderRecordId, cartItems);
            System.out.println("注文アイテムを追加");
            productDao.reduceProductQuantityByCartItemList(cartItems);
            System.out.println("商品在庫を更新しました。");
            cartDao.deleteCart(userId);
            System.out.println("カート情報を削除しました。");

            // コミットしてDB確定
            MySqlConnectionManager.getInstance().commit();
            System.out.println("注文処理完了 ID: " + orderRecordId);

            // --- 2. 専用メソッドでメール送信 ---
            MailUtility mailer = new MailUtility();
            
            // 宛先をユーザーのメアド、もしくはテスト用の yama... に設定
            // String toEmail = userInfo.getEmail(); 
            String userName = userInfo.getUserName();

            boolean isSent = mailer.sendOrderConfirmationMail(
                "yamap070@gmail.com", 
                userName, 
                addressBean, 
                orderRecordId, 
                cartItems, 
                totalPrice
            );

            if (isSent) {
                System.out.println("詳細な購入通知メールを送信しました。");
            }

            resc.setTarget("purchaseResult");

        } catch (Exception e) {
            e.printStackTrace();
            // ロールバック処理が必要な場合はここに追加
            resc.setTarget("error");
        } finally {
            MySqlConnectionManager.getInstance().closeConnection();
        }
        
        return resc;
    }
}