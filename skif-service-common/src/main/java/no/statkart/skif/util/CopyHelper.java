package no.statkart.skif.util;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.store.SnapshotVersion;

import java.io.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class CopyHelper {
    private static final int BUFFER_SIZE = 1024;

    /**
     * Trådlokal variable som normalt er null, men som CopyHelper setter til en non-null verdi dersom
     * kopiering av et objekt skal fører til at utvalgte SnapshotVersion felter i objektet overskrives med den satte
     * verdien. Etter bruk sette variablen tilbake til null.
     *
     * <P/>Hvilke felter som får overskrevet snapshotVersion felter styres av klassen selv ved å implementere følgende
     * serialiseringslogikk:
     * <pre>
     * private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
     *    stream.defaultReadObject();
     *    SnapshotVersion replaceWithSnapshotVersion = CopyHelper.getSnapshotVersion();
     *    if (replaceWithSnapshotVersion !=null) {
     *       this.snapshotVersion =  replaceWithSnapshotVersion;
     *    }
     * }
     * </pre>
     */
    private static ThreadLocal<SnapshotVersion> snapshotVersionThreadLocal = new ThreadLocal<SnapshotVersion>();

    public static SnapshotVersion getSnapshotVersion() {
        return snapshotVersionThreadLocal.get();
    }

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
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new OperationalException(e);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }

    /**
     * Makes a copy of an object by serilizing and deserializing it. Objects may use the thread local method
     * {@link #getSnapshotVersion()} to reset their snapshotVersion fields for copied objects
     *
     * @param object the object to copy.
     * @return a copy.
     */
    public static <T> T copy(T object, SnapshotVersion snapshotVersion) {
        if (object == null) return null;
        SnapshotVersion origValue = snapshotVersionThreadLocal.get();
        try {
            snapshotVersionThreadLocal.set(snapshotVersion);
            return copy(object);
        } finally {
            snapshotVersionThreadLocal.set(origValue);
        }
    }

    public static void copy(InputStream in, File dest) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dest);
            byte[] bytes = new byte[BUFFER_SIZE];

            int i;
            while ((i = in.read(bytes, 0, bytes.length)) > 0) {
                fos.write(bytes, 0, i);
            }
        } finally {
            if (fos != null) try {
                fos.close();
            } catch (IOException ioe) {
                throw new OperationalException(ioe);
            }
        }
        in.close();
    }
}
