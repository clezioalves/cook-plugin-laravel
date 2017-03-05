package laravel.cook;

import cook.core.FreemarkerWrapper;
import cook.core.IFCook;
import cook.core.ResultProcess;
import cook.util.FileUtil;
import cook.util.PrintUtil;
import laravel.database.DatabaseFactory;
import laravel.database.IDatabase;
import laravel.database.ResourceUtil;
import laravel.database.structure.Attribute;
import laravel.database.structure.ForeingKey;
import laravel.database.structure.ModelDesign;
import laravel.database.structure.TableDesign;
import laravel.utils.FileUtilPlugin;
import laravel.utils.PrintUtilPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clezio on 08/08/16.
 */
public class Recipe implements IFCook {

    public static final String MODEL = "model";
    public static final String CONTROLLER_REST = "controller-rest";
    public static final String CONTROLLER = "controller";
    public static final String TEMPLATE = "template";
    public static final String ARTISAN_FILE = "artisan";

    public static final String DATABASE_CONFIG_FILE = ".env";
    public static final String Q = "q";
    public static final String Y = "y";
    public static final String N = "n";
    public static final String ENTER_A_NUMBER_FROM_THE_LIST_ABOVE_OR_Q_TO_EXIT = "Enter a number from the list above, or 'q' to exit: ";
    public static final String POSSIBLE_MODELS_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV = "Possible Models based on current database defined in file \".env\"";
    public static final String POSSIBLE_CONTROLLERS_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV = "Possible Controllers based on current database defined in file \".env\"";
    public static final String POSSIBLE_TEMPLATES_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV = "Possible Templates based on current database defined in file \".env\"";
    public static final String INVALID_OPTION = "Invalid option";
    public static final String TABLE = "TABLE";
    public static final String MODEL_CREATED_SUCCESSFULLY = "Model created successfully!";
    public static final String CONTROLLER_CREATED_SUCCESSFULLY = "Controller created successfully!";
    public static final String TEMPLATES_CREATED_SUCCESSFULLY = "Templates created successfully!";
    public static final String OPERATION_CANCELED = "Operation canceled!";
    public static final String PLEASE_CONFIRM_THE_FOLLOWING_ASSOCIATIONS = "Please confirm the following associations:";
    public static final String PLEASE_SELECT_DISPLAY_FIELD = "A displayField could not be automatically detected, so would you like to choose one?";
    public static final String BELONGS_TO_LABEL = " belongsTo ";
    public static final String HAS_ONE_LABEL = " hasOne ";
    public static final String HAS_MANY_LABEL = " hasMany ";
    public static final String HAS_AND_BELONGS_TO_MANY_LABEL = " hasAndBelongsToMany ";
    public static final String NONE = "None";
    public static final String LANGUAGE_NAME_IN_TABLE = "Language name in table?";
    public static final String ESCAPE = "\\";
    public static final String THE_FILENAME = "The filename \"";
    public static final String ALREADY_EXISTS_REPLACE_THE_EXISTING_FILE_Y_N = "\" already exists. Replace the existing file? (y/n)";
    public static final String PATH_APP = "app";
    public static final String PATH_MODEL = "Models";
    public static final String PATH_CONTROLLER = "Http" + File.separator + "Controllers";
    public static final String PATH_REQUESTS = "Http" + File.separator + "Requests";
    public static final String PATH_CONFIG = "config";
    public static final String CONTROLLER_SUFIX = "Controller";
    public static final String REGEX_EXTRACT_RELATIONSHIPS = "public function (\\w+).*"+System.lineSeparator()+".*(->(\\w+)\\('(.*\\\\([\\w+]+))').*;";
    public static final String REGEX_EXTRACT_PRIMARYKEY = ".*primaryKey.*'(.*)'";
    public static final String REGEX_EXTRACT_DISPLAY_FIELD = ".*displayField.*'(.*)'";
    public static final String BELONGS_TO = "belongsTo";
    public static final String BELONGS_TO_MANY = "belongsToMany";
    public static final String HAS_ONE = "hasOne";
    public static final String HAS_MANY = "hasMany";
    public static final String NOME = "nome";
    public static final String NAME = "name";
    private static final String PATH_ROUTES = "routes";
    private static final String CREATED = "Created";
    private static final String UPDATED = "Updated";
    private static final String PATH_VENDOR = "vendor";
    private static final String PATH_LARAVEL_COLLECTIVE = "laravelcollective";
    private static final String PATH_HTML = "html";
    private static final String PATH_RESOURCES = "resources";
    private static final String PATH_VIEW = "views";
    public static final String RESOURCES_TEMPLATE_BOOTSTRAP1_ZIP = "https://github.com/clezioalves/cook-plugin-laravel/raw/master/others-resources/template-bootstrap1.zip";
    public static final String DOWNLOAD_ADDITIONAL_RESOURCE = "Downloading additional resource";
    private static final java.lang.String REGEX_EXTRACT_ITEM_MENU = "(.*)(<!--.*inject:itemMenu.*-->)";

    private IDatabase database;

    private String action;

    private String path;

    private Integer success;

    private List<String> changeHistory;

    public static void main(String args[]) throws Exception {
        Helper.getInstance().configureInflector(Inflector.PT_BR);
        Recipe r = new Recipe();
        String routesFileContent = r.readFile("C:\\dev\\xampp\\htdocs\\glaravel2\\resources\\views\\app.blade.php");
        System.out.println(routesFileContent);
//        Pattern p = Pattern.compile("(.*)(<!--inject:itemMenu-->)");
        Pattern p = Pattern.compile("(.*)(<!--.*inject:itemMenu.*-->)");
        Matcher m = p.matcher(routesFileContent);
        System.out.println(m.groupCount());
        if (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
    }

    public Recipe(){
        changeHistory = new ArrayList<String>();
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void printHeader() {
        PrintUtil.outn(PrintUtil.getGreenFont() + " _                              _ " + PrintUtil.getColorReset());
        PrintUtil.outn(PrintUtil.getGreenFont() + "| |    __ _ _ __ __ ___   _____| |" + PrintUtil.getColorReset());
        PrintUtil.outn(PrintUtil.getGreenFont() + "| |   / _` | '__/ _` \\ \\ / / _ \\ |" + PrintUtil.getColorReset());
        PrintUtil.outn(PrintUtil.getGreenFont() + "| |__| (_| | | | (_| |\\ V /  __/ |" + PrintUtil.getColorReset());
        PrintUtil.outn(PrintUtil.getGreenFont() + "|_____\\__,_|_|  \\__,_| \\_/ \\___|_|" + PrintUtil.getColorReset());
        PrintUtil.outn("");
        PrintUtil.outn("Laravel plugin generator. Version " + getVersion());
        PrintUtil.outn("");
    }

    @Override
    public void printHelp() {
        PrintUtil.outn("Use: cook laravel [action]");
        PrintUtil.outn("");
        PrintUtil.outn("Available actions:");
        PrintUtil.outn("~~~~~~~~~~~~~~~~~~");
        PrintUtil.outn(MODEL);
        PrintUtil.outn(CONTROLLER_REST);
        PrintUtil.outn(CONTROLLER);
        PrintUtil.outn(TEMPLATE);
    }

    @Override
    public boolean start(String[] param) {
        //Valid in param
        if (param.length == 1 || param[1].equals("")) {
            printHelp(); //show help
            PrintUtil.outn("");
            return false;
        }
        if(!(param[1].toLowerCase().equals(MODEL) ||
                param[1].toLowerCase().equals(CONTROLLER_REST) ||
                param[1].toLowerCase().equals(CONTROLLER) ||
                param[1].toLowerCase().equals(TEMPLATE))){
            printHelp();
            PrintUtil.outn("");
            return false;
        }

        this.action = param[1].toLowerCase();
        return true;
    }

    @Override
    public boolean validDirectory() {
        return validDirectory(FileUtil.getPromptPath());
    }

    private boolean validDirectory(String path) {
        if(!path.endsWith(File.separator)){
            path = path + File.separator;
        }
        this.path = path;
        boolean valid = true;
        //get the path of user execute script
        if (!new File(path + ARTISAN_FILE).exists()) {
            String directory = PrintUtil.inString("Enter the full path to the application: ");
            if(directory.length() > 0) {
                PrintUtil.outn("Invalid directory");
                valid = validDirectory(directory);
            }else{
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public ResultProcess cook() {
        ResultProcess resultProcess = new ResultProcess();
        ResourceUtil conf = ResourceUtil.getInstance();
        try {
            conf.loadProperties(this.path + DATABASE_CONFIG_FILE);
            this.database = new DatabaseFactory().getDataBase(conf.getDbType(), conf.getDbHost(), conf.getDbPort(), conf.getDbName(), conf.getDbUser(), conf.getDbPassword());

            if (this.action.equals(MODEL)) {
                resultProcess = this.buildModel();
            } else if (this.action.equals(CONTROLLER)) {
                resultProcess = this.buildController(Boolean.FALSE);
            } else if (this.action.equals(CONTROLLER_REST)) {
                resultProcess = this.buildController(Boolean.TRUE);
            } else if (this.action.equals(TEMPLATE)) {
                resultProcess = this.buildTemplate();
            } else {
                resultProcess.setResultProcess(ResultProcess.ERROR, "Action not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.success = resultProcess.getINFO();
        return resultProcess;
    }

    @Override
    public void end() {
        if(success.equals(1)) {
            PrintUtil.outn("");
            for(String h : changeHistory) {
                PrintUtilPlugin.printLineYellow(h);
            }
        }
    }
    public ResultProcess buildModel() {
        ResultProcess out = new ResultProcess();
        try {
            configureInflector();
            List<String> tableList = getTableList();

            PrintUtilPlugin.outn(POSSIBLE_MODELS_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV);
            //Model list
            int cont = 0;
            for(String table : tableList){
                PrintUtilPlugin.printLineYellow("[" + (cont++) + "] " + this.getModelName(table));
            }
            String option = this.inputOptions(cont);
            String tableName = tableList.get(Integer.valueOf(option));
            TableDesign tableDesign = getTableDesign(tableName);

            //
            String fileName = getFileNameModelByTableName(tableDesign.getName());
            Boolean generateFile = checkConfirmFileExists(fileName);
            if(generateFile) {
                FreemarkerWrapper.getInstance().addVar("tableDesign", tableDesign);
                String content = FreemarkerWrapper.getInstance().parseTemplate("model.ftl");
                FileUtilPlugin.saveToPath(fileName, content);
                this.updateHistory(CREATED, fileName);
                out.setResultProcess(ResultProcess.SUCESS, MODEL_CREATED_SUCCESSFULLY);
            }else{
                out.setResultProcess(ResultProcess.WARNING, OPERATION_CANCELED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.setResultProcess(ResultProcess.ERROR, "Error, " + ex.getMessage());
        }
        return out;
    }

    public ResultProcess buildController(Boolean resource) {
        ResultProcess out = new ResultProcess();
        try {
            configureInflector();
            List<String> tableList = getTableListWithModel();

            PrintUtilPlugin.outn(POSSIBLE_CONTROLLERS_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV);
            //Controllers list
            int cont = 0;
            for(String table : tableList){
                PrintUtilPlugin.printLineYellow("[" + (cont++) + "] " + this.getControllerName(table));
            }
            String option = this.inputOptions(cont);
            String tableName = tableList.get(Integer.valueOf(option));
            ModelDesign modelDesign = getModelDesign(tableName);

            String fileName = getFileNameController(tableName);
            Boolean generateFile = checkConfirmFileExists(fileName);
            if(generateFile) {
                FreemarkerWrapper.getInstance().addVar("modelDesign", modelDesign);
                String content = null;
                String formRequestNameStore = null;
                String formRequestNameUpdate = null;
                if(resource) {
                    content = FreemarkerWrapper.getInstance().parseTemplate("controller-resource.ftl");
                }else{
                    formRequestNameStore = modelDesign.getModelName() + "StoreRequest";
                    formRequestNameUpdate = modelDesign.getModelName() + "UpdateRequest";
                    FreemarkerWrapper.getInstance().addVar("formRequestNameStore", formRequestNameStore);
                    FreemarkerWrapper.getInstance().addVar("formRequestNameUpdate", formRequestNameUpdate);
                    content = FreemarkerWrapper.getInstance().parseTemplate("controller.ftl");
                }
                createResourceRoute(modelDesign, resource);
                FileUtilPlugin.saveToPath(fileName, content);
                this.updateHistory(CREATED, fileName);
                if(!resource) {
                    FreemarkerWrapper.getInstance().addVar("modelDesignName", modelDesign.getModelName());

                    //StoreForm
                    fileName = getFileNameRequest(formRequestNameStore);
                    FreemarkerWrapper.getInstance().addVar("formRequestName", formRequestNameStore);
                    FreemarkerWrapper.getInstance().addVar("rules", "$insertRules");
                    content = FreemarkerWrapper.getInstance().parseTemplate("form-request.ftl");
                    FileUtilPlugin.saveToPath(fileName, content);
                    this.updateHistory(CREATED, fileName);

                    //UpdateForm
                    fileName = getFileNameRequest(formRequestNameUpdate);
                    FreemarkerWrapper.getInstance().addVar("formRequestName", formRequestNameUpdate);
                    FreemarkerWrapper.getInstance().addVar("rules", "$updateRules");
                    content = FreemarkerWrapper.getInstance().parseTemplate("form-request.ftl");
                    FileUtilPlugin.saveToPath(fileName, content);
                    this.updateHistory(CREATED, fileName);
                }
                out.setResultProcess(ResultProcess.SUCESS, CONTROLLER_CREATED_SUCCESSFULLY);
            }else{
                out.setResultProcess(ResultProcess.WARNING, OPERATION_CANCELED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.setResultProcess(ResultProcess.ERROR, "Error, " + ex.getMessage());
        }
        return out;
    }

    public ResultProcess buildTemplate() {
        ResultProcess out = new ResultProcess();
        try {
            System.out.println(getFileNameLaravelCollective());
            Boolean fileLaravelCollectiveExists = checkFileExists(getFileNameLaravelCollective());
            if(!fileLaravelCollectiveExists){
                String message = this.getMessageComponentLaravelCollective();
                out.setResultProcess(ResultProcess.ERROR, message);
            }else {
                configureInflector();
                List<String> tableList = getTableListWithModel();

                PrintUtilPlugin.outn(POSSIBLE_TEMPLATES_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV);
                //Controllers list
                int cont = 0;
                for (String table : tableList) {
                    PrintUtilPlugin.printLineYellow("[" + (cont++) + "] " + this.getModelName(table));
                }
                String option = this.inputOptions(cont);
                String tableName = tableList.get(Integer.valueOf(option));
                ModelDesign modelDesign = getModelDesign(tableName);
                FreemarkerWrapper.getInstance().addVar("modelDesign", modelDesign);
                FreemarkerWrapper.getInstance().addVar("lang", Helper.getInstance().getLang());
                String content = null;

                List<TemplateViewEnum> templateViewEnumList = Arrays.asList(TemplateViewEnum.values());
                for(TemplateViewEnum templateViewEnum : templateViewEnumList) {
                    String fileName = getFileNameTemplate(modelDesign.getResourceName(), templateViewEnum.getValor() + ".php");
                    Boolean generateFile = checkConfirmFileExists(fileName);
                    if(generateFile) {
                        content = FreemarkerWrapper.getInstance().parseTemplate(templateViewEnum.getValor() + ".ftl");
                        FileUtilPlugin.saveToPath(fileName, content);
                        this.updateHistory(CREATED, fileName);
                    }
                }

                String fileNameAppBlade = getFileNameAppBlade();
                if(!FileUtil.fileExist(fileNameAppBlade)) {
                    PrintUtilPlugin.outn(DOWNLOAD_ADDITIONAL_RESOURCE);
                    FileUtilPlugin.importTemplate(RESOURCES_TEMPLATE_BOOTSTRAP1_ZIP, this.path);
                }

                createItemMenu(modelDesign, fileNameAppBlade);

                out.setResultProcess(ResultProcess.SUCESS, TEMPLATES_CREATED_SUCCESSFULLY);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            out.setResultProcess(ResultProcess.ERROR, "Error, " + ex.getMessage());
        }
        return out;
    }

    private String getFileNameAppBlade() {
        return this.path + PATH_RESOURCES + File.separator + PATH_VIEW + File.separator + "app.blade.php" ;
    }

    private String getFileNameTemplate(String resourceName, String template) {
        String controllerPath = this.path + PATH_RESOURCES + File.separator + PATH_VIEW + File.separator + resourceName + File.separator ;
        return controllerPath + template;
    }

    private String getMessageComponentLaravelCollective() {
        StringBuilder sb = new StringBuilder("Please install component Forms & HTML in https://laravelcollective.com or follow the below steps before to continue:");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Step 1 - Run this command:");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("\tcomposer require \"laravelcollective/html\"");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Step 2 - Open config/app.php and add this line to service providers array:");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("\tCollective\\Html\\HtmlServiceProvider::class,");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Step 3 - Next, add following line of code to aliases array.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("\t'Form' => Collective\\Html\\FormFacade::class,");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("\t'Html' => Collective\\Html\\HtmlFacade::class,");
        return sb.toString();
    }

    private void createResourceRoute(ModelDesign modelDesign, Boolean resource) throws Exception {
        String fileName = getFileNameResourceRoute(resource);
        String routesFileContent = readFile(fileName);
        Pattern p = Pattern.compile("Route::resource\\('"+modelDesign.getResourceName()+"'");
        Matcher m = p.matcher(routesFileContent);
        if (!m.find()) {
            FreemarkerWrapper.getInstance().addVar("currentContent", routesFileContent);
            FreemarkerWrapper.getInstance().addVar("modelDesign", modelDesign);
            String fileNameRoute = FreemarkerWrapper.getInstance().parseTemplate("routes-resource.ftl");
            FileUtilPlugin.saveToPath(fileName, fileNameRoute);
            updateHistory(UPDATED, fileName);
            printInfoRoutes(modelDesign);
        }
    }

    private void createItemMenu(ModelDesign modelDesign, String fileNameAppBlade) throws Exception {
        String menuContent = readFile(fileNameAppBlade);
        Pattern p = Pattern.compile(REGEX_EXTRACT_ITEM_MENU);
        Matcher m = p.matcher(menuContent);
        if (m.find()) {
            String route = "url('" + modelDesign.getResourceName() + "')";
            if(!menuContent.contains(route)) {
                String injectItemMenuSection = m.group(1) + m.group(2);
                FreemarkerWrapper.getInstance().addVar("padLeft", m.group(1));
                FreemarkerWrapper.getInstance().addVar("route", route);
                FreemarkerWrapper.getInstance().addVar("humanizeName", modelDesign.getModelNameHumanize());
                String contentItemMenu = FreemarkerWrapper.getInstance().parseTemplate("item-menu.ftl");
                menuContent = menuContent.replace(injectItemMenuSection, contentItemMenu);
                FileUtil.saveToPath(fileNameAppBlade, menuContent);
                updateHistory(UPDATED, fileNameAppBlade);
            }
        }
    }

    private void printInfoRoutes(ModelDesign modelDesign) {
        PrintUtilPlugin.printLineGreen("Creating RESTful Routes:");
        PrintUtilPlugin.printLineYellow("GET|HEAD  | api/"+modelDesign.getResourceName()+"               | "+modelDesign.getControllerName()+"@index");
        PrintUtilPlugin.printLineYellow("POST      | api/"+modelDesign.getResourceName()+"               | "+modelDesign.getControllerName()+"@store");
        PrintUtilPlugin.printLineYellow("GET|HEAD  | api/"+modelDesign.getResourceName()+"/create        | "+modelDesign.getControllerName()+"@create");
        PrintUtilPlugin.printLineYellow("GET|HEAD  | api/"+modelDesign.getResourceName()+"/{id}          | "+modelDesign.getControllerName()+"@show");
        PrintUtilPlugin.printLineYellow("PUT|PATCH | api/"+modelDesign.getResourceName()+"/{id}          | "+modelDesign.getControllerName()+"@update");
        PrintUtilPlugin.printLineYellow("DELETE    | api/"+modelDesign.getResourceName()+"/{id}          | "+modelDesign.getControllerName()+"@destroy");
        PrintUtilPlugin.printLineYellow("GET|HEAD  | api/"+modelDesign.getResourceName()+"/{id}/edit     | "+modelDesign.getControllerName()+"@edit");
    }

    private Boolean checkConfirmFileExists(String fileName) {
        Boolean generateFile = true;
        if(checkFileExists(fileName)){
            String[] simpleName = fileName.split(ESCAPE + File.separator);
            PrintUtilPlugin.printLineYellowGreenYellow(THE_FILENAME, simpleName[simpleName.length - 1], ALREADY_EXISTS_REPLACE_THE_EXISTING_FILE_Y_N);
            generateFile = this.inputConfirm(N);
        }
        return generateFile;
    }

    private boolean checkFileExists(String fileName) {
        return FileUtil.fileExist(fileName);
    }

    private void configureInflector() {
        PrintUtilPlugin.outn(LANGUAGE_NAME_IN_TABLE);
        PrintUtilPlugin.printLineYellow("[0] " + Inflector.EN);
        PrintUtilPlugin.printLineYellow("[1] " + Inflector.PT_BR);
        if(this.inputOptions(2).equals("0")){
            Helper.getInstance().configureInflector(Inflector.EN);
        }else{
            Helper.getInstance().configureInflector(Inflector.PT_BR);
        }
    }

    private TableDesign getTableDesign(String tableName) throws Exception {
        String option = null;
        TableDesign tableDesign = new TableDesign(tableName);
        tableDesign.setAttributeList(getAttributeList(tableName));
        tableDesign.setTimestamps(getTimestamps(tableDesign));
        PrintUtilPlugin.outn(PLEASE_CONFIRM_THE_FOLLOWING_ASSOCIATIONS);
        //ManyToOneList
        for (ForeingKey fk : this.getManyToOneList(tableName)) {
            PrintUtilPlugin.printLineYellowGreenYellow(this.getModelName(tableName), BELONGS_TO_LABEL, this.getModelName(fk.getTableName()) + "? (y/n)");
            if (this.inputConfirm(Y)) {
                tableDesign.getManyToOneList().add(fk);
            }
        }

        //OneToManyList
        for (ForeingKey fk : this.getOneToManyAndManyToManyList(tableName)) {
            if (fk.getManyToOne() != null) {
                PrintUtilPlugin.printLineYellowGreenYellow("[0] " + this.getModelName(tableName), HAS_AND_BELONGS_TO_MANY_LABEL, this.getModelName(fk.getManyToOne().getTableName()));
                PrintUtilPlugin.printLineYellowGreenYellow("[1] " + this.getModelName(tableName), HAS_MANY_LABEL, this.getModelName(fk.getTableName()));
                PrintUtilPlugin.printLineYellow("[2] " + NONE);
                option = this.inputOptions(3);
                if (Integer.valueOf(option) == 0) {
                    tableDesign.getManyToManyList().add(fk);
                } else if (Integer.valueOf(option) == 1) {
                    tableDesign.getOneToManyList().add(fk);
                }
            } else {
                PrintUtilPlugin.printLineYellowGreenYellow("[0] " + this.getModelName(tableName), HAS_MANY_LABEL, this.getModelName(fk.getTableName()));
                PrintUtilPlugin.printLineYellowGreenYellow("[1] " + this.getModelName(tableName), HAS_ONE_LABEL, this.getModelName(fk.getTableName()));
                PrintUtilPlugin.printLineYellow("[2] " + NONE);
                option = this.inputOptions(3);
                if (Integer.valueOf(option) == 0) {
                    tableDesign.getOneToManyList().add(fk);
                } else if (Integer.valueOf(option) == 1) {
                    tableDesign.getOneToOneList().add(fk);
                }
            }
        }
        String displayField = null;
        for(Attribute attribute : tableDesign.getAttributeList()) {
            if(attribute.getName().equalsIgnoreCase(NAME) || attribute.getName().equalsIgnoreCase(NOME)){
                displayField = attribute.getName();
                break;
            }
        }
        if(displayField == null){
            PrintUtilPlugin.outn(PLEASE_SELECT_DISPLAY_FIELD);
            int cont = 0;
            for(Attribute attribute : tableDesign.getAttributeList()) {
                PrintUtilPlugin.printLineYellow("[" + (cont++) + "] " + attribute.getName());
            }
            option = this.inputOptions(tableDesign.getAttributeList().size());
            displayField = tableDesign.getAttributeList().get(Integer.valueOf(option)).getName();
        }
        tableDesign.setDisplayField(displayField);
        return tableDesign;
    }

    private Boolean getTimestamps(TableDesign tableDesign) {
        Boolean existsCreatedAt = Boolean.FALSE;
        Boolean existsUpdatedAt = Boolean.FALSE;
        for(Attribute attribute : tableDesign.getAttributeList()){
            if(attribute.getName().equals(TableDesign.CREATED_AT)){
                existsCreatedAt = Boolean.TRUE;
            }else if(attribute.getName().equals(TableDesign.UPDATED_AT)){
                existsUpdatedAt = Boolean.TRUE;
            }
        }
        return existsCreatedAt && existsUpdatedAt;
    }

    private ModelDesign getModelDesign(String tableName) throws Exception {
        ModelDesign modelDesign = new ModelDesign(getModelName(tableName));
        modelDesign.setAttributeList(getAttributeControllerList(tableName));
        String modelContent = readFile(getFileNameModelByTableName(tableName));
        Matcher mDisplayField = Pattern.compile(REGEX_EXTRACT_DISPLAY_FIELD).matcher(modelContent);
        String displayField = "id";
        if(mDisplayField.find()){
            displayField = mDisplayField.group(1);
        }
        modelDesign.setDisplayField(displayField);

        Pattern p = Pattern.compile(REGEX_EXTRACT_RELATIONSHIPS);
        Matcher m = p.matcher(modelContent);
        while (m.find()) {
            String attributeName = m.group(1);
            String relationType = m.group(3);
            String simpleNameModel = m.group(5);

            String primaryKey = "id";
            String fileNameModelrelation = getFileNameModelByModelName(simpleNameModel);
            if(FileUtil.fileExist(fileNameModelrelation)) {
                String modelContentAssociation = readFile(fileNameModelrelation);
                Pattern p2 = Pattern.compile(REGEX_EXTRACT_PRIMARYKEY);
                Matcher m2 = p2.matcher(modelContentAssociation);
                if (m2.find()) {
                    primaryKey = m2.group(1);
                }
            }

            mDisplayField = Pattern.compile(REGEX_EXTRACT_DISPLAY_FIELD).matcher(modelContent);
            displayField = "id";
            if(mDisplayField.find()){
                displayField = mDisplayField.group(1);
            }

            Attribute attributePrimaryKey = new Attribute(primaryKey);
            attributePrimaryKey.setPrimaryKey(Boolean.TRUE);

            if(BELONGS_TO.equals(relationType)){
                modelDesign.getManyToOneList().add(new ModelDesign(simpleNameModel, attributeName, attributePrimaryKey, displayField));
            }else if(BELONGS_TO_MANY.equals(relationType)){
                modelDesign.getManyToManyList().add(new ModelDesign(simpleNameModel, attributeName, attributePrimaryKey, displayField));
            }else if(HAS_ONE.equals(relationType)){
                modelDesign.getOneToOneList().add(new ModelDesign(simpleNameModel, attributeName, attributePrimaryKey, displayField));
            }else if(HAS_MANY.equals(relationType)){
                modelDesign.getOneToManyList().add(new ModelDesign(simpleNameModel, attributeName, attributePrimaryKey, displayField));
            }else{
                continue;
            }
        }
        return modelDesign;
    }

    private String getModelName(String input) {
        return Helper.getInstance().modelize(input);
    }

    private String getControllerName(String input) {
        return Helper.getInstance().pluralize(this.getModelName(input)) + CONTROLLER_SUFIX;
    }

    private String inputOptions(int numberMaxOptions) {
        String option = null;
        Boolean isValid = null;
        do{
            option = PrintUtilPlugin.inString(ENTER_A_NUMBER_FROM_THE_LIST_ABOVE_OR_Q_TO_EXIT);
            isValid = Q.equalsIgnoreCase(option) || (isNumber(option) && Integer.valueOf(option) < numberMaxOptions);
            if(!isValid){
                PrintUtilPlugin.printLineRed(INVALID_OPTION);
            }
        }while(option.isEmpty() || !isValid);

        if(Q.equalsIgnoreCase(option)){
            System.exit(0);
        }
        return option;
    }

    private Boolean inputConfirm(String defaultOption) {
        String option = null;
        Boolean isValid = null;
        do{
            option = PrintUtilPlugin.inString("["+defaultOption+"] > ");
            if(option.isEmpty()){
                option = defaultOption;
            }
            isValid = Y.equalsIgnoreCase(option) || N.equalsIgnoreCase(option);
            if(!isValid){
                PrintUtilPlugin.printLineRed(INVALID_OPTION);
            }
        }while(option.isEmpty() || !isValid);

        if(Y.equalsIgnoreCase(option)){
            return true;
        }
        return false;
    }

    private boolean isNumber(String value) {
        return value.matches("[0-9]+");
    }

    private List<String> getTableList() throws SQLException, ClassNotFoundException {
        List<String> tableList = new ArrayList<String>();
        //Open connection
        this.database.openConnection();
        //List tables
        ResultSet rs = null;
        try {
            rs = database.getConnection().getMetaData().getTables(null, null, "%", new String[]{TABLE});
            while (rs.next()) {
                tableList.add(rs.getString(DatabaseFactory.TABLE_NAME));
            }
        }finally {
            //Close connection
            DatabaseFactory.close(rs);
            this.database.closeConnection();
        }
        return tableList;
    }

    private List<String> getTableListWithModel() throws Exception {
        List<String> tableList = new ArrayList<String>();
        List<String> tableListTmp = new ArrayList<String>();
        //Open connection
        this.database.openConnection();
        //List tables
        ResultSet rs = null;
        try {
            rs = database.getConnection().getMetaData().getTables(null, null, "%", new String[]{TABLE});
            while (rs.next()) {
                tableListTmp.add(rs.getString(DatabaseFactory.TABLE_NAME));
            }
        }finally {
            DatabaseFactory.close(rs);
            //Close connection
            this.database.closeConnection();
        }

        for(String tableName : tableListTmp){
            if(new File(getFileNameModelByTableName(tableName)).exists()) {
                tableList.add(tableName);
            }
        }

        return tableList;
    }

    private List<Attribute> getAttributeList(String tableName) throws SQLException, ClassNotFoundException {
        Attribute primaryKey = getPrimaryKey(tableName);
        List<Attribute> attributeList = new ArrayList<Attribute>();
        //Open connection
        this.database.openConnection();
        //List attributes
        ResultSet rs = null;
        try {
            rs = database.getConnection().getMetaData().getColumns(null, null, tableName, "%");
            while (rs.next()) {
                Attribute attribute = new Attribute(
                        rs.getString(DatabaseFactory.COLUMN_NAME), rs.getString(DatabaseFactory.TYPE_NAME),
                        rs.getInt(DatabaseFactory.COLUMN_SIZE), rs.getInt(DatabaseFactory.NULLABLE) == 0
                );
                attribute.setPrimaryKey(attribute.equals(primaryKey));
                attributeList.add(attribute);
            }
        }finally {
            DatabaseFactory.close(rs);
            //Close connection
            this.database.closeConnection();
        }
        return attributeList;
    }

    private List<Attribute> getAttributeControllerList(String tableName) throws SQLException, ClassNotFoundException {
        List<Attribute>  attributeList = getAttributeList(tableName);
        Set<Attribute> attributeListRemove = new HashSet();

        for (ForeingKey fk : this.getManyToOneList(tableName)) {
            for(Attribute attribute : attributeList){
                if(fk.getColumnName().equals(attribute.getName())){
                    attributeListRemove.add(attribute);
                }
            }
        }
        for (ForeingKey fk : this.getOneToManyAndManyToManyList(tableName)){
            for(Attribute attribute : attributeList){
                if(fk.getColumnName().equals(attribute.getName())){
                    attributeListRemove.add(attribute);
                }
            }
        }
        for(Attribute attribute : attributeList){
            if(TableDesign.CREATED_AT.equals(attribute.getName()) ||
                    TableDesign.UPDATED_AT.equals(attribute.getName())){
                attributeListRemove.add(attribute);
            }
        }
        attributeList.removeAll(attributeListRemove);
        return attributeList;
    }

    private Attribute getPrimaryKey(String tableName) throws SQLException, ClassNotFoundException {
        Attribute primaryKey = null;
        //Open connection
        this.database.openConnection();
        //List attributes
        ResultSet rs = null;
        try {
            rs = database.getConnection().getMetaData().getPrimaryKeys(database.getConnection().getCatalog(), null, tableName);
            if (rs.next()) {
                primaryKey = new Attribute(rs.getString(DatabaseFactory.COLUMN_NAME));
            }
        }finally {
            DatabaseFactory.close(rs);
            //Close connection
            this.database.closeConnection();
        }
        return primaryKey;
    }

    private List<ForeingKey> getManyToOneList(String tableName) throws SQLException, ClassNotFoundException {
        List<ForeingKey> manyToOneList = new ArrayList<ForeingKey>();
        //Open connection
        this.database.openConnection();
        ResultSet rs = null;
        try {
            rs = database.getConnection().getMetaData().getImportedKeys(database.getConnection().getCatalog(), null, tableName);
            while (rs.next()) {
                manyToOneList.add(new ForeingKey(
                        rs.getString(DatabaseFactory.PKTABLE_NAME), rs.getString(DatabaseFactory.FKCOLUMN_NAME),rs.getString(DatabaseFactory.PKCOLUMN_NAME))
                );
            }
        }finally {
            DatabaseFactory.close(rs);
            //Close connection
            this.database.closeConnection();
        }
        return manyToOneList;
    }


    private List<ForeingKey> getOneToManyAndManyToManyList(String tableName) throws SQLException, ClassNotFoundException {
        List<ForeingKey> oneToManyList = new ArrayList<ForeingKey>();
        //Open connection
        this.database.openConnection();
        try {
            //OneToMany
            ResultSet rs = null;
            try{
                rs = database.getConnection().getMetaData().getExportedKeys(database.getConnection().getCatalog(), null, tableName);
                while (rs.next()) {
                    oneToManyList.add(
                            new ForeingKey(
                                    rs.getString(DatabaseFactory.FKTABLE_NAME),
                                    rs.getString(DatabaseFactory.FKCOLUMN_NAME),
                                    rs.getString(DatabaseFactory.PKCOLUMN_NAME)));
                }
            }finally {
                DatabaseFactory.close(rs);
            }
            //ManyToOne
            for(ForeingKey fk : oneToManyList) {
                rs = database.getConnection().getMetaData().getImportedKeys(database.getConnection().getCatalog(), null, fk.getTableName());
                try {
                    while (rs.next()) {
                        if(rs.getString(DatabaseFactory.PKTABLE_NAME).equals(tableName)){
                            continue;
                        }
                        fk.setManyToOne(
                                new ForeingKey(
                                        rs.getString(DatabaseFactory.PKTABLE_NAME),
                                        rs.getString(DatabaseFactory.FKCOLUMN_NAME),
                                        rs.getString(DatabaseFactory.PKCOLUMN_NAME),
                                        rs.getString(DatabaseFactory.FKTABLE_NAME)
                                )
                        );
                    }
                } finally {
                    DatabaseFactory.close(rs);
                }
            }
        }finally {
            //Close connection
            this.database.closeConnection();
        }
        return oneToManyList;
    }

    private String readFile(String filename) throws Exception{
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    private String getFileNameModelByTableName(String name) {
        String modelPath = this.path + PATH_APP + File.separator + PATH_MODEL + File.separator;
        return modelPath + this.getModelName(name) + ".php";
    }

    private String getFileNameModelByModelName(String name) {
        String modelPath = this.path + PATH_APP + File.separator + PATH_MODEL + File.separator;
        return modelPath + name + ".php";
    }

    private String getFileNameController(String name) {
        String controllerPath = this.path + PATH_APP + File.separator + PATH_CONTROLLER + File.separator;
        return controllerPath + this.getControllerName(name) + ".php";
    }

    private String getFileNameRequest(String name) {
        String controllerPath = this.path + PATH_APP + File.separator + PATH_REQUESTS + File.separator;
        return controllerPath + name + ".php";
    }

    public String getFileNameResourceRoute(Boolean resource) {
        String routesPath = this.path + PATH_ROUTES + File.separator;
        if(resource) {
            return routesPath + "api.php";
        } else {
            return routesPath + "web.php";
        }
    }

    public String getFileNameLaravelCollective() {
        return this.path + PATH_VENDOR + File.separator + PATH_LARAVEL_COLLECTIVE + File.separator + PATH_HTML + File.separator + "composer.json";
    }

    public String getFileNameAppConfig() {
        return this.path + PATH_CONFIG + File.separator + "app.php";
    }

    private void updateHistory(String action, String file) {
        changeHistory.add(action + " " + file);
    }
}
