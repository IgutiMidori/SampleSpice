package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.AddressBean;
import jp.ac.ttc.webapp.bean.CartBean;
import jp.ac.ttc.webapp.bean.CartItemBean;
import jp.ac.ttc.webapp.bean.OrderItemBean;
import jp.ac.ttc.webapp.bean.OrderRecordBean;
import jp.ac.ttc.webapp.bean.ProductBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class OrderDao {

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

    // 未実装
    public void cancelOrder(int orderRecordId) {
        // 未実装
    }

        public List<OrderRecordBean> getOrderRecords(int userId, int limit, int offset){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<OrderRecordBean> orders = new ArrayList<>();

		try{
			String sql = """
			SELECT r.order_record_id,
                   r.total_amount,
                   r.ordered_at,
                   r.order_flag,
                   icount.item_count
            FROM order_record r
            INNER JOIN(
                SELECT order_record_id, SUM(product_quantity) AS item_count
                FROM order_items 
                GROUP BY order_record_id
            ) icount ON r.order_record_id = icount.order_record_id
            WHERE r.user_id = ?
            ORDER BY r.ordered_at DESC LIMIT ? OFFSET ?
			""";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);  //取得件数（飛ばしたところから）
            pstmt.setInt(3, offset); //非取得件数（並べた頭から）
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()){
				OrderRecordBean order = new OrderRecordBean();

                //order_record表
                order.setOrderRecordId(rs.getInt("order_record_id"));
                order.setTotalAmount(rs.getInt("total_amount"));
                order.setOrderedAt(rs.getString("ordered_at"));
                order.setOrderFlag(rs.getBoolean("order_flag"));

                //order_items表（購入商品点数）
                order.setOrderItemCount(rs.getInt("item_count"));

                orders.add(order);
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
		return orders;
    }

    public int getOrderRecordCount(int userId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
        int count = 0;

        String sql = """
            SELECT COUNT(*) AS cnt FROM order_record WHERE user_id = ?
        """;

        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                count = rs.getInt("cnt");
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

        return count;
    }


    public OrderRecordBean getOrderItems(int orderId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
        OrderRecordBean order = new OrderRecordBean();

		try{
			String sql = """
			SELECT r.order_record_id,
                   r.total_amount,
                   r.ordered_at,
                   r.order_flag,
                   icount.item_count,
                   a.receiver_name,
                   a.receiver_name_reading,
                   a.phone_number,
                   a.postal_code,
                   a.delivery_address,
                   i.order_item_id,
                   i.product_quantity,
                   p.product_id,
                   p.product_name,
                   p.price,
                   p.capacity,
                   p.origin_country,
                   p.image_url
            FROM order_record r
            INNER JOIN address a ON r.address_id = a.address_id
            INNER JOIN(
                SELECT order_record_id, SUM(product_quantity) AS item_count
                FROM order_items 
                GROUP BY order_record_id
            ) icount ON r.order_record_id = icount.order_record_id
            INNER JOIN order_items i ON r.order_record_id = i.order_record_id
            INNER JOIN(
                SELECT product_id,
                       product_name,
                       price,
                       capacity,
                       origin_country,
                       image_url
                FROM products
            ) p ON i.product_id = p.product_id
            WHERE r.order_record_id = ?
			""";
			pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();

			rs.next();

            //order_recordとaddressは一件のみ
            AddressBean address = new AddressBean();
            List<OrderItemBean> items = new ArrayList<>();

            //order_record表
            order.setOrderRecordId(rs.getInt("order_record_id"));
            order.setTotalAmount(rs.getInt("total_amount"));
            order.setOrderedAt(rs.getString("ordered_at"));
            order.setOrderFlag(rs.getBoolean("order_flag"));
            //order_items表（購入商品点数）
            order.setOrderItemCount(rs.getInt("item_count"));
            //address表
            address.setReceiverName(rs.getString("receiver_name"));
            address.setReceiverNameReading(rs.getString("receiver_name_reading"));
            address.setPhoneNumber(rs.getString("phone_number"));
            address.setPostalCode(rs.getString("postal_code"));
            address.setDeliveryAddress(rs.getString("delivery_address"));
            order.setAddress(address);

            //order_itemsとproductは複数件
            do{
                ProductBean product = new ProductBean();
                OrderItemBean item = new OrderItemBean();

                //product表
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getInt("price"));
                product.setCapacity(rs.getInt("capacity"));
                product.setOriginCountry(rs.getString("origin_country"));
                product.setImageUrl(rs.getString("image_url"));

                //order_items表
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setProduct(product);
                item.setQuantity(rs.getInt("product_quantity"));

                items.add(item);
            }while(rs.next());
			
            order.setOrderItems(items); //recordに商品をセット
			
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
		return order;
    }

}