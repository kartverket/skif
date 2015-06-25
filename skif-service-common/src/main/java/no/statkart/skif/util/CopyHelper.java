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

            FastByteArrayOutputStream os =
                    new FastByteArrayOutputStream();
            //ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(object);
            oos.close();

            //ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(os.getInputStream());
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

    /**
     * ByteArrayInputStream implementation that does not synchronize methods.
     * http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
     */
    private static class FastByteArrayInputStream extends InputStream {
        /**
         * Our byte buffer
         */
        protected byte[] buf = null;

        /**
         * Number of bytes that we can read from the buffer
         */
        protected int count = 0;

        /**
         * Number of bytes that have been read from the buffer
         */
        protected int pos = 0;

        public FastByteArrayInputStream(byte[] buf, int count) {
            this.buf = buf;
            this.count = count;
        }

        public final int available() {
            return count - pos;
        }

        public final int read() {
            return (pos < count) ? (buf[pos++] & 0xff) : -1;
        }

        public final int read(byte[] b, int off, int len) {
            if (pos >= count)
                return -1;

            if ((pos + len) > count)
                len = (count - pos);

            System.arraycopy(buf, pos, b, off, len);
            pos += len;
            return len;
        }

        public final long skip(long n) {
            if ((pos + n) > count)
                n = count - pos;
            if (n < 0)
                return 0;
            pos += n;
            return n;
        }
    }

    /**
     * ByteArrayOutputStream implementation that doesn't synchronize methods
     * and doesn't copy the data on toByteArray().
     * http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
     */
    private static class FastByteArrayOutputStream extends OutputStream {
        /**
         * Buffer and size
         */
        protected byte[] buf = null;
        protected int size = 0;

        /**
         * Constructs a stream with buffer capacity size 5K
         */
        public FastByteArrayOutputStream() {
            this(5 * 1024);
        }

        /**
         * Constructs a stream with the given initial size
         */
        public FastByteArrayOutputStream(int initSize) {
            this.size = 0;
            this.buf = new byte[initSize];
        }

        /**
         * Ensures that we have a large enough buffer for the given size.
         */
        private void verifyBufferSize(int sz) {
            if (sz > buf.length) {
                byte[] old = buf;
                buf = new byte[Math.max(sz, 2 * buf.length )];
                System.arraycopy(old, 0, buf, 0, old.length);
                old = null;
            }
        }

        public int getSize() {
            return size;
        }

        /**
         * Returns the byte array containing the written data. Note that this
         * array will almost always be larger than the amount of data actually
         * written.
         */
        public byte[] getByteArray() {
            return buf;
        }

        public final void write(byte b[]) {
            verifyBufferSize(size + b.length);
            System.arraycopy(b, 0, buf, size, b.length);
            size += b.length;
        }

        public final void write(byte b[], int off, int len) {
            verifyBufferSize(size + len);
            System.arraycopy(b, off, buf, size, len);
            size += len;
        }

        public final void write(int b) {
            verifyBufferSize(size + 1);
            buf[size++] = (byte) b;
        }

        public void reset() {
            size = 0;
        }

        /**
         * Returns a ByteArrayInputStream for reading back the written data
         */
        public InputStream getInputStream() {
            return new FastByteArrayInputStream(buf, size);
        }

    }
}
