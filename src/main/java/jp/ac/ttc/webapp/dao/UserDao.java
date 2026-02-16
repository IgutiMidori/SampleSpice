package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.ttc.webapp.bean.UserBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class UserDao {
    public UserBean findByNameAndTel(String name, String email) {
        // データベースからnameとemailに基づいてユーザー情報を取得するロジックを実装
        UserBean user = null;
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        try {
            String sql = "SELECT user_id, user_name, password_hash, email,phone_number FROM users WHERE user_name = ? AND email = ? AND active_flag = true";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String userName = rs.getString("user_name");
                String passwordHash = rs.getString("password_hash");
                String useremail = rs.getString("email");
                String phoneNumber = rs.getString("phone_number");

                user = new UserBean(userId, userName, useremail, phoneNumber, passwordHash);
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
    public UserBean editUser(int userId, String name, String tel,String password) {
    UserBean user = new UserBean();
    user.setUserId(userId); // Always set the ID

    // We use one connection for both potential updates
    try (Connection conn = MySqlConnectionManager.getInstance().getConnection()) {

        conn.setAutoCommit(true);
        // --- UPDATE 1: Change Name (only if name is provided) ---
        if (name != null && !name.isEmpty()) {
            String sqlName = "UPDATE users SET user_name = ? WHERE user_id = ?";
            
            // Nested try-with-resources to close this specific statement
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlName)) {
                pstmt1.setString(1, name);
                pstmt1.setInt(2, userId);
                
                int row = pstmt1.executeUpdate();
                if (row > 0) {
                    user.setUserName(name); // Update the bean
                }
            }
        }

        // --- UPDATE 2: Change Phone (only if tel is provided) ---
        if (tel != null && !tel.isEmpty()) {
            String sqlTel = "UPDATE users SET phone_number = ? WHERE user_id = ?";
            
            // Nested try-with-resources for the second statement
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlTel)) {
                pstmt2.setString(1, tel);
                pstmt2.setInt(2, userId);
                
                int row = pstmt2.executeUpdate();
                if (row > 0) {
                    user.setPhoneNumber(tel); // Update the bean
                }
            }
        }

        if(password != null && !password.isEmpty()){
            String sqlPass = "UPDATE users SET password_hash = ? WHERE user_id = ?";
            
            // Nested try-with-resources for the second statement
            try (PreparedStatement pstmt3 = conn.prepareStatement(sqlPass)) {
                pstmt3.setString(1, password);
                pstmt3.setInt(2, userId);
                
                int row = pstmt3.executeUpdate();
                if (row > 0) {
                    user.setPasswordHash(password); // Update the bean
                }
            }
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Return the bean (it will contain only the fields that were actually updated)
        return user;
    }
    //Create User in users table
    public boolean registerFinalUser(UserBean user){
        String sql = "INSERT INTO users (user_name, email, phone_number, password_hash, active_flag) VALUES (?, ?, ?, ?, true)";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setString(4, user.getPasswordHash());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //Delete user from users table
    public boolean deleteUser(int userId) {
        String sql = "UPDATE users SET active_flag = false WHERE user_id = ?";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    public UserBean findUserByEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ? AND active_flag = true";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserBean user = new UserBean();
                    user.setUserId(rs.getInt("user_id"));
                    user.setEmail(email);
                    return user;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updateUserPassword(String email, String hashedPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
             e.printStackTrace(); 
        }
    }
}