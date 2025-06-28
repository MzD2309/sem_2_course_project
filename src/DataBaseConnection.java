import java.sql.*;

public class DataBaseConnection {
    private static final String address = "jdbc:mysql://localhost:3306/electives?useSSL=false&serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(address, user, password);
    }


}