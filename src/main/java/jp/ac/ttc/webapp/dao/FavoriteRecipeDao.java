package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;


public class FavoriteRecipeDao{
    public List<RecipeBean> getFavoriteRecipes(int userId, int limit, int offset){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<RecipeBean> recipes = new ArrayList<>();

        try{
            String sql = """
            SELECT r.recipe_id,
                   r.recipe_title,
                   r.image_url AS recipe_image,
                   COALESCE(fr_cnt.f_cnt, 0) AS favorite_count,
                   (COALESCE(fr_user.f_flag, 0) > 0) AS favorite_flag
            FROM recipe r
            LEFT JOIN(
                SELECT recipe_id, COUNT(*) AS f_cnt
                FROM favorite_recipes
                GROUP BY recipe_id
            ) fr_cnt ON r.recipe_id = fr_cnt.recipe_id
            LEFT JOIN(
                SELECT recipe_id, COUNT(*) AS f_flag
                FROM favorite_recipes
                where user_id = ?
                GROUP BY recipe_id
            ) fr_user ON r.recipe_id = fr_user.recipe_id
            WHERE r.active_flag = true AND
                  (COALESCE(fr_user.f_flag, 0) > 0) = true
            ORDER BY r.recipe_id LIMIT ? OFFSET ?
            """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                RecipeBean recipe = new RecipeBean();
                recipe.setRecipeId(rs.getInt("recipe_id"));
                recipe.setRecipeTitle(rs.getString("recipe_title"));
                recipe.setImageUrl(rs.getString("recipe_image"));
                recipe.setFavoriteUserCount(rs.getInt("favorite_count"));
                recipe.setFavoriteFlag(rs.getBoolean("favorite_flag"));

                recipes.add(recipe);  //recipeをArrayListに追加
            }

            rs.close();
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
			try{
				if(pstmt != null){
					pstmt.close();
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}

        return recipes;
    }

    public void addFavoriteRecipe(int userId, int recipeId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;

        try{
            //SQLを用意
            String sql = "INSERT INTO favorite_recipes (user_id, recipe_id) VALUES(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        }finally{
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFavoriteRecipe(int userId, int recipeId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;

        try{
            //SQLを用意
            String sql = "DELETE FROM favorite_recipes WHERE user_id = ? AND recipe_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        }finally{
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getFavoriteRecipeCount(int userId) {
        int count = 0;
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            String sql = "SELECT COUNT(*) FROM favorite_recipes WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if(rs.next()){
                count = rs.getInt(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try{
                if(rs != null){
                    rs.close();
                }
                if(pstmt != null){
                    pstmt.close();
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }

        return count;
    }
}