package laravel.utils;

import cook.util.FileUtil;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getFilesList(String fileName, ArrayList<String> fileList) {
        File fileParent = new File(fileName);
        for(File file : fileParent.listFiles()){
            if(file.isDirectory()){
                fileList.addAll(getFilesList(file.getAbsolutePath(), fileList));
            }else{
                fileList.add(file.getAbsolutePath());
            }
        }
        return fileList;
    }

    public static void importTemplate(String url, String path) {
        String fileNameTemplateZip = null;
        String fileNameTemplate = null;
        try {
            fileNameTemplateZip = FileUtil.download(url, path);
            fileNameTemplate = fileNameTemplateZip.substring(0,fileNameTemplateZip.length() - 4);
            FileUtil.extractZip(path + fileNameTemplateZip, path);
            List<String> filesList = FileUtilPlugin.getFilesList(path + File.separator + fileNameTemplate, new ArrayList<String>());

            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
            };

            for(String fileName : filesList){
                File fileTarget = new File(fileName.replace(File.separator + fileNameTemplate,""));
                Path source = Paths.get(fileName);
                Path target = Paths.get(fileTarget.getAbsolutePath());
                if(!fileTarget.getParentFile().exists()) {
                    fileTarget.getParentFile().mkdirs();
                }
                Files.copy(source, target, options);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(fileNameTemplate != null){
                    FileUtil.deleteDir(path + fileNameTemplate);
                }
                if(fileNameTemplateZip != null) {
                    FileUtil.deleteDir(path + fileNameTemplateZip);
                }
            }catch(Exception e){
                ;
            }
        }

    }
}
