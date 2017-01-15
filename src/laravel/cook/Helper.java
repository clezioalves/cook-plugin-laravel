package laravel.cook;

/**
 * Created by Clézio on 14/09/2016.
 */
public class Helper {

    private static Helper instance = null;

    private Inflector inflector;

    private Helper(){}

    public static Helper getInstance() {
        if(instance == null){
            instance = new Helper();
        }
        return instance;
    }

    public void configureInflector(String lang){
        this.inflector = new Inflector(lang);
    }

    public String modelize(String input) {
        return this.inflector.singularize(this.inflector.normalize(input));
    }

    public String singularize(String input) {
        return this.inflector.singularize(input);
    }

    public String humanize(String input) {
        return this.inflector.humanize(input);
    }

    public String plural(String input) {
        return this.inflector.pluralize(input);
    }

    public String collections(String input) {
        String text = this.inflector.pluralize(this.inflector.normalize(this.singularize(input)));
        return text.substring(0,1).toLowerCase() + text.substring(1);
    }


}
