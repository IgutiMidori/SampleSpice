package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class CartDao {

    // カートが存在するか確認
    public boolean isCartCreated(int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            String sql = "SELECT COUNT(*) FROM cart WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exists;
    }

    // カートを新規作成する
    public int createCart(int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int cartId = 0;
        try {
            String sql = "INSERT INTO cart (user_id,subtotal) VALUES(?,0)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    cartId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cartId;
    }

    // カート削除
    public void deleteCart(int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        try {
            String sqlItems = "DELETE FROM cart_items WHERE cart_id = (SELECT cart_id FROM cart WHERE user_id = ?)";
            pstmt = conn.prepareStatement(sqlItems);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            pstmt.close();

            String sqlCart = "DELETE FROM cart WHERE user_id = ?";
            pstmt = conn.prepareStatement(sqlCart);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public CartBean getCart(int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        CartBean cartBean = new CartBean();
        try {
            String sql = "SELECT cart_id, subtotal FROM cart WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cartBean.setCartId(rs.getInt("cart_id"));
                cartBean.setSubtotal(rs.getInt("subtotal"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cartBean;
    }

    public void updateSubtotal(int cartId, int subtotal) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE cart SET subtotal = ? WHERE cart_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, subtotal);
            pstmt.setInt(2, cartId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshSubtotal(int cartId) {
        List<CartItemBean> items = getCartItems(cartId);
        int total = 0;
        for (CartItemBean item : items) {
            total += item.getProduct().getPrice() * item.getProductQuantity();
        }
        updateSubtotal(cartId, total);
    }

    public List<CartItemBean> getCartItems(int cartId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<CartItemBean> cartItems = new ArrayList<>();
        try {
            String sql = "SELECT cart.cart_item_id, cart.product_id, cart.product_quantity, " +
                    "pro.product_name, pro.price, pro.capacity, pro.origin_country, pro.image_url " +
                    "FROM cart_items cart " +
                    "JOIN products pro " +
                    "ON cart.product_id = pro.product_id " +
                    "WHERE cart.cart_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cartId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CartItemBean cartItem = new CartItemBean();
                ProductBean product = new ProductBean();
                cartItem.setCartItemId(rs.getInt("cart_item_id"));
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getInt("price"));
                product.setCapacity(rs.getInt("capacity"));
                product.setOriginCountry(rs.getString("origin_country"));
                product.setImageUrl(rs.getString("image_url"));
                cartItem.setProduct(product);
                cartItem.setProductQuantity(rs.getInt("product_quantity"));
                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cartItems;
    }

    public void addCartItem(int cartId, int productId, int product_quantity) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO cart_items (cart_id, product_id, product_quantity) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE product_quantity = product_quantity + VALUES(product_quantity)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cartId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, product_quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeCartItem(int cartItemId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        try {
            String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cartItemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateProductQuantity(int cartItemid, int quantity) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE cart_items SET product_quantity = ? WHERE cart_item_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, cartItemid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}