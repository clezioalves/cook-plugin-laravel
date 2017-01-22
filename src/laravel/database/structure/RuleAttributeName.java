package laravel.database.structure;

/**
 * Created by Clézio on 15/01/2017.
 */
public class RuleAttributeName {
    private String attributeName;
    private String rules;

    public RuleAttributeName(String attributeName, String rules) {
        this.attributeName = attributeName;
        this.rules = rules;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
