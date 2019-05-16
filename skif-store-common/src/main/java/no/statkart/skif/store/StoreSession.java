package no.statkart.skif.store;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface StoreSession {

    /**
     * Henter objekt med gitt bubbleId knyttet til sessionen. Hvis ingen objekt er knyttet til sessionen vil objektet
     * blir hentet fra underliggende session
     */
    <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds);

    /**
     * Henter alle objekter med spesifisert id. Metoden kaster exception hvis ikke alle objekter ble funnet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<I> bubbleIds);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<I> bubbleIds);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<I> bubbleIds);

    /**
     * Henter objekter med spesifisert id. Metoden ignorerer om ikke alle objekter ble funnet.
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Fjerner objektet fra sessionen. Objekter som har blitt endret fjernes ikke
     * TODO: Legge inn støtte til å kunne fjerne endret objekter etter flush/finishBatch har blitt kjørt
     *
     * @return {@code true} hvis objektet ble fjernet
     */
    <I extends BubbleId<?>> boolean evict(I bubbleId);

    /**
     * Fjerner objektet fra sessionen. Objekter som har blitt endret fjernes ikke
     * TODO: Legge inn støtte til å kunne fjerne endret objekter etter flush/finishBatch har blitt kjørt
     *
     * @return {@code true} hvis objektet ble fjernet
     */
    boolean evictAll();

    /**
     * Oppretter objektet i sessionen. Metoden kaster exception hvis objektet allerede er knyttet til sessionen eller
     * det finnes en annen instans med samme id som er knyttet til sessionen.
     * <p/>
     * Dersom som objektets id er null så tildeles objektet automatisk en ny id via kall til
     * {@link no.statkart.skif.service.sequence.IdService}
     */
    <T extends BubbleObject> void insert(T bubbleObject);

    /**
     * Oppdaterer objektet i sessionen. Dersom det allerede finnes en annen instans knyttet til sessionen med samme
     * id vil dette objektet bli erstattet og markert som utdatert slik at denne instansen ikke kan brukes i senere kall
     * mot sessionen. Metoden støtter endring av objektets subtype samt oppdatering av objekter med skjulte felter.
     */
    <T extends BubbleObject> void update(T bubbleObject);

    /**
     * Markerer objektet som slettet i sessionen. Metoden kaster exception hvis objektet ikke finnes eller allerede har blitt
     * markert for sletting. Dersom det allerede finnes en annen instans knyttet til sessionen med samme
     * id vil dette objektet bli erstattet og makert som utdatert slik at denne instansen ikke kan brukes i senere
     * kall mot sessionen.
     */
    <T extends BubbleObject> void delete(T bubbleObject);

    /**
     * Markerer objektet som uendret i sessionen.
     *
     * @since 2.2.0
     */
    <T extends BubbleObject> void undo(T bubbleObject);

    /**
     * Endre på objektets oppdateringsrekkefølge i sessionen slik at objektet kommer etter alle andre objekter
     * med samme sorteringsindex i sessionen.  Metoden kaster en exception hvis objektet ikke er endret i sessionen
     * eller hvis sessionen ikke er en unit of work
     */
    <I extends BubbleId<?>> void reorderModification(I bubbleId);


    <T extends BubbleObject> T lock(BubbleId<? extends T> bubbleId);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds);

    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds);

    <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <I extends BubbleId<?>> void unlock(I bubbleId);

    void unlock(Collection<? extends BubbleId<?>> bubbleIds);

    <I extends BubbleId<?>> boolean isLocked(I bubbleId);

    <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject);

    void register(Transfer<?> transfer);

    /**
     * Henter ut en transfer med objekter som modifisert av inneværende eller av en underliggende session.
     * @return en transfer med insert, updated og deleted objekter. Hvis et objekt er modifisert
     * både av inneværende og av en underliggende session gjelder følgende regler:
     * <ul>
     *     <li>Hvis en underliggende session har gjort en insert og inneværende session har gjort en
     *     update så vil objektet ligge i {@code inserted}.</li>
     *     <li>Hvis en underliggende session har gjort en insert og inneværende session har gjort en
     *     delete så vil objektet ikke ligge i transferen.</li>
     *     <li>Hvis en underliggende session har gjort en update og inneværende session har gjort en
     *     delete så vil objektet ligge i {@code deleted}.</li>
     *     <li>Hvis en underliggende session har gjort en delete og inneværende session har gjort en
     *     insert så vil objektet ligge i {@code updated}</li>
     * </ul>
     */
    UnitOfWorkTransfer getSnapshot();

    /**
     * Henter ut en transfer med objekter som eksplisitt er modifisert i inneværende session uten å ta
     * med objekter som bare er modifisert av underliggende sessioner. Hvis et objekt er modifisert
     * både av inneværende og av en underliggende session gjelder følgende regler:
     * <ul>
     *     <li>Hvis en underliggende session har gjort en insert og inneværende session har gjort en
     *     update så vil objektet ligge i {@code updated}.</li>
     *     <li>Hvis en underliggende session har gjort en insert eller update og inneværende session har gjort en
     *     delete så vil objektet ligge i {@code deleted}.</li>
     *     <li>Hvis en underliggende session har gjort en delete og inneværende session har gjort en
     *     insert så vil objektet ligge i {@code inserted}</li>
     * </ul>
     *
     * @return en transfer med insert, updated og deleted objekter
     */
    UnitOfWorkTransfer getSessionSnapshot();

    /**
     * Legger alle objekter som er lastet inn i angitt transfer. Dersom en unit of work er aktiv vil objektet som
     * gis ut være original versjonen som ble lastet. Hvis ingen unit of work er aktiv gis ut gjeldende versjon
     * som vil være forskjellig fra objektet som ble lastet hvis objektet er endret. Hvis et objekt lastes og endres i
     * en unit of work som deretter abortes, så vil man etterpå få ut original objektet som ble lastet da dette fortsatt
     * vil være cachet i Store.
     * @return transfer med alle lastede objekter
     */
    public <T extends Transfer<?>> T getAllLoaded(T transfer);
}
