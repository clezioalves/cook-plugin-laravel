/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laravel.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by clezio on 15/08/16.
 */
public class ResourceUtil{

    private String dbHost;

    private String dbName;

    private String dbUser;

    private String dbPassword;

    private Integer dbPort;

    private String dbType;

    private static ResourceUtil instance = null;

    private Properties bundle;

    private ResourceUtil() {
        bundle = new Properties();
    }

    public static ResourceUtil getInstance(){
        if(instance == null){
            instance = new ResourceUtil();
        }
        return instance;
    }

    public void loadProperties(String fullPath) throws IOException {
        InputStream is = null;
        try{
            is = new java.io.FileInputStream(fullPath);
            bundle.load(is);
            this.dbType = this.bundle.getProperty("DB_CONNECTION");
            this.dbHost = this.bundle.getProperty("DB_HOST");
            this.dbName = this.bundle.getProperty("DB_DATABASE");
            this.dbPort = Integer.valueOf(this.bundle.getProperty("DB_PORT"));
            this.dbUser = this.bundle.getProperty("DB_USERNAME");
            this.dbPassword = this.bundle.getProperty("DB_PASSWORD");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignore) {
                    ;
                }
            }
        }
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public String getDbType() {
        return dbType;
    }
}
