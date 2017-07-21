package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;

import java.io.IOException;
import java.util.logging.*;

/**
 * Hjelpe klasse for å slå på logging for Oracle JDBC driver som bruker Java Logging Framework. Det er mulig å
 * styre logging level, men det er ikke umiddelbar mulig å styre hvilke loggere som skal produserer output. Dermed blir det
 * fort alt for mye eller for lite log output. Denne hjelpeklasse bruker derfor et logging Filter til å velge bort
 * LogRecords som ikke skal vises. Når logging er disabled kommer det ingen output. Når logging enables logges
 * sql statements når verbose mode er satt til [@code VerboseMode.OFF}.  Ved å sette verbose mode til {@code VerboseMode.ON}
 * logges også bind parametre. I denne mode settes logging level til {@code Level.FINE} og genereres det veldig mye
 * LogRecords som må filtreres bort. Derfor går det væsentlig tregere å kjøre med {@code VerboseMode-ON} logging.
 * Endelig er det mulig å sette verbose mode til {@code VerboseMode.FULL}. I denne mode logges alle log records. Siden
 * logging uansett er tregt kan det være nødvendig å slå av logging i deler av koden hvor logging ikke er interessant,
 * for eksemple i forbindelse med lesing fra scroll iteratorer. Det dette formålet brukes metodene {@link #pause()()} og
 * {@link #resume(boolean)} som midlertidig kan slå av og på logging. Det finnes også metoder {@link #setTraceState(boolean)}
 * og {@link #getTraceState()} som globalt fullstendig kan slå av og på logging, og som gjør at ingen av de andre
 * kall til OracleLogHelper har noen effekt. Et kall {@code OracleLogHelper.setTraceState(false)} i starten av ett
 * program vil slå av all logging. Programkode som skal gjenbrukes skal ikke inneholde kall til {@code setTraceState()}
 * og bør fortrinsvis bare inneholde kall til {@code pause()} og {@code resume(boolean)}.
 *
 * <p>
 * Logging sendes til konsolen som default, men det er mulig å sende logging til en fil i stedet. Hvis det skal sendes
 * til fil må man kalle {@link #initHandler(String)} før {@link #enableTrace(Verbose)} kalles første gang.
 * <p>
 * For at det skal komme noe logging i det hele tatt må man bruke ojdbc*_g versjonen av Oracle driveren og det er
 * viktig å sjekke at ikke andre bibliotekter med oracle driver (f.eks weblogic ) er først i classpath. Dette kan
 * f.eks sjekkes ved å inspisere oracle.jdbc.driver.OracleLog.class.getProtectionDomain().getCodeSource()
 * <p>
 * Ved overgang til ny versjon  av Oracle JDBC driver må filtrene i denne logger oftest skrives om pga interne endringer
 * i driveren fører til at det genereres andre log records. Her gjelder det og oppdatere OracleLogHelper til å plukke
 * ut de riktige records slik at kun sql og bindingsparametre vises.
 * bort de
 *
 * @author Henrik Fredholm
 */
public class OracleLogHelper {
    private static boolean traceState = true;

    /**
     * Kall til logger.setTrace er tregt. Denne variable sikre at det kun skjer ved behov.
     */
    private static boolean cachedState = false;


    /**
     * Angir hvormye info som skal logges
     */
    public enum Verbose {
        /** Kun sql statements */
        ON,
        /** Også bindingsparametre */
        OFF,
        /** Alle log records */
        FULL
    }

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
     */
    public static void initHandler(String filename) {
        if (handler != null) {
            throw new ImplementationException("Handler already set");
        }
        try {
            handler = new FileHandler(filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Slå på sql logging, med mindre at {@code traceState} er {@code false}.
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
        setTrace(traceState);
        logger.log(Level.INFO, "OracleLogHelper: Trace enabled verbose=" + mode + " traceState=" + traceState);
    }

    public static void enableTraceVerbose() {
        enableTrace(Verbose.ON);
    }

    /**
     * Slår av sql logging
     */
    public static void disableTrace() {
        logger.log(Level.INFO, "OracleLogHelper: Trace disabled");
        setTrace(false);
    }

    private static void setTrace(boolean state) {
        if (cachedState != state) {
            oracle.jdbc.driver.OracleLog.setTrace(state);
            cachedState = state;
        }
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
                        } else if (verbose != Verbose.OFF && methodname.equals("addBatch") && record.getMessage().contains("Enter: ")) {
                            result = true;
                        } else if (verbose == Verbose.OFF && methodname.equals("prepareStatement") && classname.equals("oracle.jdbc.driver.PhysicalConnection") && record.getMessage().contains("Public Enter:")) {
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

            if (verbose == Verbose.FULL) {
                result = true;
            }
            return result;
        }
    }

    /**
     * Gir mulighet for å pause tracingen uavhenging av om tracing er på eller av. Dette kan være nødvendig fordi
     * tracing er treg.
     *
     * @return oldState
     */
    public static boolean pause() {
        boolean oldState = cachedState;
        setTrace(false);
        return oldState;
    }

    /**
     * Gjenoppretter tracing til tidligere tilstand
     */
    public static void resume(boolean state) {
        setTrace(traceState);
    }

    /**
     * Global setting for å slå tracing fullstendig av også for fremtidig kall til {@link #enableTrace(Verbose)}.
     *
     * @param state bestemmer tracing
     */
    public static void setTraceState(boolean state) {
        traceState = state;
    }

    /**
     * Returnerer traceState som forteller om logging er på eller av.
     */
    public static boolean getTraceState() {
        return traceState;
    }
}
