package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.AddressBean;
import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.CartDao;

// 入力内容が正しいか確認し、確認画面へ遷移するコマンドクラス
public class ConfirmationPurchaseCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();

        // 1. フォームからの入力取得
        String receiverName = reqc.getParameter("receiverName")[0];
        String receiverNameReading = reqc.getParameter("receiverNameReading")[0];
        String phoneNumber = reqc.getParameter("phoneNumber")[0];
        String postalCode = reqc.getParameter("postalCode")[0];
        String deliveryAddress = reqc.getParameter("deliveryAddress")[0];
        System.out.println("フォームのやつ受け取れてる");

        // 2. サーバー側バリデーション
        boolean isValid = true; 
        // 名前チェック
        if (receiverName == null || receiverName.length() > 50) {
            receiverName = ""; // ダメなら空にする
            isValid = false;
        }
        // フリガナチェック
        if (receiverNameReading == null || receiverNameReading.length() > 100) {
            receiverNameReading = ""; 
            isValid = false;
        }
        // 電話番号チェック
        if (phoneNumber == null || !phoneNumber.matches("^0\\d{9,10}$")) {
            phoneNumber = "";
            isValid = false;
        }  
        // 郵便番号チェック
        if (postalCode == null || !postalCode.matches("^\\d{3}-\\d{4}$")) {
            postalCode = "";
            isValid = false;
        } 
        // 住所チェック
        if (deliveryAddress == null || deliveryAddress.length() > 100) {
            deliveryAddress = "";
            isValid = false;
        }
        // 一つでもNGがあれば戻す
        // 一つでもNGがあれば戻す
        if (!isValid) {
            System.out.println("バリデーションエラーが発生しました。入力内容を確認してください。");
            Map<String, Object> errorData = new HashMap<>();
            // OKなものは元の値、ダメなものは空文字が入った状態でセットされる
            errorData.put("prevName", receiverName);
            errorData.put("prevReading", receiverNameReading);
            errorData.put("prevPhone", phoneNumber);
            errorData.put("prevPostal", postalCode);
            errorData.put("prevAddress", deliveryAddress);
            
            errorData.put("validationError", true);

            resc.setResult(errorData);
            resc.setTarget("inputPurchase"); // 入力画面のJSP名
            return resc;
        }

        // 3. Beanに格納
        AddressBean addressBean = new AddressBean();
        addressBean.setReceiverName(receiverName);
        addressBean.setReceiverNameReading(receiverNameReading);
        addressBean.setPhoneNumber(phoneNumber);
        addressBean.setPostalCode(postalCode);
        addressBean.setDeliveryAddress(deliveryAddress);

        // セッションからユーザーID取得
        int userId = 0;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        Map<String, Object> data = new HashMap<>();

        // カート情報取得
        try {
            CartDao cartDao = new CartDao();
            
            CartBean cartBean = cartDao.getCart(userId);
            int cartId = cartBean.getCartId();

            List<CartItemBean> cartItems = cartDao.getCartItems(cartId);

            // 住所情報とカート情報をセット
            data.put("address", addressBean);
            data.put("cartItems", cartItems);

            
        } catch (Exception e) {
            e.printStackTrace();
            resc.setTarget("error");
        } finally {
            MySqlConnectionManager.getInstance().closeConnection();
        }
        resc.setResult(data);
        // resc.setRedirect(true);
        resc.setTarget("purchaseConfirmation"); 
        return resc;
    }
}