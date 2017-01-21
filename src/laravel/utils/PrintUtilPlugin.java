package laravel.utils;

import cook.util.PrintUtil;

/**
 * Created by clezio on 16/08/16.
 */
public class PrintUtilPlugin extends PrintUtil {
    public static void printLineYellow(String text) {
        outn(getYellowFont() + text + PrintUtil.getColorReset());
    }

    public static void printLineRed(String text) {
        outn(getRedFont() + text + PrintUtil.getColorReset());
    }

    public static void printLineGreen(String text) {
        outn(getGreenFont() + text + PrintUtil.getColorReset());
    }

    public static void printNewLine() {
        PrintUtil.outn("");
    }

    public static void printLineYellowGreenYellow(String text0, String text1, String text2) {
        outn(getYellowFont() + text0 + getGreenFont() + text1 + getYellowFont() + text2 + PrintUtil.getColorReset());
    }
}
