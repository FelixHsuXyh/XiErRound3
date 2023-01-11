import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static String driver = null;
    private static String url = null;
    private static String username = null;
    private static String password = null;
    static {
        try {
            InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("studentdb.properties");
            Properties properties = new Properties();
            properties.load(in);
            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            Class.forName(driver);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
    public static void releaseSource(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
        if(resultSet!=null) {
            resultSet.close();
        }
        if(statement!=null) {
            statement.close();
        }
        if(connection!=null) {
            connection.close();
        }
    }
}
