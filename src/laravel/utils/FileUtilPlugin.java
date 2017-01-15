package laravel.utils;

import cook.util.FileUtil;

import java.io.File;

/**
 * Created by Clézio on 15/01/2017.
 */
public class FileUtilPlugin {

    public static void saveToPath(String fileName, String arq) {
        checkCreateDir(fileName);
        FileUtil.saveToPath(fileName, arq);
    }

    private static void checkCreateDir(String fileName) {
        File file = new File(fileName);
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }
}
