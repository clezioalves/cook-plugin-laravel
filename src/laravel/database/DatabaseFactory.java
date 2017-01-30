package laravel.database;

import java.sql.ResultSet;

/**
 * Created by clezio on 12/08/16.
 */
public class DatabaseFactory {

    public static final String TABLE_NAME = "TABLE_NAME";

    public static final String TYPE_NAME = "TYPE_NAME";

    public static final String COLUMN_NAME = "COLUMN_NAME";

    public static final String COLUMN_SIZE = "COLUMN_SIZE";

    public static final String NULLABLE = "NULLABLE";

    public static final String PKTABLE_NAME = "PKTABLE_NAME";

    public static final String FKTABLE_NAME = "FKTABLE_NAME";

    public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";

    public final static String POSTGRES = "pgsql";

    public final static String MYSQL = "mysql";

    public final static String SQL_SERVER = "sqlsrv";

    public final static String FIREBIRD = "firebird";

    public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";

    public IDatabase getDataBase(String type, String host, int port, String dbName, String user, String password){
        switch (type){
            case POSTGRES: return new DatabasePostgres(host, port, dbName, user, password);
            case MYSQL: return new DatabaseMysql(host, port, dbName, user, password);
            case SQL_SERVER: return new DatabaseSqlserver(host, port, dbName, user, password);
            case FIREBIRD: return new DatabaseFirebird(host, port, dbName, user, password);
            default: return null;
        }
    }

    public static void close(ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            }catch(Exception e){
                ;
            }
        }
    }
}
