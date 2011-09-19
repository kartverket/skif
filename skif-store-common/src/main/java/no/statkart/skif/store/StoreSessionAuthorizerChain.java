package no.statkart.skif.store;

/**
 * StoreSessionReadChain ledd som kan maskere felter i et objekt om nødvendig. Maskeringen sker på en kopi slik
 * at original objektet ikke endres.
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StoreSessionAuthorizerChain extends StoreReadChain {
    /**
     * Gjør felter som brukeren ikke har adgang til å se uleselige ved å fjerne deler eller hele feltet. Dersom
     * det er nødvendig å maskere et fellt må metoden lage en kopi av bobleobjektet først, endre dette og sette
     * det nye objektet i storeEntry.
     *
     * @param storeEntry innehold objekt som evt skal maskeres
     * @param <T>
     */
    <T extends BubbleObject> void maskFields(StoreEntry<T> storeEntry);
}
