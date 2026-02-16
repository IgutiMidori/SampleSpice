package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.SpiceDao;

public class ListSpiceGuidesCommand extends AbstractCommand{

    public ResponseContext execute(ResponseContext resc){

        RequestContext reqc = getRequestContext();
        String[] pages =  reqc.getParameter("page");
        PageBean bean;

        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        SpiceDao spiceDao = new SpiceDao();   //dao取得

        //ページング情報を設定
        int spiceCount = spiceDao.getSpicesCount();
        if(pages == null) {
            bean = new PageBean(1, spiceCount, PageBean.SPICE_PAGE_SIZE);
        } else{
            String page = pages[0];
            bean = new PageBean(Integer.parseInt(page), spiceCount, PageBean.SPICE_PAGE_SIZE);
        }

        List<SpiceBean> spiceList = spiceDao.getSpices(PageBean.SPICE_PAGE_SIZE, bean.getOffset());  //全件取得
        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる closeしなくてもいい＞
        Map<String, Object> result = new HashMap<>();
        result.put("spiceList", spiceList);
        result.put("pageBean", bean);

        resc.setResult(result);  //取得した結果をセット
        String url = "listSpice";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}