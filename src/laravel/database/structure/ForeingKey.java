package laravel.database.structure;

import laravel.cook.Helper;

/**
 * Created by clezio on 22/08/16.
 */
public class ForeingKey {

    private String tableName;

    private String columnName;

    private String pkColumnName;

    private ForeingKey manyToOne;

    private String fkTableName;

    private String fkColumnName;

    public ForeingKey(String tableName, String columnName, String pkColumnName) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.pkColumnName = pkColumnName;
    }

    public ForeingKey(String tableName, String columnName, String pkColumnName, String fkTableName) {
        this(tableName, columnName, pkColumnName);
        this.fkTableName = fkTableName;
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

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
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

    public String getFkTableName() {
        return fkTableName;
    }

    public void setFkTableName(String fkTableName) {
        this.fkTableName = fkTableName;
    }

    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    public String getColumnNameVariable(){
        String modelName = this.getColumnName();
        modelName = Helper.getInstance().modelize(modelName);
        modelName = modelName.substring(0,1).toLowerCase() + "" + modelName.substring(1);
        return modelName;
    }

    public String getTableNameVariable(){
        String modelName = this.getTableName();
        modelName = Helper.getInstance().modelize(modelName);
        modelName = modelName.substring(0,1).toLowerCase() + "" + modelName.substring(1);
        return modelName;
    }

    public String getTableNameCollections(){
        return Helper.getInstance().collections(this.getTableName());
    }
}
