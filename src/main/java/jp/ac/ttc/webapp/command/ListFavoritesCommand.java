package jp.ac.ttc.webapp.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.FavoriteProductDao;
import jp.ac.ttc.webapp.dao.FavoriteRecipeDao;


public class ListFavoritesCommand extends AbstractCommand{
    @Override
    public ResponseContext execute(ResponseContext resc){
        MySqlConnectionManager.getInstance().beginTransaction(); //トランザクション開始
        FavoriteProductDao pdao = new FavoriteProductDao();   //dao取得
        FavoriteRecipeDao rdao = new FavoriteRecipeDao();
        RequestContext reqc = getRequestContext();

        //userIdを取得
        int userId = -1;
        if(reqc.hasSessionAttribute("user") == true){
            userId = ((UserBean)reqc.getSessionAttribute("userBean")).getUserId();
        }

        int currentPage = 1;
        if(reqc.hasParameter("page") == true){
            currentPage = Integer.valueOf(reqc.getParameter("page")[0]);
        }
        PageBean pPage = new PageBean(currentPage, pdao.getFavoriteProductCount(userId), 1);
        PageBean rPage = new PageBean(currentPage, rdao.getFavoriteRecipeCount(userId), 1);
        

        //お気に入り商品・レシピを全件取得
        List<ProductBean> products = pdao.getFavoriteProducts(userId, 1, pPage.getOffset());  //お気に入り商品
        List<RecipeBean> recipes = rdao.getFavoriteRecipes(userId, 1, rPage.getOffset());  //お気に入りレシピ
        Map<String, Object> favorites = new HashMap<>();  //商品とレシピを格納するMap
        favorites.put("products", products);
        favorites.put("recipes", recipes);
        favorites.put("pPage", pPage);
        favorites.put("rPage", rPage);

        MySqlConnectionManager.getInstance().closeConnection();  //コネクションを閉じる
        resc.setResult(favorites);  //商品データをセット
        String url = "listFavorites";  //転送先URL
        resc.setTarget(url);  //転送先URLをセット
        return resc;
    }
}