package laravel.cook;

import cook.core.IFCook;
import cook.core.ResultProcess;
import cook.util.FileUtil;
import cook.util.PrintUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by clezio on 08/08/16.
 */
public class Recipe implements IFCook {

    public static final String MODEL = "model";
    public static final String CONTROLLER = "controller";
    public static final String VIEW = "view";
    public static final String ARTISAN_FILE = "artisan";

    private String[] param;

    private String action;

    private String path;

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
        PrintUtil.outn(CONTROLLER);
        PrintUtil.outn(VIEW);
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
                param[1].toLowerCase().equals(CONTROLLER) ||
                param[1].toLowerCase().equals(VIEW))){
            printHelp();
            PrintUtil.outn("");
            return false;
        }

        this.param = param;
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
        ResultProcess outn = new ResultProcess();
        if (this.action.equals(MODEL)) {
            try {
                outn = Generator.getInstance(this.path).generatorModel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (this.action.equals(CONTROLLER)) {
            //ArquivosPlay.getArquivoPlay().copiaArquivosPlay(this.PATH_ARQUIVOS);
            //outn = PlayGerador.getPlayGerador().criaController(PATH_OUT_MODEL, PATH_OUT);
        } else if (this.action.equals(VIEW)) {
            //outn = PlayGerador.getPlayGerador().criaView(PATH_OUT_MODEL, PATH_OUT);
        } else {
            outn.setResultProcess(ResultProcess.ERROR, "Action not found");
        }
        return outn;
    }

    @Override
    public void end() {
        PrintUtil.outn("");
        PrintUtil.outn("Created file.");
    }
}
