package laravel.cook;

import cook.core.FreemarkerWrapper;
import cook.core.ResultProcess;
import laravel.database.DatabaseFactory;
import laravel.database.IDatabase;
import laravel.database.ResourceUtil;
import laravel.database.pojo.Attribute;
import laravel.database.pojo.ForeingKey;
import laravel.database.pojo.ModelDesign;
import laravel.database.pojo.TableDesign;
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
public class Generator {

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
    public static final String PATH_CONTROLLER = "Http//Controllers";
    public static final String CONTROLLER = "Controller";
    public static final String REGEX_EXTRACT_RELATIONSHIPS = ".*->(\\w+)\\('(.*\\\\([^\\.]+))'\\);";
    public static final String BELONGS_TO = "belongsTo";
    public static final String BELONGS_TO_MANY = "belongsToMany";
    public static final String HAS_ONE = "hasOne";
    public static final String HAS_MANY = "hasMany";

    private static Generator instance = null;

    private IDatabase database;

    private String pathProject;

    public static void main(String args[]) throws Exception {
        //Tests
        Generator.getInstance("C:\\laravel_projects\\syslaravel\\").buildController(true);
    }

    private Generator(){}

    private Generator(String pathProject) throws IOException {
        ResourceUtil conf = ResourceUtil.getInstance();
        conf.loadProperties(pathProject+ DATABASE_CONFIG_FILE);
        this.pathProject = pathProject;
        this.database = new DatabaseFactory().getDataBase(conf.getDbType(), conf.getDbHost(), conf.getDbPort(), conf.getDbName(), conf.getDbUser(), conf.getDbPassword());

    }

    public static Generator getInstance(String pathProject) throws IOException {
        if(instance == null){
            instance = new Generator(pathProject);
        }
        return instance;
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
                String arq = FreemarkerWrapper.getInstance().parseTemplate("model.ftl");
                FileUtilPlugin.saveToPath(fileName, arq);
                out.setResultProcess(ResultProcess.SUCESS, MODEL_CREATED_SUCCESSFULLY);
            }else{
                out.setResultProcess(ResultProcess.WARNING, OPERATION_CANCELED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.setResultProcess(ResultProcess.ERROR, "Erro, " + ex.getMessage());
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
                String arq = FreemarkerWrapper.getInstance().parseTemplate("controller-resource.ftl");
                FileUtilPlugin.saveToPath(fileName, arq);
                out.setResultProcess(ResultProcess.SUCESS, CONTROLLER_CREATED_SUCCESSFULLY);
            }else{
                out.setResultProcess(ResultProcess.WARNING, OPERATION_CANCELED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.setResultProcess(ResultProcess.ERROR, "Erro, " + ex.getMessage());
        }
        return out;
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

    private String getFileNameModel(String name) {
        String modelPath = pathProject + File.separator + PATH_APP + File.separator + PATH_MODEL + File.separator;
        return modelPath + File.separator + this.getModelName(name) + ".php";
    }

    private String getFileNameController(String name) {
        String controllerPath = pathProject + File.separator + PATH_APP + File.separator + PATH_CONTROLLER + File.separator;
        return controllerPath + File.separator + this.getControllerName(name) + ".php";
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
        ModelDesign modelDesign = new ModelDesign(getModelName(tableName));
        modelDesign.setAttributeList(getAttributeControllerList(tableName));
        String modelContent = readFile(getFileNameModel(tableName));
        Pattern p = Pattern.compile(REGEX_EXTRACT_RELATIONSHIPS);
        Matcher m = p.matcher(modelContent);
        while (m.find()) {
            String relationType = m.group(1);
            String simpleNameModel = m.group(3);
            if(BELONGS_TO.equals(relationType)){
                modelDesign.getOneToManyList().add(new ModelDesign(simpleNameModel));
            }else if(BELONGS_TO_MANY.equals(relationType)){
                modelDesign.getManyToManyList().add(new ModelDesign(simpleNameModel));
            }else if(HAS_ONE.equals(relationType)){
                modelDesign.getOneToOneList().add(new ModelDesign(simpleNameModel));
            }else if(HAS_MANY.equals(relationType)){
                modelDesign.getManyToOneList().add(new ModelDesign(simpleNameModel));
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
        List<Attribute> attributeList = getAttributeList(tableName);
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
            if(attribute.getPrimaryKey() || TableDesign.CREATED_AT.equals(attribute.getName()) ||
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
                manyToOneList.add(new ForeingKey(rs.getString(DatabaseFactory.PKTABLE_NAME), rs.getString(DatabaseFactory.FKCOLUMN_NAME)));
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
                    oneToManyList.add(new ForeingKey(rs.getString(DatabaseFactory.FKTABLE_NAME), rs.getString(DatabaseFactory.FKCOLUMN_NAME)));
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
                        fk.setManyToOne(new ForeingKey(rs.getString(DatabaseFactory.PKTABLE_NAME), rs.getString(DatabaseFactory.FKCOLUMN_NAME)));
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

}
