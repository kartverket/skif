package no.statkart.skif.persistence;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Et interface for håndtering av ressourser som implementerer interfacet {@link Resource} og som gjør det mulig
 * å hent ut en ressurs basert på interface som ressoursen implementerer. Ved registrering angis alle
 * klassenavne for interfacer som skal mappe til ressoursen. Dersom flere ressourser skal mappes til
 * samme interface må mappingen av interfacet beskrives via en nøkkel {@link Key} som inneholder en string i tillegg
 * til interfacet slik at det blir unikt. Det er også mulig å mapper superklasser for en ressurs.
 *
 * En viktig egenskap ved en {@code ResourceManager} kan være å være lazy slik at ressurser ikke opprettes før
 * de etterspørs og at den kan lukke alle ressurser som har vært i bruk. Den må kunne vite om en ressurs skal inngå
 * i en transaksjon og må kunne skjelne mellom transaksjonelle og ikke transaksjonelle ressurser slik at
 * kun startes transaksjoner på ressurser som støtter det. Videre skal den ikke starte en transaksjon på en ressurs
 * før den blir etterspurt.
 *
 * Før {@code ResourceManager} kan gi ut ressurser må {@link #start} være kallt. Det er for å sikre at ressurser som blir hentet
 * ut (f.eks via dependency injection) vil skje i scope av en kodeblock som også vil sikre at {@link #close} vil bli
 * kaldt for ressoursen.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ResourceManager  {

    public final static class Key<T extends Resource> {
        final String name;
        final Class<T> type;

        public Key(Class<T> type) {
            this("", type);
        }

        public Key(String name, Class<T> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return name.hashCode() + type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key key = (Key) obj;
                return type == key.type && name.equals(key.name);
            }
            return false;
        }
    }

    public final static class Entry {
        final String name;
        final List<Class<? extends Resource>> types;
        final Resource implementation;

        /** Angir om denne resources har fått startet sin transaksjon */
        boolean transactionStarted;

        public Entry(Resource implementation) {
            this("", implementation, implementation.getClass());
        }

        public Entry(String name, Resource implementation) {
            this(name, implementation, implementation.getClass());
        }

        @SafeVarargs
        public Entry(Resource implementation, Class<? extends Resource>... types) {
            this("", implementation, types);
        }

        @SafeVarargs
        public Entry(String name, Resource implementation, Class<? extends Resource>... types) {
            this.name = name;
            this.implementation = implementation;
            this.types = ImmutableList.copyOf(types);
        }
    }


    public <T extends Resource> T getResource(Class<T> type);

    public <T extends Resource> T getResource(Key<T> key);

    /**
     * Starter {@code ResourceManager} for uthenting av ressurser
     */
    public void start();

    /**
     * Lukker {@code ResourceManager} for uthenting av ressurser
     */
    public void shutdown();

    /**
     * Returnerer true hvis en eller flere resource har blitt hentet ut
     */
    public boolean isActive();

    /**
     * Sette {@code ResourceManager} til aktiv
     */
    public void setActive();



    public void beginTransaction();

    /**
     * Utfører flush på alle transaksjonelle ressourser som har blitt hentet ut
     */
    public void flush();

    /**
     * Utfører commit på alle transaksjonelle ressourser som har blitt hentet ut
     */
    public void commit();

    /**
     * Utfører rollback på alle transaksjonelle ressourser som har blitt hentet ut
     */
    public void rollback();

    /**
     * Lukker alle resourser som har blitt hentet ut.
     * <p>
     * TODO: Vurdere om denne metode også skal kalles i JEE mode,
     */
    public void close();
}
