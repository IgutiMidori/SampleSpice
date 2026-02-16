package jp.ac.ttc.webapp.adminservice;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * レシピ検索APIを使用するクラス
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class RecipeLoader{

        /**
         * 単一のレシピ詳細を検索するメソッド
         * @param id APIのレシピデータ、mealId
         * @return {@code Mealsクラス}
         */
        public Meals serchRecipeById(String id) {
                Meals meals = null;
                try {
                        HttpClient client = HttpClient.newHttpClient();

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + id))
                                .GET()
                                .build();

                        HttpResponse<String> response =
                                client.send(request, HttpResponse.BodyHandlers.ofString());

                        String json = response.body();
                        Gson gson = new Gson();
                        meals = gson.fromJson(json, Meals.class);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return meals;
        }

        public List<String> serchRecipeIdsBySpiceName(String spiceNameEn) {
                List<String> mealIds = new ArrayList<>();
                try{
                        String encoded = URLEncoder.encode(spiceNameEn, StandardCharsets.UTF_8);
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://www.themealdb.com/api/json/v1/1/filter.php?i=" + encoded))
                                .GET()
                                .build();

                        HttpResponse<String> response =
                                client.send(request, HttpResponse.BodyHandlers.ofString());

                                String json = response.body();
                                Gson gson = new Gson();
                                Meals meals = gson.fromJson(json, Meals.class);
                        for(MealItem mealItem : meals.getMeals()) {
                                mealIds.add(mealItem.getIdMeal());
                        }
                } catch(Exception e) {
                        e.printStackTrace();
                }
                return mealIds;
        }
}
