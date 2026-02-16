package jp.ac.ttc.webapp.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.RecipeDao;
import jp.ac.ttc.webapp.dao.SpiceDao;


public class ListRecipesCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        RequestContext reqc = getRequestContext();
        RecipeDao dao = new RecipeDao();  //dao取得
        Map<String, Object> result = new HashMap<>();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //人気レシピ上位数件取得
        List<RecipeBean> recipes = dao.getPopularRecipes(userId);

        //セッションにspiceIdとspiceNameが登録されてなければ、セッションに登録する
        if(reqc.hasSessionAttribute("spiceIdAndName") == false){
            SpiceDao spiceDao = new SpiceDao();
            List<SpiceBean> spiceIdAndName = spiceDao.getSpicesIdAndNames();
            reqc.setSessionAttribute("spiceIdAndName", spiceIdAndName);
        }

        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる
        result.put("recipes", recipes);
        resc.setResult(result);  //レシピデータをセット
        String url = "listRecipe";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}