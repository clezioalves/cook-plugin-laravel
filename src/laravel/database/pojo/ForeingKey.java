package laravel.database.pojo;

import laravel.cook.Helper;

/**
 * Created by clezio on 22/08/16.
 */
public class ForeingKey {

    private String tableName;

    private String columnName;

    private ForeingKey manyToOne;

    public ForeingKey(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ForeingKey getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(ForeingKey manyToOne) {
        this.manyToOne = manyToOne;
    }

    public String getTableNameModelize(){
        return Helper.getInstance().modelize(this.getTableName());
    }

    public String getTableNameSingularize(){
        return Helper.getInstance().singularize(this.getTableName());
    }

    public String getTableNameCollections(){
        return Helper.getInstance().collections(this.getTableName());
    }
}
