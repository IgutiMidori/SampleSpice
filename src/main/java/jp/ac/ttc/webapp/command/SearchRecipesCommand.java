package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.RecipeDao;
import jp.ac.ttc.webapp.dao.SpiceDao;


public class SearchRecipesCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        RecipeDao dao = new RecipeDao();   //dao取得
        RequestContext reqc = getRequestContext();
        List<RecipeBean> recipes = null;
        Map<String, Object> result = new HashMap<>();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //検索パラメータを格納する変数
        String recipeTitle;
        String foodName;
        Integer spiceId;


        //レシピ名のnullチェック
        if(reqc.hasParameter("recipeTitle") == true){
            recipeTitle = reqc.getParameter("recipeTitle")[0];
            if(reqc.getParameter("recipeTitle")[0].isBlank()){
                recipeTitle = null;
            }
        }else{
            recipeTitle = null;
        }
        //食材名のnullチェック
        if(reqc.hasParameter("foodName") == true){
            foodName = reqc.getParameter("foodName")[0];
            if(reqc.getParameter("foodName")[0].isBlank()){
                foodName = null;
            }
        }else{
            foodName = null;
        }
        //スパイスIDのnullチェック
        if(reqc.hasParameter("spiceId") == true){
            if(reqc.getParameter("spiceId")[0].isBlank()){
                spiceId = null;
            }else{
                spiceId = Integer.valueOf(reqc.getParameter("spiceId")[0]);
            }
        }else{
            spiceId = null;
        }

        if(recipeTitle == null && foodName == null && spiceId == null){
            //検索条件がすべてnullだった場合
            //通常通り、人気順上位20件を取得する
            MySqlConnectionManager.getInstance().closeConnection();
            reqc.setSessionAttribute("searchError", "検索条件がありません");
            resc.setRedirect(true);
            resc.setTarget("/spiceEC/all/recipeList");
            return resc;
        }

        StringBuilder builder = new StringBuilder();
        if(recipeTitle != null){
            builder.append("recipeTitle=").append(recipeTitle).append("&");
        }
        if(foodName != null){
            builder.append("foodName=").append(foodName).append("&");
        }
        if(spiceId != null){
            builder.append("spiceId=").append(spiceId).append("&");
        }
        String searchParams = builder.toString();
        if(searchParams.endsWith("&")) {
            searchParams = searchParams.substring(0, searchParams.length() - 1);
        }

        //現在のページ数を取得
        int currentPage = 1;
        if(reqc.hasParameter("page") == true){
            currentPage = Integer.valueOf(reqc.getParameter("page")[0]);
        }

        //ページ情報を設定
        int totalItems = dao.getRecipeCount(recipeTitle, foodName, spiceId);
        System.out.println("totalItems: " + totalItems);
        PageBean pageBean = new PageBean(currentPage, totalItems, PageBean.RECIPE_PAGE_SIZE);


        //レシピデータを格納するList
        recipes = dao.getSearchRecipes(userId, recipeTitle, foodName, spiceId, PageBean.RECIPE_PAGE_SIZE,pageBean.getOffset());



        //セッションにspiceIdとspiceNameが登録されてなければ、セッションに登録する
        if(reqc.hasSessionAttribute("spiceIdAndName") == false){
            SpiceDao spiceDao = new SpiceDao();
            List<SpiceBean> spiceIdAndName = spiceDao.getSpicesIdAndNames();
            reqc.setSessionAttribute("spiceIdAndName", spiceIdAndName);
        }

        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる

        result.put("recipes", recipes);
        result.put("page", pageBean);
        result.put("searchParams", searchParams);
        resc.setResult(result);  //商品データをセット
        String url = "listRecipe";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}