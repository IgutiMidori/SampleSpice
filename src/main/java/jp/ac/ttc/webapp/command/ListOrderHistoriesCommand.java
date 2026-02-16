package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.OrderRecordBean;
import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.OrderDao;


public class ListOrderHistoriesCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        OrderDao dao = new OrderDao();  //dao取得
        RequestContext reqc = getRequestContext(); //RequestContextを取得
        Map<String, Object> result = new HashMap<>(); //結果転送用Map

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //現在のページ数を取得
        int currentPage = 1;
        if(reqc.hasParameter("page")) {
            currentPage = Integer.parseInt(reqc.getParameter("page")[0]);
        }

        //PageBeanをインスタンス化。現在のページ数、全体の件数、1ページあたりの表示件数でページングに必要な情報を算出する。
        PageBean page = new PageBean(currentPage, dao.getOrderRecordCount(userId), PageBean.ORDER_RECODE_PAGE_SIZE);

        List<OrderRecordBean> orders = dao.getOrderRecords(userId, PageBean.ORDER_RECODE_PAGE_SIZE, page.getOffset());  //注文履歴を取得
        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる

        result.put("orderList", orders);//注文履歴をセット
        result.put("pageBean", page);//ページング情報をセット
        resc.setResult(result);  //結果Mapをセット
        String url = "listOrder";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}