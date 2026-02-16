package jp.ac.ttc.webapp.command;

import java.util.List;

import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.SpiceDao;

public class SearchSpiceGuidesCommand extends AbstractCommand{
    public ResponseContext execute(ResponseContext resc){

        RequestContext reqc = getRequestContext();
        String[] spiceNameMap = reqc.getParameter("searchSpiceName");
        
        // 一応nullチェック
        String spiceName = ""; 
        if (spiceNameMap != null && spiceNameMap.length > 0){
            spiceName = spiceNameMap[0];
        }

        SpiceDao spiceDao = new SpiceDao();   //dao取得
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        
        List<SpiceBean> spiceList = spiceDao.getSpicesBySpiceName(spiceName);  //全件取得
        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる closeしなくてもいい＞
        resc.setResult(spiceList);  //取得した結果をセット
        String url = "searchSpiceResult";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}