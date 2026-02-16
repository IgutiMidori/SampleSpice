package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.AddressBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AddressDao;

// 遷移するだけ
public class SendPurchaseInputCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        List<AddressBean> addressList = new ArrayList<AddressBean>();
        try{
            MySqlConnectionManager.getInstance().beginTransaction();

            int userId = 0;
            if(reqc.hasSessionAttribute("user") == true){
                userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
            }

            AddressDao addressDao = new AddressDao();
            // userIdでアドレスを取得
            addressList= addressDao.getAddress(userId);

        } catch(Exception e) {
            e.printStackTrace();
            try {
                MySqlConnectionManager.getInstance().rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                MySqlConnectionManager.getInstance().closeConnection();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String , Object> resultData = new HashMap<>();
        resultData.put("addressList", addressList);  //取得したアドレスリストをセット
        resc.setResult(resultData);  //取得した結果をセット
        String url = "inputPurchase"; 
        resc.setTarget(url);

        return resc;
    }
}