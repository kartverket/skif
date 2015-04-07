package no.statkart.skif.store;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

/**
 */
public final class ConcatenatedFields {
   public static char SEPARATOR = ',';
   public static char START_CHAR = '[';
   public static char END_CHAR = ']';
   private static Pattern pattern = Pattern.compile(",");
   final String value;

    public ConcatenatedFields(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }

    public final String[] getFields(){
        checkState(value.charAt(0)==START_CHAR);
        checkState(value.charAt(value.length()-1)==END_CHAR);
        return replaceNullPattern(pattern.split(value.substring(1, value.length()-1), 0));
    }

    private String[] replaceNullPattern(String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].equals("null")) {
                fields[i] = null;
            }
        }
        return fields;
    }

    public static ConcatenatedFields create(Object... fields) {
        StringBuilder builder = new StringBuilder();
        builder.append(START_CHAR);
        for (Object field : fields) {
            builder.append(field);
            builder.append(SEPARATOR);
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(END_CHAR);
        return new ConcatenatedFields(builder.toString());
    }

    public static <T extends ConcatenatedFieldsSerialization>  T createObject(String classname, ConcatenatedFields fields) {
        return SkifUtil.newInstance(classname, fields);
    }

}
