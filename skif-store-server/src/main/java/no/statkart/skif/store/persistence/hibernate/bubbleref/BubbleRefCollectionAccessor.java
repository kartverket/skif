package no.statkart.skif.store.persistence.hibernate.bubbleref;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.PropertyAccessException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;
import org.hibernate.property.Getter;
import org.hibernate.util.ReflectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for accessing BubbleId collection property values via a getter/setter pair, which may be
 * non public. The implementation is based on the default implementation from {@link
 * org.hibernate.property.BasicPropertyAccessor}, but has been modified such that the
 * getter/setter pair must contain the postfix: "Ids". E.g. a mapping property
 * named "users" must have a methods called getUsersIds/setUsersIds.
 *
 * @author Henrik Fredholm
 */
public class BubbleRefCollectionAccessor implements PropertyAccessor {

   private static final Logger log = LoggerFactory.getLogger(BubbleRefCollectionAccessor.class);

   public static final class BubbleRefCollectionSetter implements Setter {
      private Class clazz;
      private final Method method;
      private final String propertyName;

      private BubbleRefCollectionSetter(Class clazz, Method method, String propertyName) {
         this.clazz = clazz;
         this.method = method;
         this.propertyName = propertyName;
      }

      public void set(Object target, Object value, SessionFactoryImplementor sessionFactoryImplementor) throws HibernateException {
         try {
            method.invoke(target, new Object[]{value});
         } catch (NullPointerException npe) {
            if (value == null && method.getParameterTypes()[0].isPrimitive()) {
               throw new PropertyAccessException(npe, "Null value was assigned to a property of primitive type", true, clazz, propertyName);
            } else {
               throw new PropertyAccessException(npe, "NullPointerException occurred while calling", true, clazz, propertyName);
            }
         } catch (InvocationTargetException ite) {
            throw new PropertyAccessException(ite, "Exception occurred inside", true, clazz, propertyName);
         } catch (IllegalAccessException iae) {
            throw new PropertyAccessException(iae, "IllegalAccessException occurred while calling", true, clazz, propertyName);
            //cannot occur
         } catch (IllegalArgumentException iae) {
            if (value == null && method.getParameterTypes()[0].isPrimitive()) {
               throw new PropertyAccessException(iae, "Null value was assigned to a property of primitive type", true, clazz, propertyName);
            } else {
               log.error("IllegalArgumentException in class: " + clazz.getName() +
                     ", setter method of property: " + propertyName);
               log.error("expected type: " + method.getParameterTypes()[0].getName() +
                     ", actual value: " + (value == null ? null : value.getClass().getName()));
               throw new PropertyAccessException(iae, "IllegalArgumentException occurred while calling", true, clazz, propertyName);
            }
         }
      }

      public Method getMethod() {
         return method;
      }

      public String getMethodName() {
         return method.getName();
      }

   }

   public static final class BubbleRefCollectionGetter implements Getter {
      private Class clazz;
      private final Method method;
      private final String propertyName;

      private BubbleRefCollectionGetter(Class clazz, Method method, String propertyName) {
         this.clazz = clazz;
         this.method = method;
         this.propertyName = propertyName;
      }

      public Object get(Object target) throws HibernateException {
         try {
            return method.invoke(target, (Object[]) null);
         } catch (InvocationTargetException ite) {
            throw new PropertyAccessException(ite, "Exception occurred inside", false, clazz, propertyName);
         } catch (IllegalAccessException iae) {
            throw new PropertyAccessException(iae, "IllegalAccessException occurred while calling", false, clazz, propertyName);
            //cannot occur
         } catch (IllegalArgumentException iae) {
            log.error("IllegalArgumentException in class: " + clazz.getName() +
                  ", getter method of property: " + propertyName);
            throw new PropertyAccessException(iae, "IllegalArgumentException occurred calling", false, clazz, propertyName);
         }
      }

      public Object getForInsert(Object target, Map map, SessionImplementor sessionImplementor) throws HibernateException {
         return get(target);
      }

      public Class getReturnType() {
         return method.getReturnType();
      }

      public Method getMethod() {
         return method;
      }

      public String getMethodName() {
         return method.getName();
      }

   }


   public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
      propertyName += "IdsSet";
      BubbleRefCollectionSetter result = getSetterOrNull(theClass, propertyName);
      if (result == null)
         throw new PropertyNotFoundException("Could not find a setter for property " + propertyName + " in class " + theClass.getName());
      return result;
   }

   private static BubbleRefCollectionSetter getSetterOrNull(Class theClass, String propertyName) {
      if (theClass == Object.class || theClass == null) return null;

      Method method = setterMethod(theClass, propertyName);

      if (method != null) {
         if (!ReflectHelper.isPublic(theClass, method)) method.setAccessible(true);
         return new BubbleRefCollectionSetter(theClass, method, propertyName);
      } else {
         BubbleRefCollectionSetter collectionSetter = getSetterOrNull(theClass.getSuperclass(), propertyName);
         if (collectionSetter == null) {
            Class[] interfaces = theClass.getInterfaces();
            for (int i = 0; collectionSetter == null && i < interfaces.length; i++) {
               collectionSetter = getSetterOrNull(interfaces[i], propertyName);
            }
         }
         return collectionSetter;
      }

   }

   private static Method setterMethod(Class theClass, String propertyName) {
      BubbleRefCollectionGetter getter = getGetterOrNull(theClass, propertyName);
      Class returnType = (getter == null) ? null : getter.getReturnType();

      Method[] methods = theClass.getDeclaredMethods();
      Method potentialSetter = null;
      for (int i = 0; i < methods.length; i++) {
         String methodName = methods[i].getName();

         if (methods[i].getParameterTypes().length == 1 && methodName.startsWith("set")) {
            String testStdMethod = Introspector.decapitalize(methodName.substring(3));
            String testOldMethod = methodName.substring(3);
            if (testStdMethod.equals(propertyName) || testOldMethod.equals(propertyName)) {
               potentialSetter = methods[i];
               if (returnType == null || methods[i].getParameterTypes()[0].equals(returnType)) return potentialSetter;
            }
         }
      }
      return potentialSetter;
   }

   public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
      propertyName += "IdsSet";
      BubbleRefCollectionGetter result = getGetterOrNull(theClass, propertyName);
      if (result == null)
         throw new PropertyNotFoundException("Could not find a getter for " + propertyName + " in class " + theClass.getName());
      return result;

   }

   private static BubbleRefCollectionGetter getGetterOrNull(Class theClass, String propertyName) {
      if (theClass == Object.class || theClass == null) return null;

      Method method = getterMethod(theClass, propertyName);

      if (method != null) {
         if (!ReflectHelper.isPublic(theClass, method)) method.setAccessible(true);
         return new BubbleRefCollectionGetter(theClass, method, propertyName);
      } else {
         BubbleRefCollectionGetter getter = getGetterOrNull(theClass.getSuperclass(), propertyName);
         if (getter == null) {
            Class[] interfaces = theClass.getInterfaces();
            for (int i = 0; getter == null && i < interfaces.length; i++) {
               getter = getGetterOrNull(interfaces[i], propertyName);
            }
         }
         return getter;
      }
   }

   private static Method getterMethod(Class theClass, String propertyName) {
      Method[] methods = theClass.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++) {
         // only carry on if the method has no parameters
         if (methods[i].getParameterTypes().length == 0) {
            String methodName = methods[i].getName();

            // try "get"
            if (methodName.startsWith("get")) {
               String testStdMethod = Introspector.decapitalize(methodName.substring(3));
               String testOldMethod = methodName.substring(3);
               if (testStdMethod.equals(propertyName) || testOldMethod.equals(propertyName)) return methods[i];

            }

            // if not "get" then try "is"
            if (methodName.startsWith("is")) {
               String testStdMethod = Introspector.decapitalize(methodName.substring(2));
               String testOldMethod = methodName.substring(2);
               if (testStdMethod.equals(propertyName) || testOldMethod.equals(propertyName)) return methods[i];
            }
         }
      }
      return null;
   }
}
