package jp.ac.ttc.webapp.command;


import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.RecipeDao;


public class ShowRecipeDetailCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        RequestContext reqc = getRequestContext();
        RecipeDao dao = new RecipeDao();  //dao取得

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //recipeIdを取得
        int recipeId = -1;
        if(reqc.hasParameter("recipeId") == true){
            if(reqc.getParameter("recipeId")[0].isBlank() == false){
                recipeId = Integer.valueOf(reqc.getParameter("recipeId")[0]);
            }
        }

        //人気レシピ上位数件取得
        RecipeBean recipe = dao.getRecipeDetail(userId, recipeId);

        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる
        resc.setResult(recipe);  //レシピデータをセット
        String url = "detailRecipe";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}