package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class AdminRecipeDao {
    public int hasRecipeAsMealId(String mealId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        int recipeId = -1;

        try{
            String sql = "SELECT recipe_id, meal_id  FROM recipe WHERE meal_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, mealId);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()) {
                recipeId = rs.getInt("recipe_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipeId;
    }

    public boolean hasSpicesAsRecipeId(int recipeId, String spiceIdStr) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        boolean hasSpice = false;

        try{
            String sql = "SELECT recipe_id, spice_id  FROM recipe_spice WHERE recipe_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                String spiceId = rs.getString("spice_id");
                if(spiceIdStr.equals(spiceId)) {
                    hasSpice = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasSpice;
    }

    public int addRecipe(RecipeBean recipe) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO recipe (meal_id, recipe_title, image_url, instructions, ingredients_jp, ingredients_en, source_language) VALUES(?, ?, ?, ?, ?, ?, 'ja')";
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, recipe.getMealId());
            pstmt.setString(2, recipe.getRecipeTitle());
            pstmt.setString(3, recipe.getImageUrl());
            pstmt.setString(4, recipe.getInstructions());
            pstmt.setString(5, recipe.getIngredientsJp());
            pstmt.setString(6, recipe.getIngredientsEn());
            

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated product ID
            }
            return -1; // Indicate failure to get generated ID

        } catch (SQLException e) {
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
            return -1;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addSpiceRecipe(String spiceId, int recipeId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO recipe_spice (recipe_id, spice_id) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, recipeId);
            pstmt.setString(2, spiceId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<RecipeBean> getAllRecipe(int limit, int offset) {
        List<RecipeBean> recipeList = new ArrayList<>();
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;


        try{
            String sql = "SELECT * FROM recipe ORDER BY recipe_id DESC LIMIT ? OFFSET ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                RecipeBean bean = new RecipeBean();
                bean.setRecipeId(rs.getInt("recipe_id"));
                bean.setMealId(rs.getString("meal_id"));
                bean.setRecipeTitle(rs.getString("recipe_title"));
                bean.setImageUrl(rs.getString("image_url"));
                bean.setActiveFlag(rs.getBoolean("active_flag"));
                recipeList.add(bean);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(pstmt != null) {
                    pstmt.close();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
            MySqlConnectionManager.getInstance().closeConnection();
        }
        return recipeList;
    }

    public RecipeBean getResipeById(int recipeId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        RecipeBean bean = new RecipeBean();


        try{
            String sql = "SELECT * FROM recipe WHERE recipe_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                bean.setRecipeId(rs.getInt("recipe_id"));
                bean.setMealId(rs.getString("meal_id"));
                bean.setRecipeTitle(rs.getString("recipe_title"));
                bean.setImageUrl(rs.getString("image_url"));
                bean.setInstructions(rs.getString("instructions"));
                bean.setIngredientsJp(rs.getString("ingredients_jp"));
                bean.setIngredientsEn(rs.getString("ingredients_en"));
                bean.setActiveFlag(rs.getBoolean("active_flag"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(pstmt != null) {
                    pstmt.close();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
            MySqlConnectionManager.getInstance().closeConnection();
        }

        return bean;
    }

    public void editRecipe(RecipeBean bean) {
         Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = """
                        UPDATE recipe SET instructions = ?, 
                        ingredients_jp = ?, ingredients_en = ?, active_flag = ? 
                        WHERE recipe_id = ?
                        """;
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, bean.getInstructions());
            pstmt.setString(2, bean.getIngredientsJp());
            pstmt.setString(3, bean.getIngredientsEn());
            pstmt.setBoolean(4, bean.getActiveFlag());
            pstmt.setInt(5, bean.getRecipeId());


            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getRecipesCount() {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        int count = 0;
        try {

            String sql = "SELECT COUNT(*) AS count FROM recipe";

            pstmt = conn.prepareStatement(sql); 
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                count = rs.getInt("count");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return count;
    }
}
