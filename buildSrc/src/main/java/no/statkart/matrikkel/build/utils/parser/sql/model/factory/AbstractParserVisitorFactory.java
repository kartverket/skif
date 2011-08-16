package no.statkart.matrikkel.build.utils.parser.sql.model.factory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * Felles funksjonalitet for parsing av {@link java.io.Reader tekst-readere}
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class AbstractParserVisitorFactory implements VisitorFactoryInterface {

    protected final LineNumberReader reader;
    protected final Scanner scanner;

    protected AbstractParserVisitorFactory(LineNumberReader reader) {
        this.reader = reader;
        scanner = new DefaultScanner(reader);
    }


    public interface Scanner {
        /**
         * @param pattern regEx-pattern
         * @return dersom påfølgende input matcher pattern
         */
        boolean nextMatches(String pattern);

        /**
         * @param delimiterString
         * @param returnDelimiter
         * @return påfølgende tekst til enten EOF, eller til {@code delimiterPattern}
         */
        String getInput(String delimiterString, boolean returnDelimiter);
    }


    private class DefaultScanner implements Scanner {
        private final int READ_AHEAD_LIMIT = 1024 * 2;
        private final java.util.Scanner scanner;

        private DefaultScanner(Reader reader) {
            scanner = new java.util.Scanner(reader);
            scanner.useDelimiter("/;");
        }


        /**
         * @see java.util.Scanner#hasNext(String)
         */
        @Override
        public boolean nextMatches(String pattern) {
            try {
                reader.mark(READ_AHEAD_LIMIT);
                try {
                    return scanner.hasNext(pattern);
                } finally {
                    reader.reset();
                }
            } catch (IOException e) {
                throw new RuntimeException("na", e);
            }
        }

        @Override
        public String getInput(String delimiterString, boolean returnDelimiter) {
            StringBuilder sb = new StringBuilder();
            int read = 0;
            do {
                try {
                    read = reader.read();
                    if (read != -1) {
                        sb.append((char) read);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("na", e);
                }
            } while (sb.indexOf(delimiterString) == -1);


            return returnDelimiter ? sb.toString() : sb.toString().replaceAll(delimiterString, "");
        }
    }

}
