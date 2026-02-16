package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;


public class RecipeDao{
    public List<RecipeBean> getPopularRecipes(int userId){
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
            WHERE r.active_flag = true
            ORDER BY COALESCE(fr_cnt.f_cnt, 0) DESC LIMIT 20
            """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
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

    public List<RecipeBean> getSearchRecipes(int userId, String recipeTitle, String foodName, Integer spiceId, int limit, int offset){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<RecipeBean> recipes = new ArrayList<>();
        int recipeCount = 0;  //表示対象のレシピ件数
        int index = 1; //バインド変数のバインド位置をカウントする変数

        try{
            //元となるSQL文を用意
			StringBuilder baseSql = new StringBuilder("""
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
            INNER JOIN recipe_spice rs ON r.recipe_id = rs.recipe_id
            INNER JOIN( SELECT spice_id FROM spices) s ON rs.spice_id = s.spice_id
            WHERE r.active_flag = true
            """);


            //レシピ名と材料名の複合検索した場合のパラメータを受け取る変数
            ArrayList<String> rParams = new ArrayList<>();
            ArrayList<String> fParams = new ArrayList<>();

            //入力された検索条件の有無を判別
            if(recipeTitle != null && !recipeTitle.isBlank()){
                //複合検索の場合
				if(recipeTitle.matches(".*[\\s　].*")){
					// \s(タブなどを含む半角スペース)と全角スペースでsplit
					for(String temp : recipeTitle.split("[\\s　]")){
						//分割した文字列の空白チェック
						if(!temp.isEmpty()){
							baseSql.append(" AND r.recipe_title LIKE ?");
							rParams.add(temp);
						}
					}
				}else{
					baseSql.append(" AND r.recipe_title LIKE ?");
				}
            }
            if(foodName != null && !foodName.isBlank()){
                //複合検索の場合
				if(foodName.matches(".*[\\s　].*")){
					// \s(タブなどを含む半角スペース)と全角スペースでsplit
					for(String temp : foodName.split("[\\s　]")){
						//分割した文字列の空白チェック
						if(!temp.isEmpty()){
							baseSql.append(" AND (r.ingredients_jp LIKE ? OR r.ingredients_en LIKE ?)");
							fParams.add(temp);
						}
					}
				}else{
					baseSql.append(" AND (r.ingredients_jp LIKE ? OR r.ingredients_en LIKE ?)");
				}
            }
            if(spiceId != null){
				baseSql.append(" AND s.spice_id = ?");
			}
            baseSql.append(" ORDER BY r.recipe_id LIMIT ? OFFSET ?");

            String sql = baseSql.toString();  //StringBuilderをStringに変換
			pstmt = conn.prepareStatement(sql);

            //バインドの実行
            pstmt.setInt(index++, userId);  //userIdをバインド
            if(recipeTitle != null && !recipeTitle.isBlank()){
                //複合検索の場合
				if(recipeTitle.matches(".*[\\s　].*")){
					for(String temp : rParams){
						pstmt.setString(index++, "%" + temp + "%");
					}
				}else{
					pstmt.setString(index++, "%" + recipeTitle + "%");
				}
            }
            if(foodName != null && !foodName.isBlank()){
                //複合検索の場合
				if(foodName.matches(".*[\\s　].*")){
					for(String temp : fParams){
                        pstmt.setString(index++, "%" + temp + "%");
                        pstmt.setString(index++, "%" + temp + "%");
					}
				}else{
					pstmt.setString(index++, "%" + foodName + "%");
                    pstmt.setString(index++, "%" + foodName + "%");
				}
            }
            if(spiceId != null){
				pstmt.setObject(index++, spiceId, java.sql.Types.INTEGER);
			}
            pstmt.setInt(index++, limit);
            pstmt.setInt(index++, offset);

            // SQL実行
			ResultSet rs = pstmt.executeQuery();

            //レシピの重複を判別する変数
            int currentRecipeId = 0;
            int lastRecipeId = 0;

            while(rs.next()){
                currentRecipeId = rs.getInt("recipe_id");
                
                if(currentRecipeId != lastRecipeId){
                    RecipeBean recipe = new RecipeBean();
                    recipe.setRecipeId(rs.getInt("recipe_id"));
                    recipe.setRecipeTitle(rs.getString("recipe_title"));
                    recipe.setImageUrl(rs.getString("recipe_image"));
                    recipe.setFavoriteUserCount(rs.getInt("favorite_count"));
                    recipe.setFavoriteFlag(rs.getBoolean("favorite_flag"));

                    recipes.add(recipe);  //recipeをArrayListに追加

                    lastRecipeId = currentRecipeId;
                }
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

    public int getRecipeCount(String recipeTitle, String foodName, Integer spiceId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<RecipeBean> recipes = new ArrayList<>();
        int recipeCount = 0;  //表示対象のレシピ件数
        int index = 1; //バインド変数のバインド位置をカウントする変数

        try{
            //元となるSQL文を用意
			StringBuilder baseSql = new StringBuilder("""
            SELECT COUNT(*) AS cnt
            FROM recipe r
            INNER JOIN recipe_spice rs ON r.recipe_id = rs.recipe_id
            INNER JOIN( SELECT spice_id FROM spices) s ON rs.spice_id = s.spice_id
            WHERE active_flag = true 
            """);

            //レシピ名と材料名の複合検索した場合のパラメータを受け取る変数
            ArrayList<String> rParams = new ArrayList<>();
            ArrayList<String> fParams = new ArrayList<>();

            //入力された検索条件の有無を判別
            if(recipeTitle != null && !recipeTitle.isBlank()){
                //複合検索の場合
				if(recipeTitle.matches(".*[\\s　].*")){
					// \s(タブなどを含む半角スペース)と全角スペースでsplit
					for(String temp : recipeTitle.split("[\\s　]")){
						//分割した文字列の空白チェック
						if(!temp.isEmpty()){
							baseSql.append(" AND r.recipe_title LIKE ?");
							rParams.add(temp);
						}
					}
				}else{
					baseSql.append(" AND r.recipe_title LIKE ?");
				}
            }
            if(foodName != null && !foodName.isBlank()){
                //複合検索の場合
				if(foodName.matches(".*[\\s　].*")){
					// \s(タブなどを含む半角スペース)と全角スペースでsplit
					for(String temp : foodName.split("[\\s　]")){
						//分割した文字列の空白チェック
						if(!temp.isEmpty()){
							baseSql.append(" AND r.food_name LIKE ?");
							fParams.add(temp);
						}
					}
				}else{
					baseSql.append(" AND r.food_name LIKE ?");
				}
            }
            if(spiceId != null){
				baseSql.append(" AND s.spice_id = ?");
			}

            String sql = baseSql.toString();  //StringBuilderをStringに変換
			pstmt = conn.prepareStatement(sql);

            //バインドの実行
            if(recipeTitle != null && !recipeTitle.isBlank()){
                //複合検索の場合
				if(recipeTitle.matches(".*[\\s　].*")){
					for(String temp : rParams){
						pstmt.setString(index++, "%" + temp + "%");
					}
				}else{
					pstmt.setString(index++, "%" + recipeTitle + "%");
				}
            }
            if(foodName != null && !foodName.isBlank()){
                //複合検索の場合
				if(foodName.matches(".*[\\s　].*")){
					for(String temp : fParams){
                        pstmt.setString(index++, "%" + temp + "%");
					}
				}else{
					pstmt.setString(index++, "%" + foodName + "%");
				}
            }
            if(spiceId != null){
				pstmt.setObject(index++, spiceId, java.sql.Types.INTEGER);
			}

            // SQL実行
			ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                recipeCount = rs.getInt("cnt");
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

        return recipeCount;
    }

    public RecipeBean getRecipeDetail(int userId, int recipeId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        RecipeBean recipe = new RecipeBean();

        try{
            String sql = """
            SELECT r.recipe_id,
                   r.recipe_title,
                   r.image_url AS recipe_image,
                   r.instructions,
                   r.ingredients_jp,
                   r.ingredients_en,
                   COALESCE(fr_cnt.f_cnt, 0) AS favorite_count,
                   (COALESCE(fr_user.f_flag, 0) > 0) AS favorite_flag,
                   s.spice_id,
                   s.spice_name_jp,
                   s.spice_name_en,
                   s.image_url AS spice_image
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
            INNER JOIN recipe_spice rs ON r.recipe_id = rs.recipe_id
            INNER JOIN(
                SELECT spice_id, spice_name_jp, spice_name_en, image_url
                FROM spices
            ) s ON rs.spice_id = s.spice_id
            WHERE r.active_flag = true AND 
                  r.recipe_id = ?
            """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            ResultSet rs = pstmt.executeQuery();

            //レシピのデータを取得
            rs.next();
            recipe.setRecipeId(rs.getInt("recipe_id"));
            recipe.setRecipeTitle(rs.getString("recipe_title"));
            recipe.setImageUrl(rs.getString("recipe_image"));
            recipe.setInstructions(rs.getString("instructions"));
            recipe.setIngredientsJp(rs.getString("ingredients_jp"));
            recipe.setIngredientsEn(rs.getString("ingredients_en"));
            recipe.setFavoriteUserCount(rs.getInt("favorite_count"));
            recipe.setFavoriteFlag(rs.getBoolean("favorite_flag"));
            recipe.setSpices(new ArrayList<SpiceBean>());
            //spiceのデータを取得し、recipeに追加
            do{
                SpiceBean spice = new SpiceBean();
                spice.setSpiceId(rs.getInt("spice_id"));
                spice.setSpiceNameJp(rs.getString("spice_name_jp"));
                spice.setSpiceNameEn(rs.getString("spice_name_en"));
                spice.setImageUrl(rs.getString("spice_image"));

                recipe.addSpice(spice);
            }while(rs.next());
            
            //ResultSetをClose
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

        return recipe;
    }
}