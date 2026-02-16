package jp.ac.ttc.webapp.adminservice;

import java.util.List;

/**
 * 料理検索APIのJsonパース用クラス。複数の料理を配列で持つ
 * @author suzuki-takumi23
 * @since 2026-01-16
 */
public class Meals {
    List<MealItem> meals;

    public List<MealItem> getMeals() {
        return meals;
    }
}
