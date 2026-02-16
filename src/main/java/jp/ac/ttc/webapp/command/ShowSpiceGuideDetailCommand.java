package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.SpiceDao;


// スパイス詳細取得コマンド
public class ShowSpiceGuideDetailCommand extends AbstractCommand{
    public ResponseContext execute(ResponseContext resc){
        RequestContext reqc = getRequestContext();
        
        // nullチェック未実装
        String[] spiceIdMap = reqc.getParameter("spiceId");
        String stringSpiceId = spiceIdMap[0];
        int spiceId = Integer.parseInt(stringSpiceId);

        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        SpiceDao spiceDao = new SpiceDao();   //dao取得

        SpiceBean spiceBean = spiceDao.getSpiceDetail(spiceId); // １件取得
        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる closeしなくてもいい＞
        resc.setResult(spiceBean);  //取得した結果をセット
        String url = "detailSpice";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}