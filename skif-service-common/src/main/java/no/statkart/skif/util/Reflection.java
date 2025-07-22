package no.statkart.skif.util;

import no.statkart.skif.exception.ReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Static convenience methods for finding methods and
 * making reflection invocation methods without
 * being drowned in exception handling
 *
 * @author  <a href="mailto:bratseth@pvv.org">Jon S Bratseth</a>
 * @version $SpifId: Reflection.java,v 1.1.1.1 2003/03/12 09:04:15 henrik Exp $
 */
public class Reflection {

    /** Use statically */
    private Reflection() {}

    /**
     * Invokes a no-argument method on an object
     *
     * @throws ReflectionException for any error
     */
    public static Object invoke(Object on,Method method)
            throws ReflectionException {
        return invoke(on,method,null);
    }

    /**
     * Invokes a single argument method on an object
     *
     * @throws ReflectionException for any error
     */
    public static Object invoke(Object on,Method method,Object argument)
            throws ReflectionException {
        return invoke(on,method,new Object[] { argument } );
    }

    /**
     * Invokes a method on an object
     *
     * @throws ReflectionException for any error
     */
    public static Object invoke(Object on,Method method,Object[] arguments)
            throws ReflectionException {
        try {
            return method.invoke(on,arguments);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionException("Could not access " + method + " on " + on,e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionException("An exception occured while invoking " + method +
                                          " on " + on + " with arguments " + arguments,e);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException("Incorrect parameters, could not invoke " +
                                          method + " on " + on + " with arguments " +
                                          arguments,e);
        }
    }

    /**
     * Invokes a constructor taking no arguments
     *
     * @throws ReflectionException for any error
     */
    public static Object invoke(Constructor constructor) {
	return invoke(constructor,new Object[] { null });
    }

    /**
     * Invokes a constructor taking one argument
     *
     * @throws ReflectionException for any error
     */
    public static Object invoke(Constructor constructor,Object argument) {
	return invoke(constructor,new Object[] { argument });
    }

    /**
     * Invokes a constructor
     *
     * @throws ReflectionException for any error
     */
    public static Object invoke(Constructor constructor,Object[] arguments)
            throws ReflectionException {
        try {
            return constructor.newInstance(arguments);
        }
	catch (InstantiationException e) {
	    throw new ReflectionException("Cold not invoke " + constructor +
					  ", the class is abstract",e);
	}
        catch (IllegalAccessException e) {
            throw new ReflectionException("Could not access " + constructor,e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionException("An exception occured while invoking " +
					  constructor + " with arguments " + arguments,e);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException("Incorrect parameters, could not invoke " +
                                          constructor + " with arguments " + arguments,e);
        }
    }

    /**
     * Creates an instance of the given class if possible
     *
     * @return the instance or null if there is no default constructor in the class
     */
    public static Object newInstance(Class type) {
        try {
            return type.newInstance();
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }
    /**
     * Creates an instance of the given class if possible
     *
     * @return the instance or null if there is no matching constructor in the class
     */
    public static Object newInstance(Class type,Object argument) {
        try {
	    Constructor constructor=findConstructor(type,new Class[] { argument.getClass() } );
	    if (constructor==null)
		return null;
            return constructor.newInstance(new Object[] { argument} );
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
        catch (InvocationTargetException e) {
            return null;
        }
    }

    /** Finds a constructor and returns it, or null if it was not found */
    public static Constructor findConstructor(Class type,Class[] argTypes) {
        try {
            return type.getConstructor(argTypes);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (SecurityException e) {
            return null;
        }
    }


    /** Finds a method witrh no arguments and returns it, or null if it was not found */
    public static Method findMethod(Class type,String name) {
        return findMethod(type,name,new Class[] {});
    }

    /** Finds a method and returns it, or null if it was not found */
    public static Method findMethod(Class type,String name,Class[] argTypes) {
        try {
            return type.getMethod(name,argTypes);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (SecurityException e) {
            return null;
        }
    }

    /** Returns a setter matching the given getter, or null if none is found */
    public static Method findSetterMatching(Class type,Method getter) {
        if (getter==null) return null;
        try {
            int skipChars=3;
            if (getter.getName().startsWith("is"))
                skipChars=2;
            return type.getMethod("set" + getter.getName().substring(skipChars),
                                  new Class[] { getter.getReturnType() });
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (SecurityException e) {
            return null;
        }
    }

    /**
     * Returns an adder matching the given getter collection, or null if none is found
     *
     * @param type the type containing the adder
     * @param collectionGetter the getter to match
     * @param adderType the type of the adder, or null to return the first name match
     */
    public static Method findAdderMatching(Class type,Method collectionGetter,
					   Class adderType) {
        if (adderType==null)
            return findAdderMatching(type,collectionGetter);
        if (collectionGetter==null) return null;
        try {
	    String name=collectionGetter.getName();
            return type.getMethod("add" +
				  name.substring(3,name.length()-"Iterator".length()),
                                  new Class[] { adderType });
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (SecurityException e) {
            return null;
        }
    }

    /**
     * Returns the first (any) adder matching the given getter collection,
     * or null if none is found
     *
     * @param type the type containing the adder
     * @param collectionGetter the getter to match
     */
    public static Method findAdderMatching(Class type,Method collectionGetter) {
        String targetName=
            "add" + collectionGetter.getName().substring(3,
                                    collectionGetter.getName().length()-"Iterator".length());
        Method[] all=type.getMethods();
        for (int i=0; i<all.length; i++) {
            if (targetName.equals(all[i].getName())
                && all[i].getParameterTypes().length==1
                && !Modifier.isStatic(all[i].getModifiers()))
                return all[i];
        }
        return null;
    }

    /**
     * Returns a getter for the given name, or null if there is no such getter
     * Case is not significant.
     */
    public static Method findGetter(Class type,String propertyName) {
	String lowPropertyName=propertyName.toLowerCase();
	Iterator getters=findGetters(type).iterator();
	while (getters.hasNext()) {
	    Method getter=(Method)getters.next();
	    String getterName=getter.getName().toLowerCase();
	    if (getterName.equals("get" + lowPropertyName) ||
		getterName.equals("is" + lowPropertyName))
		return getter;
	}
	return null;
    }

   /**
    * Returns a setter for the given name, or null if there is no such setter.
    * Case is not significant.
    */
   public static Method findSetter(Class type,String propertyName) {
       String lowPropertyName=propertyName.toLowerCase();
       Iterator setters=findSetters(type).iterator();
       while (setters.hasNext()) {
           Method setter=(Method)setters.next();
           if (setter.getName().toLowerCase().equals("set" + lowPropertyName))
               return setter;
       }
       return null;
   }
    /**
     * Returns all the getters in the given type.
     * A getter is a public, non-static method beginning with "get"
     * or "is" and taking no arguments.
     *
     * @return a List&lt;Method&gt; of all the getters in the given type,
     *         or an emtpy list if there is no getters
     */
    public static List findGetters(Class type) {
        List getters=new ArrayList();
        Method[] all=type.getMethods();
        for (int i=0; i<all.length; i++) {
            if ((all[i].getName().startsWith("get") || all[i].getName().startsWith("is"))
                && all[i].getParameterTypes().length==0
                && !Modifier.isStatic(all[i].getModifiers()))
                getters.add(all[i]);
        }
        return getters;
    }

    /**
     * Returns the collection getters of a given type.
     * Collection getters are public, non-static no-arg methods of the form get*Iterator,
     * and returning an iterator
     */
    public static List findCollectionGetters(Class type) {
        List getters=new ArrayList();
        Method[] all=type.getMethods();
        for (int i=0; i<all.length; i++) {
            if (all[i].getName().startsWith("get")
		&& all[i].getName().endsWith("Iterator")
                && all[i].getParameterTypes().length==0
                && !Modifier.isStatic(all[i].getModifiers()))
                getters.add(all[i]);
        }
        return getters;
    }

    /**
     * Returns all the setters in the given type.
     * A getter is a public, non-static method beginning with "set"
     * and taking one argument.
     *
     * @return a List&lt;Method&gt; of all the setters in the given type,
     *         or an emtpy list if there is no setters
     */
    public static List findSetters(Class type) {
        List setters=new ArrayList();
        Method[] all=type.getMethods();
        for (int i=0; i<all.length; i++) {
            if (all[i].getName().startsWith("set")
                && all[i].getParameterTypes().length==1
                && !Modifier.isStatic(all[i].getModifiers()))
                setters.add(all[i]);
        }
        return setters;
    }

    /** Returns the short name of a class */
    public static String toName(Class type) {
        String className=type.getName();
        int lastDotIndex=className.lastIndexOf(".");
        int dollarIndex=className.lastIndexOf("$"); // For inner classes
        int cutIndex=Math.max(lastDotIndex,dollarIndex);
        if (cutIndex<0)
            return className.substring(0,className.length());
        else
            return className.substring(cutIndex+1,className.length());
    }

    /**
     * Returns the name of the property the given
     * method is a getter or setter for, in lowercase only.
     * Works for collection getters too.
     *
     * @return the property name, or null if the given method is not an accessor
     */
    public static String toPropertyName(Method accessor) {
        String name=accessor.getName().toLowerCase();
        if (name.endsWith("iterator"))
            name=name.substring(0,name.length()-"iterator".length());
        if (name.startsWith("get"))
            return name.substring(3);
        else if (name.startsWith("is"))
            return name.substring(2);
        else if (name.startsWith("set"))
            return name.substring(3);
        return null;
    }

}
