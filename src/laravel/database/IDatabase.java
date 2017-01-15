package laravel.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by clezio on 12/08/16.
 */
public interface IDatabase {
    public void openConnection() throws ClassNotFoundException, SQLException;
    public Connection getConnection();
    public void closeConnection();
}
