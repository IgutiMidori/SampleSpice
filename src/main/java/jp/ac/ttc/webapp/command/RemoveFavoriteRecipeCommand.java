package jp.ac.ttc.webapp.command;


import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.FavoriteRecipeDao;


public class RemoveFavoriteRecipeCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        FavoriteRecipeDao dao = new FavoriteRecipeDao();   //dao取得
        RequestContext reqc = getRequestContext();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        //RecipeIdを取得
        int recipeId = -1;
        if(reqc.hasParameter("recipeId") == true){
            if(reqc.getParameter("recipeId")[0].isBlank() == false){
                recipeId = Integer.valueOf(reqc.getParameter("recipeId")[0]);
            }
        }

        //お気に入り登録解除
        dao.removeFavoriteRecipe(userId, recipeId);

        //コミット
        MySqlConnectionManager.getInstance().commit();
        //トランザクション終了
        MySqlConnectionManager.getInstance().closeConnection();

        //Redirect設定
        resc.setRedirect(true);
        resc.setTarget("/spiceEC/all/showRecipeDetail?recipeId=" + recipeId);
        return resc;
    }
}