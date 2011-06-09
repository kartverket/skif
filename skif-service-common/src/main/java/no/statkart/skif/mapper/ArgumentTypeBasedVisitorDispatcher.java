package no.statkart.skif.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Denne klassen implementerer dispatcher delen av et visitor pattern.
 * @author Henrik Fredholm
 */
public class ArgumentTypeBasedVisitorDispatcher implements VisitorDispatcher {
    private Map<Class, Method> methodCache = new HashMap<Class, Method>();
    private Visitor visitor;
    Class<? extends Visitor> visitorClass;
    private Method NO_VISITOR_IMPLEMENTED; // Angir at ingen visitor er implementert for denne objekttype

    public ArgumentTypeBasedVisitorDispatcher(Visitor visitor) {
        this.visitor = visitor;
        visitorClass = visitor.getClass();
        visitor.setDispatcher(this);
        try {
            NO_VISITOR_IMPLEMENTED = getClass().getMethod("visitor", Object.class);
        } catch (NoSuchMethodException e) {
            // Kan ikke feile
        }
    }

    @Override
    public void visit(Object object) {
        if (object==null) {
            return;
        }
        Class argClass = object.getClass();
        Method m = methodCache.get(argClass);
        while (m == null) {
            try {
                m = visitorClass.getMethod("visit", argClass);
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e) {
            }
            if (m != null) {
                methodCache.put(object.getClass(), m);
                break;
            } else if (argClass==Object.class) {
                methodCache.put(object.getClass(), NO_VISITOR_IMPLEMENTED);
                break;
            }
            argClass = argClass.getSuperclass();
        }

        if (m!=null || m!= NO_VISITOR_IMPLEMENTED) {
            try {
                m.invoke(visitor, object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                if (target instanceof RuntimeException) {
                    throw (RuntimeException)target;
                } else {
                    throw new RuntimeException(target);
                }
            }
        } else if (object instanceof Collection) {
            for (Object o : (Collection)object) {
                visit(o);
            }
        } else if (object.getClass().isArray()) {
            int len= Array.getLength(object);
            for (int i=0; i <len; i++ ) {
                visit(Array.get(object, i));
            }
        }
    }
}