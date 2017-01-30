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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clezio on 08/08/16.
 */
public class Recipe implements IFCook {

    public static final String MODEL = "model";
    public static final String CONTROLLER_RESOURCE = "controller-resource";
    public static final String ARTISAN_FILE = "artisan";

    public static final String DATABASE_CONFIG_FILE = ".env";
    public static final String Q = "q";
    public static final String Y = "y";
    public static final String N = "n";
    public static final String ENTER_A_NUMBER_FROM_THE_LIST_ABOVE_OR_Q_TO_EXIT = "Enter a number from the list above, or 'q' to exit: ";
    public static final String POSSIBLE_MODELS_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV = "Possible Models based on current database defined in file \".env\"";
    public static final String POSSIBLE_CONTROLLERS_BASED_ON_CURRENT_DATABASE_DEFINED_IN_FILE_ENV = "Possible Controllers based on current database defined in file \".env\"";
    public static final String INVALID_OPTION = "Invalid option";
    public static final String TABLE = "TABLE";
    public static final String MODEL_CREATED_SUCCESSFULLY = "Model created successfully!";
    public static final String CONTROLLER_CREATED_SUCCESSFULLY = "Controller created successfully!";
    public static final String OPERATION_CANCELED = "Operation canceled!";
    public static final String PLEASE_CONFIRM_THE_FOLLOWING_ASSOCIATIONS = "Please confirm the following associations:";
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
    public static final String CONTROLLER = "Controller";
    //public static final String REGEX_EXTRACT_RELATIONSHIPS = ".*->(\\w+)\\('(.*\\\\([^\\.]+))'\\);";
    public static final String REGEX_EXTRACT_RELATIONSHIPS = "public function (\\w+).*"+System.lineSeparator()+".*(->(\\w+)\\('(.*\\\\([\\w+]+))').*;";
    public static final String BELONGS_TO = "belongsTo";
    public static final String BELONGS_TO_MANY = "belongsToMany";
    public static final String HAS_ONE = "hasOne";
    public static final String HAS_MANY = "hasMany";
    private static final String PATH_ROUTES = "routes";
    private static final String CREATED = "Created";
    private static final String UPDATED = "Updated";

    private IDatabase database;

    private String action;

    private String path;

    private Integer success;

    private List<String> changeHistory;

    /*public static void main(String args[]) throws Exception {
        Helper.getInstance().configureInflector(Inflector.PT_BR);
        Recipe r = new Recipe();
        String modelContent = r.readFile("C:\\dev\\laravel_projects\\sportal\\app\\Models\\Setor.php");
        Pattern p = Pattern.compile(REGEX_EXTRACT_RELATIONSHIPS);
        Matcher m = p.matcher(modelContent);
        System.out.println("m: " + m.groupCount());
        while (m.find()) {
            String attributeName = m.group(1);
            String relationType = m.group(3);
            String simpleNameModel = m.group(5);
            System.out.println("simpleNameModel: "+simpleNameModel+" relationType: "+relationType+" attributeName: "+attributeName);
        }
    }*/

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
        PrintUtil.outn(CONTROLLER_RESOURCE);
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
                param[1].toLowerCase().equals(CONTROLLER_RESOURCE))){
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
            }else if (this.action.equals(CONTROLLER_RESOURCE)) {
                resultProcess = this.buildController(Boolean.TRUE);
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
            String fileName = getFileNameModel(tableDesign.getName());
            Boolean generateFile = checkFileExists(fileName);
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
            Boolean generateFile = checkFileExists(fileName);
            if(generateFile) {
                FreemarkerWrapper.getInstance().addVar("modelDesign", modelDesign);
                String content = null;
                if(resource) {
                    content = FreemarkerWrapper.getInstance().parseTemplate("controller-resource.ftl");
                    createResourceRoute(modelDesign);
                }
                FileUtilPlugin.saveToPath(fileName, content);
                this.updateHistory(CREATED, fileName);
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

    private void createResourceRoute(ModelDesign modelDesign) throws Exception {
        String fileName = getFileNameResourceRoute();
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

    private void printInfoRoutes(ModelDesign modelDesign) {
        PrintUtilPlugin.printLineGreen("Creating RESTful Routes:");
        PrintUtilPlugin.printLineYellow("GET|HEAD  | api/"+modelDesign.getResourceName()+"               | "+modelDesign.getControllerName()+"@index");
        PrintUtilPlugin.printLineYellow("POST      | api/"+modelDesign.getResourceName()+"               | "+modelDesign.getControllerName()+"@store");
        PrintUtilPlugin.printLineYellow("GET|HEAD  | api/"+modelDesign.getResourceName()+"/{id}          | "+modelDesign.getControllerName()+"@show");
        PrintUtilPlugin.printLineYellow("PUT|PATCH | api/"+modelDesign.getResourceName()+"/{id}          | "+modelDesign.getControllerName()+"@update");
        PrintUtilPlugin.printLineYellow("DELETE    | api/"+modelDesign.getResourceName()+"/{id}          | "+modelDesign.getControllerName()+"@destroy");
    }

    private Boolean checkFileExists(String fileName) {
        Boolean generateFile = true;
        if(new File(fileName).exists()){
            String[] simpleName = fileName.split(ESCAPE + File.separator);
            PrintUtilPlugin.printLineYellowGreenYellow(THE_FILENAME, simpleName[simpleName.length-1], ALREADY_EXISTS_REPLACE_THE_EXISTING_FILE_Y_N);
            generateFile = this.inputConfirm(N);
        }
        return generateFile;
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
        ModelDesign modelDesign = new ModelDesign(getModelName(tableName),"Teste");
        modelDesign.setAttributeList(getAttributeControllerList(tableName));
        String modelContent = readFile(getFileNameModel(tableName));
        Pattern p = Pattern.compile(REGEX_EXTRACT_RELATIONSHIPS);
        Matcher m = p.matcher(modelContent);
        while (m.find()) {
            String attributeName = m.group(1);
            String relationType = m.group(3);
            String simpleNameModel = m.group(5);
            if(BELONGS_TO.equals(relationType)){
                modelDesign.getOneToManyList().add(new ModelDesign(simpleNameModel, attributeName));
            }else if(BELONGS_TO_MANY.equals(relationType)){
                modelDesign.getManyToManyList().add(new ModelDesign(simpleNameModel, attributeName));
            }else if(HAS_ONE.equals(relationType)){
                modelDesign.getOneToOneList().add(new ModelDesign(simpleNameModel, attributeName));
            }else if(HAS_MANY.equals(relationType)){
                modelDesign.getManyToOneList().add(new ModelDesign(simpleNameModel, attributeName));
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
        return Helper.getInstance().pluralize(this.getModelName(input)) + CONTROLLER;
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
            if(new File(getFileNameModel(tableName)).exists()) {
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

    private String getFileNameModel(String name) {
        String modelPath = this.path + PATH_APP + File.separator + PATH_MODEL + File.separator;
        return modelPath + this.getModelName(name) + ".php";
    }

    private String getFileNameController(String name) {
        String controllerPath = this.path + PATH_APP + File.separator + PATH_CONTROLLER + File.separator;
        return controllerPath + this.getControllerName(name) + ".php";
    }

    public String getFileNameResourceRoute() {
        String routesPath = this.path + PATH_ROUTES + File.separator;
        return routesPath + "api.php";
    }

    private void updateHistory(String action, String file) {
        changeHistory.add(action + " " + file);
    }
}
