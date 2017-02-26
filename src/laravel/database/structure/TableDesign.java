package laravel.database.structure;

import laravel.cook.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clezio on 19/08/16.
 */
public class TableDesign {

    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    private String name;

    private Boolean timestamps;

    private List<Attribute> attributeList;

    private List<ForeingKey> manyToOneList;

    private List<ForeingKey> oneToManyList;

    private List<ForeingKey> oneToOneList;

    private List<ForeingKey> manyToManyList;

    public TableDesign(String name) {
        this.name = name;
        this.attributeList = new ArrayList();
        this.manyToOneList = new ArrayList();
        this.oneToManyList = new ArrayList();
        this.oneToOneList = new ArrayList();
        this.manyToManyList = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<ForeingKey> getManyToOneList() {
        return manyToOneList;
    }

    public void setManyToOneList(List<ForeingKey> manyToOneList) {
        this.manyToOneList = manyToOneList;
    }

    public List<ForeingKey> getOneToManyList() {
        return oneToManyList;
    }

    public void setOneToManyList(List<ForeingKey> oneToManyList) {
        this.oneToManyList = oneToManyList;
    }

    public List<ForeingKey> getManyToManyList() {
        return manyToManyList;
    }

    public void setManyToManyList(List<ForeingKey> manyToManyList) {
        this.manyToManyList = manyToManyList;
    }

    public List<ForeingKey> getOneToOneList() {
        return oneToOneList;
    }

    public void setOneToOneList(List<ForeingKey> oneToOneList) {
        this.oneToOneList = oneToOneList;
    }

    public Boolean getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Boolean timestamps) {
        this.timestamps = timestamps;
    }

    public Attribute getPrimaryKey(){
        for(Attribute attribute: this.getAttributeList()){
            if(attribute.getPrimaryKey()){
                return attribute;
            }
        }
        return null;
    }

    public String getNameModelize(){
        return Helper.getInstance().modelize(this.getName());
    }

    public String getNameSingularize(){
        return Helper.getInstance().singularize(this.getName());
    }

    public List<RuleAttributeName> getRuleAttributeNameList() {
        List<RuleAttributeName> attributeNameList = new ArrayList<RuleAttributeName>();
        for(Attribute attribute : this.getAttributeList()){
            if(attribute.getName().equals(CREATED_AT) || attribute.getName().equals(UPDATED_AT)){
                continue;
            }
            StringBuilder rules = new StringBuilder();
            String attributeName = null;
            ForeingKey foreingKey = this.getForeingKeyByNameColumn(attribute.getName());
            if(attribute.getRequired() && !attribute.getPrimaryKey()){
                attributeName = attribute.getName();
                if(foreingKey != null){
                    attributeName = foreingKey.getTableNameVariable();
                }
                rules.append("required|");
            }
            if(attribute.getType().equalsIgnoreCase(Attribute.VARCHAR)){
                rules.append("max:"+attribute.getMaxLenght()+"|");
            }

            if(attributeName != null && rules.length() > 0){
                attributeNameList.add(new RuleAttributeName(attributeName, rules.substring(0,rules.length() - 1).toString()));
            }

        }
        return attributeNameList;
    }

    private ForeingKey getForeingKeyByNameColumn(String columnName){
        for(ForeingKey foreingKey : this.getManyToOneList()){
            if(columnName.equals(foreingKey.getColumnName())){
                return foreingKey;
            }
        }
        for(ForeingKey foreingKey : this.getOneToOneList()){
            if(columnName.equals(foreingKey.getColumnName())){
                return foreingKey;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableDesign that = (TableDesign) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TableDesign{" +
                "name='" + name + '\'' +
                '}';
    }
}
