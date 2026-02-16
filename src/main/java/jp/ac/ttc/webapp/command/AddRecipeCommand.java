package jp.ac.ttc.webapp.command;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jp.ac.ttc.webapp.adminservice.MealItem;
import jp.ac.ttc.webapp.adminservice.Meals;
import jp.ac.ttc.webapp.adminservice.RecipeLoader;
import jp.ac.ttc.webapp.adminservice.Translator;
import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminRecipeDao;

public class AddRecipeCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String[] spiceNameAndIds = reqc.getParameter("spiceNameAndId");
        String spiceNameAndId = spiceNameAndIds[0];

        //必要なパラメータをカンマ区切りで取得
        String spiceIdStr = spiceNameAndId.split(",")[0];
        String spiceNameJp = spiceNameAndId.split(",")[1];
        String spiceNameEn = spiceNameAndId.split(",")[2];

        //確認用：後で消す
        System.out.println("spiceNameJp : " + spiceNameJp);
        System.out.println("spiceNameEn : " + spiceNameEn);
        System.out.println("spiceId : " + spiceIdStr);

        //レシピAPI検索用クラス
        RecipeLoader recipeLoader = new RecipeLoader();

        //スパイス名で検索したレシピ複数のAPI側id
        List<String> mealIds = recipeLoader.serchRecipeIdsBySpiceName(spiceNameEn);

        //結果として返すレシピ
        List<RecipeBean> recipeList = new ArrayList<>();

        //データベース操作開始
        MySqlConnectionManager.getInstance().beginTransaction();
        //今回使う管理者用DAO
        AdminRecipeDao recipeDao = new AdminRecipeDao();

        //IDを一件ずつ取得
        for(String mealId : mealIds) {

            /*すでにこのAPIのIDで登録されたレシピがあればそのrecipe_idを返す
            なければ-1を返す*/
            int recipeId = recipeDao.hasRecipeAsMealId(mealId);
            

            if(recipeId != -1) {
                //すでにレシピが登録されていた場合は同一スパイスで登録されてるか確認
                if(recipeDao.hasSpicesAsRecipeId(recipeId, spiceIdStr)) {
                    //登録されていれば何もしない
                    System.out.println("このレシピは同一のスパイスカテゴリーで登録されている");
                } else {
                    //登録されていなければ新しくspice_idと関連させる
                    recipeDao.addSpiceRecipe(spiceIdStr, recipeId);
                    
                }
            } else {
                //APIにmeal_idで検索してレシピ詳細を取得
                Meals meals = recipeLoader.serchRecipeById(mealId);

                //複数権取得のリストに入ってしまうのでいったん分解
                for(MealItem meal : meals.getMeals()) {

                    //新規登録レシピ用のBean
                    RecipeBean recipe = new RecipeBean();

                    //翻訳API使用クラス
                    Translator translate = new Translator();

                    //meal_idをBeanにセット
                    recipe.setMealId(mealId);

                    //タイトルを翻訳してBeanにセット
                    String recipeTitle = translate.translateString(meal.getStrMeal());
                    recipe.setRecipeTitle(recipeTitle);

                    //画像URLをセット
                    recipe.setImageUrl(meal.getStrMealThumb());

                    //料理手順を翻訳してセット
                    String instructionsJp = translate.translateString(meal.getStrInstructions());
                    recipe.setInstructions(instructionsJp);

                    //JsonArryはJSON配列を作るためのクラス。日本語用と英語用で二つ用意
                    JsonArray jsonArrayEn = new JsonArray();
                    JsonArray jsonArrayJp = new JsonArray();

                    //それぞれのListを作成
                    List<String> ingredients = meal.getIngredients();
                    List<String> measures = meal.getMeasures();

                    //分量または材料のどちらかのみ値が存在する可能性があるのでどちらかの最小値を取得
                    int minSize = Math.min(ingredients.size(), measures.size());
                    //その値でListから一個ずつ取得
                    for(int i = 0; i < minSize; i++ ) {
                        //それぞれ一時的に変数で保持
                        String ing = ingredients.get(i);
                        String mea = measures.get(i);

                        //途中でどちらかに空欄が存在する場合は処理を飛ばす
                        if(ing.isBlank() || mea.isBlank()) {
                            continue;
                        }

                        //JsonObjectは任意のJavaオブジェクトをJsonオブジェクトに変換するためのクラス
                        //日本語用に作成
                        JsonObject jsonObjectJp = new JsonObject();
                        //材料・分量を一個ずつ翻訳
                        String ingJp = translate.translateString(ing);
                        String meaJp = translate.translateString(mea);
                        //Jsonオブジェクトに属性として登録
                        jsonObjectJp.addProperty("ingredient", ingJp);
                        jsonObjectJp.addProperty("measure", meaJp);

                        //配列に材料と分量がペアのオブジェクト1件として登録
                        jsonArrayJp.add(jsonObjectJp);

                        //英語用のJsonオブジェクト変換元
                        JsonObject jsonObjectEn = new JsonObject();
                        //同様にオブジェクトに属性として登録
                        jsonObjectEn.addProperty("ingredient", ing);
                        jsonObjectEn.addProperty("measure", mea);

                        //同様に英語用配列に登録
                        jsonArrayEn.add(jsonObjectEn);
                    }
                    //それぞれString型でJson形式に変換。Beanにセット
                    String ingredientsJsonEn = new Gson().toJson(jsonArrayEn);
                    recipe.setIngredientsEn(ingredientsJsonEn);

                    String ingredientsJsonJp = new Gson().toJson(jsonArrayJp);
                    recipe.setIngredientsJp(ingredientsJsonJp);

                    //recipeIdを新規登録した際に生成されたもので上書き
                    recipeId = recipeDao.addRecipe(recipe);
                    //Beanにセット
                    recipe.setRecipeId(recipeId);
                    //recipe_spice表にも更新
                    recipeDao.addSpiceRecipe(spiceIdStr, recipeId);
                    //新規登録したレシピデータをリストに登録
                    recipeList.add(recipe);
                }
            }
        }
        //コミット
        MySqlConnectionManager.getInstance().commit();
        MySqlConnectionManager.getInstance().closeConnection();

        if(recipeList.size() == 0) {
            reqc.setSessionAttribute("noAddedContents", "新規レシピが登録されませんでした。");
        }

        //結果セットを転送先の属性に登録
        reqc.setSessionAttribute("recipeList", recipeList);
        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/selectSpices");
        return resc;
    }
}
