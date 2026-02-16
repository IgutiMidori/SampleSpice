package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.OrderRecordBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.OrderDao;


public class ShowOrderHistoryDetailCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        OrderDao dao = new OrderDao();  //dao取得
        RequestContext reqc = getRequestContext(); //RequestContextを取得

        Integer orderRecordId;
        //productIDのnullチェック
        if(reqc.hasParameter("orderRecordId") == true){
            if(reqc.getParameter("orderRecordId")[0].isBlank()){
                orderRecordId = null;
            }else{
                orderRecordId = Integer.valueOf(reqc.getParameter("orderRecordId")[0]);
            }
        }else{
            orderRecordId = null;
        }

        //注文内容を取得
        OrderRecordBean order = dao.getOrderItems(orderRecordId);

        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる
        resc.setResult(order);  //注文内容をセット
        String url = "detailOrder";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}