package jp.ac.ttc.webapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ttc.webapp.bean.SpiceBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;

public class SpiceDao {
    public int getSpicesCount() {
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        int count = 0;
        try {

            String sql = "SELECT COUNT(*) AS count FROM spices";

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


    public List<SpiceBean> getSpicesIdAndNames() {
        // Implementation for retrieving spices list
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        List<SpiceBean> spicesList = new ArrayList<>();
        try {

            String sql = "SELECT spice_id, spice_name_jp, spice_name_en FROM spices";

            pstmt = conn.prepareStatement(sql); 
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SpiceBean spice = new SpiceBean();
                spice.setSpiceId(rs.getInt("spice_id"));
                spice.setSpiceNameEn(rs.getString("spice_name_en"));
                spice.setSpiceNameJp(rs.getString("spice_name_jp"));
                spicesList.add(spice);
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
        return spicesList;
    }

    // 全スパイス情報取得
    public List<SpiceBean> getSpices(int limit, int offset) {
        // Implementation for retrieving spices list
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;

        List<SpiceBean> spicesList = new ArrayList<>();
        try {
            String sql = """
                        SELECT spice_id, spice_name_jp, 
                        spice_name_en,image_url  FROM spices
                        ORDER BY spice_id LIMIT ? OFFSET ?
                        """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int spiceId = rs.getInt("spice_id");
                String spiceNameJp = rs.getString("spice_name_jp");
                String spiceNameEn = rs.getString("spice_name_en");
                String imageUrl = rs.getString("image_url");

                SpiceBean spice = new SpiceBean();
                spice.setSpiceId(spiceId);
                spice.setSpiceNameJp(spiceNameJp);
                spice.setSpiceNameEn(spiceNameEn);
                spice.setImageUrl(imageUrl);
                spicesList.add(spice);
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
        return spicesList;
    }

    // spiceIdからスパイス情報取得(１件)
    public SpiceBean getSpiceDetail(int spiceId){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        SpiceBean spiceBean = new SpiceBean();
        try{
            String sql = "SELECT * FROM spices WHERE spice_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, spiceId);
            ResultSet rs = pstmt.executeQuery();    

            // 一応ifでチェック
            if(rs.next()){
                spiceBean.setSpiceId(rs.getInt("spice_id"));
                spiceBean.setSpiceNameJp(rs.getString("spice_name_jp"));
                spiceBean.setSpiceNameEn(rs.getString("spice_name_en"));
                spiceBean.setImageUrl(rs.getString("image_url"));
                spiceBean.setOriginCountry(rs.getString("origin_country"));
                spiceBean.setPriceRange(rs.getString("price_range"));
                spiceBean.setEffect(rs.getString("effect"));
                spiceBean.setExampleDishes(rs.getString("example_dishes"));
                spiceBean.setOverview(rs.getString("overview"));
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
        return spiceBean;
    }

    // スパイス検索結果取得
    public List<SpiceBean> getSpicesBySpiceName(String spiceName){
        Connection conn = MySqlConnectionManager.getInstance().getConnection();
        PreparedStatement pstmt = null;
        List<SpiceBean> spicesList = new ArrayList<>(); 
        try{
            // 日本語名と英語名で検索
            String sql = "SELECT spice_id, spice_name_jp, spice_name_en,image_url  FROM spices WHERE spice_name_jp LIKE ? OR spice_name_en LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%"+spiceName+"%");
            pstmt.setString(2, "%"+spiceName+"%");
            ResultSet rs = pstmt.executeQuery();    
            
            while (rs.next()) {
                int spiceId = rs.getInt("spice_id");
                String spiceNameJp = rs.getString("spice_name_jp");
                String spiceNameEn = rs.getString("spice_name_en");
                String imageUrl = rs.getString("image_url");

                SpiceBean spice = new SpiceBean();
                spice.setSpiceId(spiceId);
                spice.setSpiceNameJp(spiceNameJp);
                spice.setSpiceNameEn(spiceNameEn);
                spice.setImageUrl(imageUrl);       
                spicesList.add(spice);
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
        return spicesList;
    }
}