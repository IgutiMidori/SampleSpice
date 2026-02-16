package jp.ac.ttc.webapp.bean;

import java.io.Serializable;

public class FavouriteRecipeBean implements Serializable{
    private int favouriteRecipeId;
    private RecipeBean recipe;
    private String addedAt;

    public FavouriteRecipeBean() {}

    public int getFavouriteRecipeId() {
        return favouriteRecipeId;
    }
    public void setFavouriteRecipeId(int favouriteRecipeId) {
        this.favouriteRecipeId = favouriteRecipeId;
    }
    public RecipeBean getRecipe() {
        return recipe;
    }
    public void setRecipe(RecipeBean recipe) {
        this.recipe = recipe;
    }
    public String getAddedAt() {
        return addedAt;
    }
    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}