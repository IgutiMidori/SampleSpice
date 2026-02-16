package jp.ac.ttc.webapp.bean;

import java.io.Serializable;
import java.util.List;

public class RecipeBean implements Serializable{
    private int recipeId;
    private String mealId;
    private String recipeTitle;
    private String imageUrl;
    private String instructions;
    private String ingredientsJp;
    private String ingredientsEn;
    private String updatedAt;
    private boolean favoriteFlag;
    private int favoriteUserCount;
    private List<Integer> spiceIds;
    private List<SpiceBean> spices;
    private boolean activeFlag;

    public RecipeBean() {}

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
    public int getRecipeId() {
        return recipeId;
    }
    public void setMealId(String mealId) {
        this.mealId = mealId;
    }
    public String getMealId() {
        return mealId;
    }
    public String getRecipeTitle() {
        return recipeTitle;
    }
    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getIngredientsJp() {
        return ingredientsJp;
    }
    public void setIngredientsJp(String ingredientsJp) {
        this.ingredientsJp = ingredientsJp;
    }
    public String getIngredientsEn() {
        return ingredientsEn;
    }
    public void setIngredientsEn(String ingredientsEn) {
        this.ingredientsEn = ingredientsEn;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public boolean getFavoriteFlag() {
        return favoriteFlag;
    }
    public void setFavoriteFlag(boolean favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }
    public int getFavoriteUserCount() {
        return favoriteUserCount;
    }
    public void setFavoriteUserCount(int favoriteUserCount) {
        this.favoriteUserCount = favoriteUserCount;
    }
    public List<Integer> getSpiceIds() {
        return spiceIds;
    }
    public void setSpiceIds(List<Integer> spiceIds) {
        this.spiceIds = spiceIds;
    }
    public List<SpiceBean> getSpices(){
        return spices;
    }
    public void setSpices(List<SpiceBean> spices){
        this.spices = spices;
    }
    public void addSpice(SpiceBean spice){
        spices.add(spice);
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
    public boolean getActiveFlag() {
        return activeFlag;
    } 
}
