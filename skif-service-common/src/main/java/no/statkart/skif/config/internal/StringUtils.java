package no.statkart.skif.config.internal;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str==null || str.length()==0;
    }
}
