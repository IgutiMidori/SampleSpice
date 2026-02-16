package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class OtpDao {

    public boolean isEmailAvailable(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean available = !rs.next(); 
                System.out.println("[DEBUG-DB] Email " + email + " availability: " + available);
                return available;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void saveOTP(Integer userId, String email, int otp) {
    // 1. Always delete by email first to avoid UNIQUE constraint violation
    String delSql = "DELETE FROM mfa_otp WHERE email = ?";
    String insSql = "INSERT INTO mfa_otp (user_id, email, otp) VALUES (?, ?, ?)";

    try (Connection conn = MySqlConnectionManager.getInstance().getConnection()) {
        conn.setAutoCommit(false); // Use transaction for safety
        
        try (PreparedStatement delP = conn.prepareStatement(delSql);
             PreparedStatement insP = conn.prepareStatement(insSql)) {
            
            // Delete old entry
            delP.setString(1, email);
            delP.executeUpdate();

            // Insert new entry
            if (userId != null && userId > 0) {
                insP.setInt(1, userId); // Existing user (Email Change)
            } else {
                insP.setNull(1, java.sql.Types.INTEGER); // New user (Registration)
            }
            insP.setString(2, email);
            insP.setInt(3, otp);
            insP.executeUpdate();
            
            conn.commit();
            System.out.println("[DEBUG-DB] OTP saved successfully for: " + email);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    } catch (SQLException e) {
        System.out.println("[DEBUG-DB] Error in saveOTP: " + e.getMessage());
        e.printStackTrace();
    }
}

    public int verifyOTPStatus(int userId, int enteredOTP) {
        String sql = "SELECT created_at FROM mfa_otp WHERE user_id = ? AND otp = ?";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, enteredOTP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    long diff = System.currentTimeMillis() - ts.getTime();
                    if (diff > 600000) return 2; // Expired
                    return 1; // Valid
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0; // Wrong
    }

    public boolean updateEmail(int userId, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    public int verifyRegisterOTP(String email, int otp) {
    String sql = "SELECT created_at FROM mfa_otp WHERE email = ? AND otp = ?";
    try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, email);
        pstmt.setInt(2, otp);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                long currentTime = System.currentTimeMillis();
                long otpTime = ts.getTime();
                
                // 30,000 milliseconds = 30 seconds
                if (currentTime - otpTime > 30000) {
                    System.out.println("[DEBUG-DB] OTP Expired. Diff: " + (currentTime - otpTime) + "ms");
                    return 2; // Expired
                }
                System.out.println("[DEBUG-DB] OTP Valid for email: " + email);
                return 1; // Valid
            }
        }
    } catch (SQLException e) { 
        System.out.println("[DEBUG-DB] SQL Error in verifyRegisterOTP: " + e.getMessage());
    }
    return 0; // Wrong
    }
    public int verifyOtp(String email, int inputOtp) {
        String sql = "SELECT created_at FROM mfa_otp WHERE email = ? AND otp = ?";
        try (Connection conn = MySqlConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setInt(2, inputOtp);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    long diff = System.currentTimeMillis() - ts.getTime();
                    
                    // Check if expired (e.g., 5 minutes = 300000ms)
                    if (diff > 300000) { 
                        return 2; // Expired
                    }
                    return 1; // Valid
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0; // Invalid (No match found)
    }
}