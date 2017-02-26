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
        return this.inflector.normalize(this.singularize(input));
    }

    public String singularize(String input) {
        String strInputSplit[] = input.split("_");
        boolean underscore = true;
        if(strInputSplit.length == 1){
            strInputSplit = input.split("(?=[A-Z])");
            underscore = false;
        }
        StringBuilder sb = new StringBuilder();
        for(int x = 0; x < strInputSplit.length - 1; x++){
            sb.append(strInputSplit[x] + (underscore ? "_" : ""));
        }
        sb.append(this.inflector.singularize(strInputSplit[strInputSplit.length - 1]));
        return sb.toString();
    }

    public String humanize(String input) {
        return this.inflector.humanize(input);
    }

    public String pluralize(String input) {
        String strInputSplit[] = input.split("_");
        boolean underscore = true;
        if(strInputSplit.length == 1){
            strInputSplit = input.split("(?=[A-Z])");
            underscore = false;
        }
        StringBuilder sb = new StringBuilder();
        for(int x = 0; x < strInputSplit.length - 1; x++){
            sb.append(strInputSplit[x] + (underscore ? "_" : ""));
        }
        sb.append(this.inflector.pluralize(strInputSplit[strInputSplit.length - 1]));
        return sb.toString();
    }

    public String collections(String input) {
        String text = this.inflector.normalize(input);
        return text.substring(0,1).toLowerCase() + text.substring(1);
    }


}
