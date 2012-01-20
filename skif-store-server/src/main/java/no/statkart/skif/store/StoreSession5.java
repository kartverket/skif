package no.statkart.skif.store;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSession5 {
    /**
     * Henter objekt med gitt bubbleId knyttet til sessionen. Hvis ingen objekt er knyttet til sessionen vil objektet
     * blir hentet fra underliggende session
     * @param bubbleId
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @param bubbleObjects
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Registrerer et allerede eksisterende objekt med sessionen. Dersom det finne en annen instans med samme id som har
     * blitt endret vil denne bli returnert.
     * @param bubbleObject
     * @return
     */
    <T extends BubbleObject> T register(T bubbleObject);

    /**
     * Fjerner objektet fra sessionen. Objekter som har blitt endret fjernes først når endringene
     * @param bubbleId
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId);

    /**
     * Oppretter objektet i sessionen. Metoden kaster exception hvis objektet allerede er knyttet til sessionen eller
     * det finnes en annen instans med samme id som er knyttet til sessionen.
     * @param bubbleObject
     */
    <T extends BubbleObject> void insert(T bubbleObject);

    /**
     * Oppdaterer objektet i sessionen. Dersom det allerede finnes en annen instans knyttet til sessionen med samme
     * id vil dette objektet bli erstattet og makert som utdatert slik at denne instansen ikke kan brukes i senere kall
     * mot sessionen. Methoden støtter endring av objekts subtype samt oppdatering av objekter med skjulte felter.
     *
     * @param bubbleObject
     */
    <T extends BubbleObject> void update(T bubbleObject);

    /**
     * Markerer objektet som slettet i sessionen. Metoden kaster exception hvis objektet ikke finnes eller allerde har blitt
     * markert for sletting. Dersom det allerede finnes en annen instans knyttet til sessionen med samme
     * id vil dette objektet bli erstattet og makert som utdatert slik at denne instansen ikke kan brukes i senere
     * kall mot sessionen.
     *
     * @param bubbleObject
     */
    <T extends BubbleObject> void delete(T bubbleObject);

    <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I bubbleId);

}
