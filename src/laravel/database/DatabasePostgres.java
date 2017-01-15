package laravel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by clezio on 12/08/16.
 */
public class DatabasePostgres implements IDatabase {

    public static final String DRIVER = "org.postgresql.Driver";

    private String url;

    private Connection con;

    public DatabasePostgres(String host, Integer port, String dbName, String user, String password) {
        this.url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", host, port, dbName, user, password);
        //postgres://hlcjutxtvoqgjw:BcDc6toO9F1P_JR496gYYZ8Pm8@ec2-54-83-56-177.compute-1.amazonaws.com:5432/d6o1u23j3sqfkc
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
