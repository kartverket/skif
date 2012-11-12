package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;

import java.io.IOException;
import java.util.logging.*;

/**
 * Hjelpe klasse for å slå på logging for Oracle JDBC driver. Oralce driveren bruke Java Logging Framework. Det er mulig å
 * styre logging level, men det er ikke mulig å styre hvilke loggere som skal produserer output. Dermed blir det fort for mye
 * eller for lite log output. Denne hjelpeklasse bruker derfor et logging Filter til å velge bort LogRecords som ikke
 * skal vises. Når logging er disabled kommer ingen output. Når logging enables logges sql statements. I verbose mode
 * er det også mulig å se bind parametre. Da genereres det mye LogRecords som filtreres bort. Derfor går det væsentlig
 * tregere å kjøre med verbose logging.
 * <p/>
 * Logging sendes til konsolen som default, men det er mulig å sende logging til en fil i stedet. Hvis det skal sendes
 * til fil må man kalle {@link #initHandler(String)} før {@link #enableTrace(Verbose)} kalles første gang.
 * <p/>
 * For at det skal komme noe logging i det hele tatt må man bruke ojdbc*_g versjonen av Oracle driveren og det er
 * viktig å sjekke at ikke andre bibliotekter med oracle driver (f.eks weblogic ) er først i classpath. Dette kan
 * f.eks sjekkes ved å inspisere oracle.jdbc.driver.OracleLog.class.getProtectionDomain().getCodeSource()
 *
 * @author Henrik Fredholm
 */
public class OracleLogHelper {
    /**
     * Angir hvormye info som skal logges
     */
    public enum Verbose {
        ON, OFF, FULL
    }

    ;

    private static Handler handler;
    private static OracleLogFilter filter;

    // Forhindre garbage collection av logger.
    private static Logger logger;

    private static synchronized void configureOracleJDBCLogging() {
        if (filter != null) return;

        // Sett opp et filter som velger bort alle FINE records untatt noen få utvalgte
        filter = new OracleLogFilter();
        if (handler == null) {
            handler = new ConsoleHandler();
        }
        handler.setFilter(filter);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        handler.publish(new LogRecord(Level.INFO, "Oracle Logging configured"));
        handler.publish(new LogRecord(Level.INFO, "Oracle Library path: " + oracle.jdbc.driver.OracleLog.class.getProtectionDomain().getCodeSource()));
        logger = Logger.getLogger("oracle.jdbc");
        Logger.getLogger("oracle.jdbc").addHandler(handler);
        Logger.getLogger("oracle.jdbc").setUseParentHandlers(false);
        Logger.getLogger("oracle.jdbc").setLevel(Level.CONFIG);
    }

    /**
     * Initialiser Logger for "oracle.jdbc" til å bruke en FileHandler istedet for ConsoleHandler som er default. Denne metoden må
     * kalles før første kall til {@link #enableTrace(Verbose)} og kan kun kalle en gang. Bruke evt {@link #isHandlerInitialized()}
     * til å sjekke om en handler allerede er satt.
     *
     * @param filename
     */
    public static void initHandler(String filename) {
        if (handler != null) {
            throw new ImplementationException("Handler allerede satt");
        }
        try {
            handler = new FileHandler(filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Slå på sql logging
     *
     * @param mode OFF hvis kun sql statements skal logges. ON for mer detaljert logging info, inkl parameter binning
     */
    public static void enableTrace(Verbose mode) {
        configureOracleJDBCLogging();
        filter.verbose = mode;
        if (mode == Verbose.OFF) {
            Logger.getLogger("oracle.jdbc").setLevel(Level.FINE);
        } else {
            Logger.getLogger("oracle.jdbc").setLevel(Level.FINER);
        }
        oracle.jdbc.driver.OracleLog.setTrace(true);
    }

    public static void enableTraceVerbose() {
        enableTrace(Verbose.ON);
    }

    /**
     * Slår av sql logging
     */
    public static void disableTrace() {
        oracle.jdbc.driver.OracleLog.setTrace(false);
    }

    /**
     * Returnerer true hvis Logging handler er initialisert for Logger "oracle.jdbc".
     *
     * @return true hvis Logging handler er initialisert for Logger "oracle.jdbc".
     */
    public static boolean isHandlerInitialized() {
        return handler != null;
    }

    private static class OracleLogFilter implements Filter {
        Verbose verbose = Verbose.OFF;

        public boolean isLoggable(LogRecord record) {
            boolean result = true;
            if (record.getLevel().intValue() <= Level.CONFIG.intValue()) {
                // For LogRecords som er mer detaljert enn CONFIG skal kun utvalgte records logges
                result = false;
                String classname = record.getSourceClassName();
                String methodname = record.getSourceMethodName();
                if (classname != null && methodname != null) {
                    if (record.getLevel().intValue() == Level.FINE.intValue()) {
                        if (methodname.equals("connect")) {
                            // Logger oppkobling mot database
                            result = true;
                        } else if (verbose!=Verbose.OFF && methodname.equals("addBatch") && record.getMessage().contains("Enter: ")) {
                            result = true;
                        } else if (verbose==Verbose.OFF && methodname.equals("prepareStatement") && classname.equals("oracle.jdbc.driver.PhysicalConnection") && record.getMessage().contains("Public Enter:")) {
                            result = true;
                        }
                    } else {
                        if (methodname.equals("StringToCharBytes") && classname.equals("oracle.jdbc.driver.DBConversion") && record.getMessage().contains(" Enter: \"")) {
                            // Logger sql statement hvor alle bindingsvariable har fått nummer
                            result = true;
                        } else {
                            if (classname.equals("oracle.jdbc.driver.OraclePreparedStatement")) {
                                if (methodname.startsWith("set") && record.getMessage().contains(" Enter: ")) {
                                    if (record.getMessage().endsWith("Enter: ") || !Character.isUpperCase(methodname.charAt(methodname.indexOf("set") + 3))) {
                                        //skip, hvis record kun inneholder "Enter: " eller metodenavn ikke har format setX
                                    } else {
                                        // Logger bindingsvariable med verdier
                                        result = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (verbose==Verbose.FULL)  {
              result = true;
            }
            return result;
        }

        ;
    }
}
