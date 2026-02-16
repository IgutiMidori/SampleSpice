package jp.ac.ttc.webapp.connector;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlConnectionManager {
    private static MySqlConnectionManager mysqlConnection = null;
    private Connection connection = null;
    private MySqlConnectionManager() {}

    public static MySqlConnectionManager getInstance() {
        if(mysqlConnection == null) {
            mysqlConnection = new MySqlConnectionManager();
        }
        return mysqlConnection;
    }

    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                String env = System.getenv("APP_ENV");
                if(env == null) {
                    env = "dev";
                }

                String propName = "db_" + env + ".properties";
                InputStream is = MySqlConnectionManager.class.getClassLoader().getResourceAsStream(propName);
                Properties prop = new Properties();
                prop.load(is);

                if(is == null) {
                    throw new Exception("property file '" + propName + "' not found in the classpath");
                }

                String url = prop.getProperty("url");
                String user = prop.getProperty("user");
                String password = prop.getProperty("password");

                System.out.println("DB LOGIN" + user);
            
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(false);   
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void beginTransaction() {
        try{
            if(connection != null && !connection.isClosed()) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
