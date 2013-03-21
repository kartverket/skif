package no.statkart.skif.config;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Dec 11, 2006
 * Time: 9:26:13 PM
 * To change this template use File | Settings | File Templates.
 */
class ObjectArrayIterator implements Iterator {
   private Object[] array;
   private int i= 0;
   // ctor
   public ObjectArrayIterator(Object[] array) {
      // check for null being passed in etc.
      this.array= array;
   }
   // interface implementation

   public boolean hasNext() { return i < array.length; }
   public Object next() { return array[i++]; }
   public void remove() { throw new UnsupportedOperationException(); }
}
