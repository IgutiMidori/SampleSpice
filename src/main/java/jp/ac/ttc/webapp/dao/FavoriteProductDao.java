package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;


public class FavoriteProductDao{
    public List<ProductBean> getFavoriteProducts(int userId, int limit, int offset){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<ProductBean> products = new ArrayList<>();

        try{
            String sql = """
            SELECT p.product_id,
				   p.product_name,
				   p.price,
				   p.capacity,
				   p.origin_country,
				   p.image_url,
				   ps.stock_quantity,
				   ps.sales_volume,
				   COALESCE(fp_cnt.f_cnt, 0) AS favorite_count,
				   (COALESCE(fp_user.f_flag, 0) > 0) AS favorite_flag
			FROM products p
			INNER JOIN product_status ps ON p.product_id = ps.product_id
			LEFT JOIN (
				SELECT product_id, COUNT(*) AS f_cnt
				FROM favorite_products
				GROUP BY product_id
			) fp_cnt ON p.product_id = fp_cnt.product_id
			LEFT JOIN (
  				SELECT product_id, COUNT(*) AS f_flag
  				FROM favorite_products
  				WHERE user_id = ?
  				GROUP BY product_id
			) fp_user ON p.product_id = fp_user.product_id
			WHERE ps.active_flag = true AND
                  (COALESCE(fp_user.f_flag, 0) > 0) = true
            ORDER BY p.product_id LIMIT ? OFFSET ?
			""";

            pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()){
				ProductBean p = new ProductBean();
				//products表
				p.setProductId(rs.getInt("product_id"));
				p.setProductName(rs.getString("product_name"));
				p.setPrice(rs.getInt("price"));
				p.setCapacity(rs.getInt("capacity"));
				p.setOriginCountry(rs.getString("origin_country"));
				p.setImageUrl(rs.getString("image_url"));
				//product_status表
				p.setStockQuantity(rs.getInt("stock_quantity"));
				p.setSalesVolume(rs.getInt("sales_volume"));
				//favorite_products表
				p.setFavoriteUserCount(rs.getInt("favorite_count"));
				p.setFavoriteFlag(rs.getBoolean("favorite_flag"));

				products.add(p);
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
        
        return products;
    }

    public void addFavoriteProduct(int userId, int productId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;

        try{
            //SQLを用意
            String sql = "INSERT INTO favorite_products (user_id, product_id) VALUES(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
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

    public void removeFavoriteProduct(int userId, int productId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;

        try{
            //SQLを用意
            String sql = "DELETE FROM favorite_products WHERE user_id = ? AND product_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
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

    public int getFavoriteProductCount(int userId) {
        int count = 0;
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            String sql = "SELECT COUNT(*) FROM favorite_products WHERE user_id = ?";
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