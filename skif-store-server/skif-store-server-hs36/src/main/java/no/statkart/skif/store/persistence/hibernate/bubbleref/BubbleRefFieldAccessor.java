package no.statkart.skif.store.persistence.hibernate.bubbleref;

import org.hibernate.HibernateException;
import org.hibernate.PropertyAccessException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;
import org.hibernate.util.ReflectHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Class for accessing BubbleId property values on objects directly through its field, which may be
 * nonpublic. The implementation is based on the default implementation  {@link
 * org.hibernate.property.DirectPropertyAccessor}, but has been modified to produce a field
 * with postfix "Id" for a given property. E.g. a mapping property named "user" would produce a
 * field accessor for the field "userId".
 *
 * @author Henrik Fredholm
 */
public class BubbleRefFieldAccessor implements PropertyAccessor {

   public static final class DirectGetter implements Getter {
      private final Field field;
      private final Class clazz;
      private final String name;

      DirectGetter(Field field, Class clazz, String name) {
         this.field = field;
         this.clazz = clazz;
         this.name = name;
      }


      public Object get(Object target) throws HibernateException {
         try {
            return field.get(target);
         }
         catch (Exception e) {
            throw new PropertyAccessException(e, "could not get a field value by reflection", false, clazz, name);
         }
      }

      public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) throws HibernateException {
         return get(target);
      }

       @Override
       public Member getMember() {
           return field;
       }

       public Method getMethod() {
         return null;
      }

      public String getMethodName() {
         return null;
      }

      public Class getReturnType() {
         return field.getType();
      }

   }

   public static final class DirectSetter implements Setter {
      private final Field field;
      private final Class clazz;
      private final String name;

      DirectSetter(Field field, Class clazz, String name) {
         this.field = field;
         this.clazz = clazz;
         this.name = name;
      }

      public Method getMethod() {
         return null;
      }

      public String getMethodName() {
         return null;
      }

      public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException {
         try {
            field.set(target, value);
         }
         catch (Exception e) {
            throw new PropertyAccessException(e, "could not set a field value by reflection", true, clazz, name);
         }
      }

   }

   private static Field getField(Class clazz, String name) throws PropertyNotFoundException {
      String nameWithId = name + "Id";
      if (clazz == null || clazz == Object.class) throw new PropertyNotFoundException("field not found: " + nameWithId);
      Field field;
      try {
         field = clazz.getDeclaredField(nameWithId);
      }
      catch (NoSuchFieldException nsfe) {
         field = getField(clazz.getSuperclass(), name);
      }
      if (!ReflectHelper.isPublic(clazz, field)) field.setAccessible(true);
      return field;
   }

   public Getter getGetter(Class theClass, String propertyName)
         throws PropertyNotFoundException {
      return new DirectGetter(getField(theClass, propertyName), theClass, propertyName);
   }

   public Setter getSetter(Class theClass, String propertyName)
         throws PropertyNotFoundException {
      return new DirectSetter(getField(theClass, propertyName), theClass, propertyName);
   }

}
