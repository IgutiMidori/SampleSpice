package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.AddressBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class AddressDao {

    // アドレス表に追加する
    public int addAddress(AddressBean addressBean, int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int addressId = 0;
        try {
            String sql = "INSERT INTO address (user_id, receiver_name, receiver_name_reading, phone_number, postal_code, delivery_address) VALUES(?, ?, ?, ?, ?, ?)";
            // 第2引数に自動採番キー取得のフラグを追加
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setString(2, addressBean.getReceiverName());
            pstmt.setString(3, addressBean.getReceiverNameReading());
            pstmt.setString(4, addressBean.getPhoneNumber());
            pstmt.setString(5, addressBean.getPostalCode());
            pstmt.setString(6, addressBean.getDeliveryAddress());

            pstmt.executeUpdate();

            // 追加されたアドレスの自動採番IDを取得
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                addressId = rs.getInt(1);
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
        return addressId;
    }

    // userIdでアドレスを取得
    public List<AddressBean> getAddress(int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<AddressBean> addressList = new ArrayList<AddressBean>();
        try {
            String sql = "SELECT address_id,receiver_name, receiver_name_reading, phone_number, postal_code, delivery_address FROM address WHERE user_id = ? ORDER BY  created_at DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AddressBean addressBean = new AddressBean();
                addressBean.setAddressId(rs.getInt("address_id"));
                addressBean.setReceiverName(rs.getString("receiver_name"));
                addressBean.setReceiverNameReading(rs.getString("receiver_name_reading"));
                addressBean.setPhoneNumber(rs.getString("phone_number"));
                addressBean.setPostalCode(rs.getString("postal_code"));
                addressBean.setDeliveryAddress(rs.getString("delivery_address"));
                addressList.add(addressBean);
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
        return addressList;
    }

    // アドレスを更新する
    // 未使用だけど消すのもったいないので残してます。
    public boolean editAddress(AddressBean addressBean, int userId) {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        boolean success = false;
        try {
            String sql = "UPDATE address SET receiver_name = ?, receiver_name_reading = ?, phone_number = ?, postal_code = ?, delivery_address = ? WHERE address_Id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, addressBean.getReceiverName());
            pstmt.setString(2, addressBean.getReceiverNameReading());
            pstmt.setString(3, addressBean.getPhoneNumber());
            pstmt.setString(4, addressBean.getPostalCode());
            pstmt.setString(5, addressBean.getDeliveryAddress());
            pstmt.setInt(6, addressBean.getAddressId());
            pstmt.setInt(7, userId);

            int rowsUpdated = pstmt.executeUpdate();
            success = (rowsUpdated > 0);
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
        return success;
    }
}