package no.statkart.skif.util;


import java.io.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class CopyHelper {
   private static final int BUFFER_SIZE = 1024;

   /**
    * Makes a copy of an object by serilizing and deserializing it.
    *
    * @param object the object to copy.
    * @return a copy.
    */
    public static <T> T copy(T object) {
       if (object == null) return null;
       try {
           ByteArrayOutputStream os = new ByteArrayOutputStream();
           ObjectOutputStream oos = new ObjectOutputStream(os);
           oos.writeObject(object);
           oos.close();

           ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
           ObjectInputStream ois = new ObjectInputStream(is);
          return (T)ois.readObject();
       } catch (IOException e) {
           throw new RuntimeException(e);
       } catch (ClassNotFoundException e) {
           throw new RuntimeException(e);
       }
   }

   public static void copy(InputStream in, File dest) throws IOException {
      FileOutputStream fos = null;
      try {
         fos = new FileOutputStream(dest);
         byte [] bytes = new byte[BUFFER_SIZE];

         int i;
         while( (i = in.read(bytes, 0, bytes.length)) > 0 ) {
            fos.write(bytes, 0, i);
         }
      } finally {
         if( fos != null ) try {
            fos.close();
         } catch( IOException ioe ) {
            throw new RuntimeException(ioe);
         }
      }
      in.close();
   }

}
