package no.statkart.skif.util.testsupport;

import org.testng.IHookCallBack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class TestNGSupport {
    public static Method getMethod(IHookCallBack callBack)  {
        // Nødvendig med reflection her for å få ut metoden som callBack vil kalle.
        try {
            Field field = callBack.getClass().getDeclaredField("val$thisMethod");
            field.setAccessible(true);
            Method method = (Method) field.get(callBack);
            field.setAccessible(false);
            return method;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("This should not happen. Something has changed in TestNG", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("This should not happen. Something has changed in TestNG", e);
        }
    }
}
