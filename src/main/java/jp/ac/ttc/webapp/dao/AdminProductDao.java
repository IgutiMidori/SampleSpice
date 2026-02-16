package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

/**
 * 管理者ユーザーの機能を提供するうえで必要な
 * Products表のデータを取得するメソッド群
 * @author takumi-suzuki23
 * @since 2026-01-16
 */
public class AdminProductDao {
    public int getProductCount() {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        int count = 0;
        try {

            String sql = "SELECT COUNT(*) AS count FROM products";

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

    /**
     * 商品があるかを判定するメソッド
     * @param apiItemId APIにおける商品ID 次の値は許可しない。 {@code null }   
     * @return 商品が一つでもあれば {@code true} なければ {@code false}
     */
    public boolean hasProductAsApi(String apiItemId) {

        //コネクション取得
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        //戻り値で使用する真偽値のデフォルト値
        boolean isExists = false;

        try{
            //SQL文。正直COUNTとか返してもいい
            String sql = "SELECT product_name, api_item_id FROM products WHERE api_item_id = ?";
            //ステートメント作成
            pstmt = conn.prepareStatement(sql);

            //apiItemIdをバインド変数にセット
            pstmt.setString(1, apiItemId);
            //SQL実行
            ResultSet rs = pstmt.executeQuery();
            
            //結果セットが一件でもあればtrue
            if(rs.next()) {
                isExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        //最終的な真偽値を戻す
        return isExists;
    }

    /**
     * 商品追加を行うメソッド
     * @param product 一つの商品 一部許可する値 {@code product.originCountry null}
     * @return {@code int productId} 商品新規追加時に新しく生成された主キー
     */
    public int addProduct(ProductBean product) {
        //コネクション取得
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = """
                            INSERT INTO products (api_item_id, product_name, 
                            price, capacity, origin_country, image_url, 
                            product_description, spice_id) 
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """;
            //ステートメント作成・定数:RETURN_GENERATED_KEYを指定して登録時の主キーを取得できるようにする
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //Bean経由で与えられたプロパティをそれぞれバインド変数にセット
            pstmt.setString(1, product.getApiItemId());
            pstmt.setString(2, product.getProductName());
            pstmt.setInt(3, product.getPrice());
            pstmt.setInt(4, product.getCapacity());
            pstmt.setString(5, product.getOriginCountry());
            pstmt.setString(6, product.getImageUrl());
            pstmt.setString(7, product.getProductDescription());
            pstmt.setInt(8, product.getSpiceId());
            

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                //実行完了時に主キーを返す
                return rs.getInt(1); 
            }
            //何かしらの失敗で結果セットがない場合-1を返す
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            //例外発生時は-1を返す。ロールバックもする
            MySqlConnectionManager.getInstance().rollback();
            return -1;
        } finally {
            try {
                if (pstmt != null) {
                    //ステートメント閉じる
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * データベースにおける商品IDをAPIのIDから取得する
     * @param apiItemId この値は{@code null}を許可しない
     * @return {@code productId}：商品ID
     */
    public int getProductIdByApiItemId(String apiItemId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        int productId = -1;

        try {
            String sql = "SELECT product_id FROM products WHERE api_item_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, apiItemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                productId = rs.getInt("product_id");
            }
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

        return productId;
    }

    /**
     * 商品に付随する情報を初期化する。
     * @param productId 紐づけ先のproduct_id
     */
    public void initProductStatus(int productId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            //SQL文product_id以外は0で初期化します
            String sql = """
                            INSERT INTO product_status (product_id, stock_quantity, 
                            sales_volume) VALUES (?, 0, 0)
                        """;
            //ステートメント作成
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, productId);
            //SQL実行
            pstmt.executeUpdate();
        } catch (SQLException e) {
            //例外発生時はロールバック
            e.printStackTrace();
            MySqlConnectionManager.getInstance().rollback();
        } finally {
            //ラストにステートメント閉じる
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void editProductStatus(ProductBean product) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE product_status SET stock_quantity = ?, active_flag = ?  WHERE product_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, product.getStockQuantity());
            pstmt.setBoolean(2, product.getActiveFlag());
            pstmt.setInt(3, product.getProductId());

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

    public void editProduct(ProductBean product) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE products SET price = ?, origin_country = ?  WHERE product_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, product.getPrice());
            pstmt.setString(2, product.getOriginCountry());
            pstmt.setInt(3, product.getProductId());

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

    public List<ProductBean> getAllProducts(int limit, int offset) {
        // Implementation for retrieving all products for admin view
        
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<ProductBean> productList = new ArrayList<>();
        try {
            String sql = """
                           SELECT p.product_id, p.api_item_id, p.product_name, p.price, p.capacity, 
                           p.image_url, p.origin_country, p.product_description, ps.stock_quantity, ps.sales_volume, 
                           ps.active_flag FROM products p JOIN 
                           product_status ps ON p.product_id = ps.product_id
                           ORDER BY p.product_id LIMIT ? OFFSET ?
                           """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductBean product = new ProductBean();
                product.setProductId(rs.getInt("product_id"));
                product.setApiItemId(rs.getString("api_item_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getInt("price"));
                product.setCapacity(rs.getInt("capacity"));
                product.setOriginCountry(rs.getString("origin_country"));
                product.setImageUrl(rs.getString("image_url"));
                product.setProductDescription(rs.getString("product_description"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setSalesVolume(rs.getInt("sales_volume"));
                product.setActiveFlag(rs.getBoolean("active_flag"));
                productList.add(product);
            }
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
        return productList;
    }

    public ProductBean getProductById(int productId) {
        // Implementation for retrieving a product by its ID
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ProductBean product = null;

        try {
            String sql = "SELECT p.product_id, p.api_item_id, p.product_name, p.price, p.capacity, p.origin_country, p.image_url, p.product_description, ps.stock_quantity, ps.sales_volume, ps.updated_at, ps.active_flag "
                       + "FROM products p JOIN product_status ps ON p.product_id = ps.product_id WHERE p.product_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                product = new ProductBean();
                product.setProductId(rs.getInt("product_id"));
                product.setApiItemId(rs.getString("api_item_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getInt("price"));
                product.setCapacity(rs.getInt("capacity"));
                product.setOriginCountry(rs.getString("origin_country"));
                product.setImageUrl(rs.getString("image_url"));
                product.setProductDescription(rs.getString("product_description"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setSalesVolume(rs.getInt("sales_volume"));
                product.setActiveFlag(rs.getBoolean("active_flag"));
                product.setUpdatedAt(rs.getString("updated_at"));
            }
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
        return product;
    }

    public void editProductDetail(int productId, boolean activeFlag, int capacity, String originCountry, String productDescription, int price, int stockQuantity) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = """
                            UPDATE products p JOIN product_status ps ON 
                            p.product_id = ps.product_id SET ps.active_flag = ?, 
                            p.capacity = ?, p.origin_country = ?, p.product_description = ?, 
                            p.price = ?, ps.stock_quantity = ? WHERE p.product_id = ?
                        """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, activeFlag);
            pstmt.setInt(2, capacity);
            pstmt.setString(3, originCountry);
            pstmt.setString(4, productDescription);
            pstmt.setInt(5, price);
            pstmt.setInt(6, stockQuantity);
            pstmt.setInt(7, productId);

            pstmt.executeUpdate();
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