package laravel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by clezio on 12/08/16.
 */
public class DatabaseSqlserver implements IDatabase {

    public static final String DRIVER = "net.sourceforge.jtds.jdbc.Driver";

    private String url;

    private Connection con;

    public DatabaseSqlserver(String host, Integer port, String dbName, String user, String password) {
        this.url = String.format("jdbc:microsoft:sqlserver://%s:%d;DatabaseName=%s;user=%s;password=%s", host, port, dbName, user, password);
    }

    @Override
    public void openConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        con = DriverManager.getConnection(url);
    }

    @Override
    public Connection getConnection() {
        return con;
    }

    @Override
    public void closeConnection() {
        try {
            if (!con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            ;
        }
    }
}
