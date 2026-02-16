package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;


public class ProductDao{
    public List<ProductBean> getProducts(int userId, int limit, int offset){
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
			WHERE ps.active_flag = true
			ORDER BY p.product_id 
			LIMIT ? OFFSET ?
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

	public List<ProductBean> getSearchProducts(int userId, String productName, Integer spiceId, 
		Integer lowCapacity, Integer highCapacity, String originCountry, int limit, int offset) {
			
		Connection conn = MySqlConnectionManager.getInstance().getConnection();  //トランザクション開始
		PreparedStatement pstmt = null;
		List<ProductBean> products = new ArrayList<>();
		int index = 1; //バインド変数のバインド位置をカウントする変数

		try{
			//元となるSQL文を用意
			StringBuilder baseSql = new StringBuilder("""
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
			WHERE ps.active_flag = true
			""");

			//商品名で複合検索した場合のパラメータを受け取る変数
			ArrayList<String> params = new ArrayList<>();

			//入力された検索条件の有無を判別
			if((productName != null && !productName.isBlank()) || spiceId != null){
				if(productName != null && !productName.isBlank()){
					//複合検索の場合
					if(productName.matches(".*[\\s　].*")){
						// \s(タブなどを含む半角スペース)と全角スペースでsplit
						for(String temp : productName.split("[\\s　]")){
							//分割した文字列の空白チェック
							if(!temp.isEmpty()){
								baseSql.append(" AND p.product_name LIKE ?");
								params.add(temp);
							}
						}
					}else{
						baseSql.append(" AND p.product_name LIKE ?");
					}
				}else if(spiceId != null){
					baseSql.append(" AND spice_id = ?");
				}

				if (lowCapacity != null && highCapacity != null) {
                baseSql.append(" AND capacity BETWEEN ? AND ?");
            	}
            	if (originCountry != null && !originCountry.isBlank()) {
            	    baseSql.append(" AND origin_country LIKE ?");
            	}
			}else if((lowCapacity != null && highCapacity != null) || originCountry != null && !originCountry.isBlank()){
				//例外処理を行う想定
				System.out.println("商品検索：商品名かスパイスIDがありません");
			}

			baseSql.append(" ORDER BY p.product_id LIMIT ? OFFSET ?");

			String sql = baseSql.toString();  //StringBuilderをStringに変換
			pstmt = conn.prepareStatement(sql);

			//バインド変数の置換
			pstmt.setInt(index++, userId); //userIdをバインド
			if((productName != null && !productName.isBlank()) || spiceId != null){
				if(productName != null && !productName.isBlank()){
					//複合検索の場合
					if(productName.matches(".*[\\s　].*")){
						// 検索文字列を一つずつ取得
						for(String temp : params){
							// 一つずつバインド
							pstmt.setString(index++, "%" + temp + "%");
						}
					}else{
						pstmt.setString(index++, "%" + productName + "%");
					}
				}else if(spiceId != null){
					pstmt.setObject(index++, spiceId, java.sql.Types.INTEGER);
				}

				if (lowCapacity != null && highCapacity != null) {
                	pstmt.setObject(index++, lowCapacity, java.sql.Types.INTEGER);
					pstmt.setObject(index++, highCapacity, java.sql.Types.INTEGER);
            	}
            	if (originCountry != null && !originCountry.isBlank()) {
            	    pstmt.setString(index++, "%" + originCountry + "%");
            	}
			}else if((lowCapacity != null && highCapacity != null) || originCountry != null && !originCountry.isBlank()){
				//例外処理を行う想定
				System.out.println("商品検索：商品名かスパイスIDがありません");
			}

			//ページング用のバインド
			pstmt.setInt(index++, limit);
			pstmt.setInt(index++, offset);

			// SQL実行
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

	public int getProductCount(String productName, Integer spiceId, Integer lowCapacity, Integer highCapacity, String originCountry){
		Connection conn = MySqlConnectionManager.getInstance().getConnection();  //トランザクション開始
		PreparedStatement pstmt = null;
		int productCount = 0;  //表示対象の商品件数
		int index = 1; //バインド変数のバインド位置をカウントする変数

		try{
			//元となるSQL文を用意
			StringBuilder baseSql = new StringBuilder("""
			SELECT COUNT(*) AS cnt 
			FROM products p
			INNER JOIN( SELECT product_id, active_flag FROM product_status )
			ps ON p.product_id = ps.product_id
			WHERE ps.active_flag = true
			""");

			//商品名で複合検索した場合のパラメータを受け取る変数
			ArrayList<String> params = new ArrayList<>();

			//入力された検索条件の有無を判別
			if(productName != null && !productName.isBlank()){
				//複合検索の場合
				if(productName.matches(".*[\\s　].*")){
					// \s(タブなどを含む半角スペース)と全角スペースでsplit
					for(String temp : productName.split("[\\s　]")){
						//分割した文字列の空白チェック
						if(!temp.isEmpty()){
							baseSql.append(" AND p.product_name LIKE ?");
							params.add(temp);
						}
					}
				}else{
					baseSql.append(" AND p.product_name LIKE ?");
				}
			}
			if(spiceId != null){
				baseSql.append(" AND spice_id = ?");
			}
			if (lowCapacity != null && highCapacity != null) {
                baseSql.append(" AND capacity BETWEEN ? AND ?");
            }
            if (originCountry != null && !originCountry.isBlank()) {
                baseSql.append(" AND origin_country LIKE ?");
            }

			String sql = baseSql.toString();  //StringBuilderをStringに変換
			pstmt = conn.prepareStatement(sql);

			//入力された検索条件の有無を判別
			if(productName != null && !productName.isBlank()){
				//複合検索の場合
				if(productName.matches(".*[\\s　].*")){
					// 検索文字列を一つずつ取得
					for(String temp : params){
						// 一つずつバインド
						pstmt.setString(index++, "%" + temp + "%");
					}
				}else{
					pstmt.setString(index++, "%" + productName + "%");
				}
			}
			if(spiceId != null){
				pstmt.setObject(index++, spiceId, java.sql.Types.INTEGER);
			}
			if (lowCapacity != null && highCapacity != null) {
                pstmt.setObject(index++, lowCapacity, java.sql.Types.INTEGER);
				pstmt.setObject(index++, highCapacity, java.sql.Types.INTEGER);
            }
            if (originCountry != null && !originCountry.isBlank()) {
                pstmt.setString(index++, "%" + originCountry + "%");
            }

			// SQL実行
			ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                productCount = rs.getInt("cnt");
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
		return productCount;
	}

	public ProductBean getProductDetail(int userId, int productId){
		Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ProductBean product = new ProductBean();

		try{
			String sql = """
			SELECT p.product_id,
				   p.product_name,
				   p.price,
				   p.capacity,
				   p.origin_country,
				   p.image_url,
				   p.product_description,
				   ps.stock_quantity,
				   ps.sales_volume,
				   COALESCE(fp_cnt.f_cnt, 0) AS favorite_count,
				   (COALESCE(fp_user.f_flag, 0) > 0) AS favorite_flag,
				   s.spice_id,
				   s.spice_name_jp,
				   s.spice_name_en
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
			INNER JOIN (
                SELECT spice_id, spice_name_jp, spice_name_en
                FROM spices
            ) s ON p.spice_id = s.spice_id
			WHERE ps.active_flag = true AND
				  p.product_id = ?
			""";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, productId);
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
				p.setProductDescription(rs.getString("product_description"));
				//product_status表
				p.setStockQuantity(rs.getInt("stock_quantity"));
				p.setSalesVolume(rs.getInt("sales_volume"));
				//favorite_products表
				p.setFavoriteUserCount(rs.getInt("favorite_count"));
				p.setFavoriteFlag(rs.getBoolean("favorite_flag"));
				//spices表
				SpiceBean s = new SpiceBean();
				s.setSpiceId(rs.getInt("spice_id"));
				s.setSpiceNameJp(rs.getString("spice_name_jp"));
				s.setSpiceNameEn(rs.getString("spice_name_en"));
				p.setSpice(s);

				product = p;
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
		return product;
	}

	public List<String> getOriginCountries(){
		Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<String> countries = new ArrayList<>();

		try{
			String sql = "SELECT DISTINCT origin_country FROM products";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()){
				String country = rs.getString("origin_country");
				if(country == null || country.isBlank()){
					continue;
				}
				countries.add(country);
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
		
		return countries;
	}

	public int getStockQuantityByProductId(int productId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        int stock = 0;

        try {
            String sql = "SELECT stock_quantity FROM product_status WHERE product_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                stock = rs.getInt("stock_quantity");
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

        return stock;
    }

	public void reduceProductQuantityByCartItemList(List<CartItemBean> itemList) {
		Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;

		try {
			String sql = "UPDATE product_status SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
			pstmt = conn.prepareStatement(sql);

			for(CartItemBean item : itemList) {
				pstmt.setInt(1, item.getProductQuantity());
				pstmt.setInt(2, item.getProduct().getProductId());
				// バッチに追加
				pstmt.addBatch();
			}
			// バッチを実行
			pstmt.executeBatch();
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

	}
}