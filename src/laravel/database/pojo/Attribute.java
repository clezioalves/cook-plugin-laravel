package laravel.database.pojo;

/**
 * Created by clezio on 19/08/16.
 */
public class Attribute {

    public static final String VARCHAR = "varchar";

    private String name;

    private String type;

    private Integer maxLenght;

    private Boolean required;

    private Boolean primaryKey;

    private ForeingKey foreingKey;

    private Boolean displayField;

    public Attribute(String name, String type, Integer maxLenght, Boolean required) {
        this.name = name;
        this.type = type;
        this.maxLenght = maxLenght;
        this.required = required;
        this.primaryKey = Boolean.FALSE;
    }

    public Attribute(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght(Integer maxLenght) {
        this.maxLenght = maxLenght;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public ForeingKey getForeingKey() {
        return foreingKey;
    }

    public void setForeingKey(ForeingKey foreingKey) {
        this.foreingKey = foreingKey;
    }

    public Boolean getDisplayField() {
        return displayField;
    }

    public void setDisplayField(Boolean displayField) {
        this.displayField = displayField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        return name != null ? name.equals(attribute.name) : attribute.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
