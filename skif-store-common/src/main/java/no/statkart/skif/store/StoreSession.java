package no.statkart.skif.store;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface StoreSession {
    /**
     * Henter objekt med gitt bubbleId knyttet til sessionen. Hvis ingen objekt er knyttet til sessionen vil objektet
     * blir hentet fra underliggende session
     * @param bubbleId
     * @return
     */
    <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @return
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);


    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @param bubbleObjects
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     *
     * @param bubbleIds
     * @param bubbleObjects
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);
    
    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<I> bubbleIds);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<I> bubbleIds);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     *
     * @param bubbleIds
     * @return
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<I> bubbleIds);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     *
     * @param bubbleIds
     * @param bubbleObjects
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Fjerner objektet fra sessionen. Objekter som har blitt endret fjernes ikke
     * TODO: Legge inn støtte til å kunne fjerne endret objekter etter flush/finishBatch har blitt kjørt
     * @param bubbleId
     * @return  true hvis objektet ble fjernet
     */
    <I extends BubbleId<?>> boolean evict(I bubbleId);

    /**
     * Fjerner objektet fra sessionen. Objekter som har blitt endret fjernes ikke
     * TODO: Legge inn støtte til å kunne fjerne endret objekter etter flush/finishBatch har blitt kjørt
     * @return  true hvis objektet ble fjernet
     */
    boolean evictAll();

    /**
     * Oppretter objektet i sessionen. Metoden kaster exception hvis objektet allerede er knyttet til sessionen eller
     * det finnes en annen instans med samme id som er knyttet til sessionen.
     *
     * Dersom som objektets id er null så tildeles objektet automatisk en ny id via kall til
     * {@link no.statkart.skif.service.sequence.IdService}
     *
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

    /**
     * Markerer objektet som uendret i sessionen.
     *
     * @since 2.2.0
     *
     * @param bubbleObject
     */
    <T extends BubbleObject> void undo(T bubbleObject);

    /**
     * Endre på objektets oppdateringsrekkefølge i sessionen slik at objektet kommer etter alle andre objekter
     * med samme sorteringsindex i sessionen.  Metoden kaster en exception hvis objektet ikke er endret i sessionen
     * eller hvis sessionen ikke er en unit of work
     *
     * @param bubbleId
     */
    <I extends BubbleId<?>> void reorderModification(I bubbleId);


    <T extends BubbleObject> T lock(BubbleId<? extends T> bubbleId);

    <I extends BubbleId<?>> void unlock(I bubbleId);

    <I extends BubbleId<?>> boolean isLocked(I bubbleId);

    <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject);

    void register(BubbleTransfer bubbleTransfer);
}
