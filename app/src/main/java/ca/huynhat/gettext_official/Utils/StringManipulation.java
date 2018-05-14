package ca.huynhat.gettext_official.Utils;

/**
 * Created by huynhat on 2018-03-12.
 */

public class StringManipulation {
    public static String expandUsername(String username){
        return username.replace("."," ");
    }

    public static String condenseString(String username){
        return username.replace(" ",".");
    }

}
