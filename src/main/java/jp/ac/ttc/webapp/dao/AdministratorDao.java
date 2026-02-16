package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class AdministratorDao {
    public UserBean findByNameAndId(String name, String tel) {
        UserBean user = null;
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = "SELECT administrator_id, administrator_name, password_hash FROM administrator WHERE administrator_name = ? AND administrator_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, tel);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("administrator_id");
                String userName = rs.getString("administrator_name");
                String passwordHash = rs.getString("password_hash");

                user = new UserBean(userId, userName, null, null, passwordHash);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}