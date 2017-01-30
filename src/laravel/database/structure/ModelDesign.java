package laravel.database.structure;

import laravel.cook.Helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by clezio on 17/01/2017.
 */
public class ModelDesign {

    private String modelName;

    private String columnName;

    private List<Attribute> attributeList;

    private List<ModelDesign> manyToOneList;

    private List<ModelDesign> oneToManyList;

    private List<ModelDesign> oneToOneList;

    private List<ModelDesign> manyToManyList;

    public ModelDesign(String modelName, String columnName) {
        this.modelName = modelName;
        this.columnName = columnName;
        this.attributeList = new ArrayList();
        this.manyToOneList = new ArrayList();
        this.oneToManyList = new ArrayList();
        this.oneToOneList = new ArrayList();
        this.manyToManyList = new ArrayList();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<ModelDesign> getManyToOneList() {
        return manyToOneList;
    }

    public void setManyToOneList(List<ModelDesign> manyToOneList) {
        this.manyToOneList = manyToOneList;
    }

    public List<ModelDesign> getOneToManyList() {
        return oneToManyList;
    }

    public void setOneToManyList(List<ModelDesign> oneToManyList) {
        this.oneToManyList = oneToManyList;
    }

    public List<ModelDesign> getOneToOneList() {
        return oneToOneList;
    }

    public void setOneToOneList(List<ModelDesign> oneToOneList) {
        this.oneToOneList = oneToOneList;
    }

    public List<ModelDesign> getManyToManyList() {
        return manyToManyList;
    }

    public void setManyToManyList(List<ModelDesign> manyToManyList) {
        this.manyToManyList = manyToManyList;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getControllerName(){
        return Helper.getInstance().pluralize(this.getModelName())+"Controller";
    }

    public String getModelNameVariable(){
        String modelName = this.getModelName();
        modelName = modelName.substring(0,1).toLowerCase() + "" + modelName.substring(1);
        modelName = modelName.replaceAll("([A-Z]+)","\\_$1").toLowerCase();
        return modelName;
    }

    public String getResourceName() {
        return Helper.getInstance().pluralize(this.getModelNameVariable().replaceAll("(_)","\\-").toLowerCase());
    }

    public String getModelNameVariableList(){
        String modelName = this.getModelName();
        modelName = Helper.getInstance().pluralize(modelName.substring(0,1).toLowerCase() + "" + modelName.substring(1));
        return modelName;
    }

    public Attribute getPrimaryKey(){
        for(Attribute attribute : attributeList){
            if(attribute.getPrimaryKey()){
                return attribute;
            }
        }
        return null;
    }

    public Set<String> getListaModelImports(){
        Set<String> importList = new HashSet<String>();

        for(ModelDesign md : this.getManyToOneList()){
            importList.add(md.getModelName());
        }
        for(ModelDesign md : this.getOneToManyList()){
            importList.add(md.getModelName());
        }
        for(ModelDesign md : this.getOneToOneList()){
            importList.add(md.getModelName());
        }
        for(ModelDesign md : this.getManyToManyList()){
            importList.add(md.getModelName());
        }
        return importList;
    }
}
