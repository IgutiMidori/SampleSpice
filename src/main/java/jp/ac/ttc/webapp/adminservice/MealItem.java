package jp.ac.ttc.webapp.adminservice;

import java.util.ArrayList;
import java.util.List;

/**
 * 料理検索APIのJsonパース用クラス。一つの料理に必要なデータを格納する
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class MealItem {
    String idMeal;
    String strMeal;
    String strMealAlternate;
    String strCategory;
    String strArea;
    String strInstructions;
    String strMealThumb;
    String strTags;
    String strYoutube;

    // Ingredients
    String strIngredient1;
    String strIngredient2;
    String strIngredient3;
    String strIngredient4;
    String strIngredient5;
    String strIngredient6;
    String strIngredient7;
    String strIngredient8;
    String strIngredient9;
    String strIngredient10;
    String strIngredient11;
    String strIngredient12;
    String strIngredient13;
    String strIngredient14;
    String strIngredient15;
    String strIngredient16;
    String strIngredient17;
    String strIngredient18;
    String strIngredient19;
    String strIngredient20;

    // Measures
    String strMeasure1;
    String strMeasure2;
    String strMeasure3;
    String strMeasure4;
    String strMeasure5;
    String strMeasure6;
    String strMeasure7;
    String strMeasure8;
    String strMeasure9;
    String strMeasure10;
    String strMeasure11;
    String strMeasure12;
    String strMeasure13;
    String strMeasure14;
    String strMeasure15;
    String strMeasure16;
    String strMeasure17;
    String strMeasure18;
    String strMeasure19;
    String strMeasure20;

    String strSource;
    String strImageSource;
    String strCreativeCommonsConfirmed;
    String dateModified;

    // --------- 便利機能：Ingredient一覧を取得 ---------

    public String getStrInstructions() {
        return strInstructions;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public List<String> getIngredients() {
        List<String> list = new ArrayList<>();
        String[] arr = {
                strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
                strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
                strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
                strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
        };
        for (String s : arr) {
            if (s != null && !s.isBlank()) list.add(s);
        }
        return list;
    }

    public List<String> getMeasures() {
        List<String> list = new ArrayList<>();
        String[] arr = {
                strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
                strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
                strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
                strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
        };
        for (String s : arr) {
            if (s != null && !s.isBlank()) list.add(s);
        }
        return list;
    }
}
