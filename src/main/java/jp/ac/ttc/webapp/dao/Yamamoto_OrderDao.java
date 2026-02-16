package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class Yamamoto_OrderDao {

    // オーダーレコード表に追加するメソッド
    public int addOrderRecord(int userId, CartBean cart, int addressId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int orderRecordId = 0;

        try {
            String sql = "INSERT INTO order_record(user_id, total_amount, address_id) VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, cart.getSubtotal());
            pstmt.setInt(3, addressId);
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                orderRecordId = rs.getInt(1);
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
        return orderRecordId;
    }

    // 注文された商品を一括で order_items 表に登録する
    public void addOrderItems(int orderRecordId, List<CartItemBean> cartItems) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO order_items (order_record_id, product_id, product_quantity) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            for (CartItemBean item : cartItems) {
                pstmt.setInt(1, orderRecordId);
                pstmt.setInt(2, item.getProduct().getProductId());
                pstmt.setInt(3, item.getProductQuantity());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
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

    public void cancelOrder(int orderRecordId) {
        // 未実装
    }
}