package no.statkart.skif.store;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public interface Store {
    /**
     * Resetter hele Store.
     * <p/>
     * TODO: Forklar forskjellen mellom denne og {@link #evictAll()}.
     */
    void clear();

    /**
     * Henter objektet for gitt id. Dersom id er <code>null</code> så returneres også <code>null</code>.
     *
     * @param bubbleId id til objektet man ønsker å hente ut
     * @param <T>      typen til objektet man ønsker å hente ut
     * @return objektet
     * @throws no.statkart.skif.exception.ObjectNotFoundException
     *          dersom objektet ikke fins
     */
    <T extends BubbleObject> T get(@Nullable BubbleId<? extends T> bubbleId);

    /**
     * Henter mange objekter for gitte id-er. Returnerer {@link Set} eller {@link List} avhengig av hva man sendte inn.
     * Rekkefølgene objektene returneres i er tilfeldig.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i tilfeldig rekkefølge
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er tilfeldig.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i tilfeldig rekkefølge
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er "tilfeldig".
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i tilfeldig rekkefølge
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er tilfeldig.
     *
     * @param bubbleIds     id-ene til objektene man ønsker å hente ut
     * @param bubbleObjects et sted å putte objektene for returnering
     * @param <T>           den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>           den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);


    /**
     * Henter mange objekter for gitte id-er. Returnerer {@link Set} eller {@link List} avhengig av hva man sendte inn.
     * Rekkefølgene objektene returneres i er den samme som for id-ene man sendte inn. Dette koster litt mer enn
     * {@link #get(java.util.Collection)}.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i samme rekkefølge som id-ene
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er den samme som for id-ene man sendte
     * inn. For at dette skal gi mening, bør id-ene ligge i {@link java.util.LinkedHashSet}. Dette koster litt mer enn
     * {@link #get(java.util.Set)}.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i samme rekkefølge som id-ene
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er den samme som for id-ene man sendte
     * inn. Dette koster litt mer enn {@link #get(java.util.List)}.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i samme rekkefølge som id-ene
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er den samme som for id-ene man sendte
     * inn. Dette koster litt mer enn {@link #get(java.util.Collection, java.util.Collection)}.
     *
     * @param bubbleIds     id-ene til objektene man ønsker å hente ut
     * @param bubbleObjects et sted å putte objektene for returnering
     * @param <T>           den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>           den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);


    /**
     * Henter mange objekter for gitte id-er. Returnerer {@link Set} eller {@link List} avhengig av hva man sendte inn.
     * Rekkefølgene objektene returneres i er tilfeldig, og eventuelt manglende objekter blir ikke rapportert.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene som fins, i tilfeldig rekkefølge
     * @since 2.2.0
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er tilfeldig, og eventuelt manglende
     * objekter blir ikke rapportert.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i tilfeldig rekkefølge
     * @since 2.2.0
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er "tilfeldig", og eventuelt manglende
     * objekter blir ikke rapportert.
     *
     * @param bubbleIds id-ene til objektene man ønsker å hente ut
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @return objektene, i tilfeldig rekkefølge
     * @since 2.2.0
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<I> bubbleIds);

    /**
     * Henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er tilfeldig, og eventuelt manglende
     * objekter blir ikke rapportert.
     *
     * @param bubbleIds     id-ene til objektene man ønsker å hente ut
     * @param bubbleObjects et sted å putte objektene for returnering
     * @param <T>           den supertypen som er felles for alle objektene man ønsker å hente ut
     * @param <I>           den id-supertypen som er felles for alle objektene man ønsker å hente ut
     * @since 2.2.0
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Låser og henter objektet for gitt id. Dersom id er <code>null</code> så returneres også <code>null</code>, uten
     * at noe låses.
     * <p/>
     * Det er viktig at det er objektet som returneres her som en det man gjør endringer på for oppdatering, da andre
     * utgaver er potensielt utdatert cachede utgaver.
     *
     * @param bubbleId id til objektet man ønsker å låse
     * @param <T>      typen til objektet man ønsker å låse
     * @return objektet
     * @throws no.statkart.skif.exception.ObjectNotFoundException
     *          dersom objektet ikke fins
     * @throws no.statkart.skif.exception.LockedException
     *          dersom noen andre har låst objektet
     */
    <T extends BubbleObject> T lock(@Nullable BubbleId<? extends T> bubbleId);

    /**
     * Låser og henter mange objekter for gitte id-er. Returnerer {@link Set} eller {@link List} avhengig av hva man sendte inn.
     * Rekkefølgene objektene returneres i er tilfeldig.
     * <p/>
     * Det er viktig at det er objektene som returneres her som en de man gjør endringer på for oppdatering, da andre
     * utgaver er potensielt utdatert cachede utgaver.
     *
     * @param bubbleIds id-ene til objektene man ønsker å låse
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å låse
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å låse
     * @return objektene, i tilfeldig rekkefølge
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     * @throws no.statkart.skif.exception.LockedException
     *          dersom noen andre har låst objektet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds);

    /**
     * Låser og henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er tilfeldig.
     * <p/>
     * Det er viktig at det er objektene som returneres her som en de man gjør endringer på for oppdatering, da andre
     * utgaver er potensielt utdatert cachede utgaver.
     *
     * @param bubbleIds id-ene til objektene man ønsker å låse
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å låse
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å låse
     * @return objektene, i tilfeldig rekkefølge
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     * @throws no.statkart.skif.exception.LockedException
     *          dersom noen andre har låst objektet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds);

    /**
     * Låser og henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er "tilfeldig".
     * <p/>
     * Det er viktig at det er objektene som returneres her som en de man gjør endringer på for oppdatering, da andre
     * utgaver er potensielt utdatert cachede utgaver.
     *
     * @param bubbleIds id-ene til objektene man ønsker å låse
     * @param <T>       den supertypen som er felles for alle objektene man ønsker å låse
     * @param <I>       den id-supertypen som er felles for alle objektene man ønsker å låse
     * @return objektene, i tilfeldig rekkefølge
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     * @throws no.statkart.skif.exception.LockedException
     *          dersom noen andre har låst objektet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds);

    /**
     * Låser og henter mange objekter for gitte id-er. Rekkefølgene objektene returneres i er tilfeldig.
     * <p/>
     * Det er viktig at det er objektene som returneres her som en de man gjør endringer på for oppdatering, da andre
     * utgaver er potensielt utdatert cachede utgaver.
     *
     * @param bubbleIds     id-ene til objektene man ønsker å låse
     * @param bubbleObjects et sted å putte de låste objektene for returnering
     * @param <T>           den supertypen som er felles for alle objektene man ønsker å låse
     * @param <I>           den id-supertypen som er felles for alle objektene man ønsker å låse
     * @throws no.statkart.skif.exception.ObjectsNotFoundException
     *          dersom noen av objektene ikke fins
     * @throws no.statkart.skif.exception.LockedException
     *          dersom noen andre har låst objektet
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    /**
     * Sier opp låsen på et gitt objekt. Objektet må være uendret i {@link Store}.
     *
     * @param bubbleId id-en til objektet som skal låses opp
     * @param <I>      id-typen til objektet som skal låses opp
     */
    <I extends BubbleId<?>> void unlock(@Nullable I bubbleId);

    void register(BubbleTransfer transfer);

    void registerTransfer(UnitOfWorkTransfer transfer);

    /**
     * Sjekker om et objekt er låst av gjeldende bruker.
     *
     * @param bubbleId id-en til objektet som skal sjekkes
     * @param <I>      id-type til objektet som skal sjekkes
     * @return <code>true</code> hvis objektet er låst av gjeldende bruke
     */
    <I extends BubbleId<?>> boolean isLocked(I bubbleId);

    /**
     * Kaster gitt objekt ut av Store. Dette kan være nødvendig i store operasjoner for å frigi minne. Objektet må være
     * uendret.
     *
     * @param bubbleId id-en til objektet som skal kastes ut av minnet
     * @param <I>      id-typen til objektet som skal kastes ut av minnet
     * @return <code>true</code> hvis objekter faktisk ble kastet ut
     */
    <I extends BubbleId<?>> boolean evict(I bubbleId);

    /**
     * Kaster gitte objekter ut av Store. Dette kan være nødvendig i store operasjoner for å frigi minne. Objektene må
     * være uendrede.
     *
     * @param bubbleIds id-ene til objektene som skal kastes ut av minnet
     * @param <I>       den id-supertypen som er felles for objektene som skal kastes ut av minnet
     * @return noe uklart
     */
    <I extends BubbleId<?>> boolean evict(Collection<I> bubbleIds);

    /**
     * Kaster alle objekter ut av Store. Dette kan være nødvendig i store operasjoner for å frigi minne.
     *
     * @return noe uklart
     */
    boolean evictAll();


    /**
     * Finner historiske utgaver av et gitt objekt.
     *
     * @param id    id-en til objektet man skal finne historikk for
     * @param start tidligste tidspunkt man er interessert i
     * @param end   seneste tidspunkt man er interessert i
     * @param <I>   id-typen til objektet
     * @return liste med tidsspesifike id-er
     */
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);

    /**
     * Finner historiske utgaver av gitte objekter.
     *
     * @param ids   id-ene til objektene man skal finne historikk for
     * @param start tidligste tidspunkt man er interessert i
     * @param end   seneste tidspunkt man er interessert i
     * @param <I>   den id-supertypen som er felles for objektene
     * @return key er id-en man sendte inn, value er liste med tidsspesifike id-er
     */
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);

    /**
     * Legger en nyopprettet boble inn i Store. Dersom boblen ikke har fått tilordnet id, så genereres denne med
     * {@link no.statkart.skif.service.sequence.IdService}.
     *
     * @param bubbleObject boblen som skal legges inn
     * @param <T>          typen til boblen
     */
    public <T extends BubbleObject> void insert(T bubbleObject);

    /**
     * Oppdaterer en eksisterende boble. Boblen må være låst av gjeldende bruker, eller nyopprettet i denne sesjonen.
     *
     * @param bubbleObject den oppdaterte utgaven av boblen
     * @param <T>          typen til boblen
     */
    public <T extends BubbleObject> void update(T bubbleObject);

    /**
     * Sletter en eksisterende boble. Boblen må være låst av gjeldende bruker, eller nyopprettet i denne sesjonen.
     *
     * @param bubbleObject boblen som skal slettes
     * @param <T>          typen til boblen
     */
    public <T extends BubbleObject> void delete(T bubbleObject);

    /**
     * Markerer en boble som uendret. Alle modifikasjoner i gjeldende unit-of-work forkastes.
     *
     * @param bubbleObject boblen som skal rulles tilbake
     * @param <T>          typen til boblen
     */
    public <T extends BubbleObject> void undo(T bubbleObject);

    /**
     * Endrer boblens plass i oppdateringsrekkefølgen ved å flytte den til "nå".
     *
     * @param bubbleId id-en til boblen som skal omkronologiseres
     * @param <I>      id-typen til boblen
     */
    public <I extends BubbleId<?>> void reorderModification(I bubbleId);

    /**
     * Sørger for at en boble er fullstendig initialisert.
     * <p/>
     * TODO: Burde denne kun vært på StoreServer?
     *
     * @param bubbleObject boblen som skal være fullstendig initialisert når metoden returerer
     * @param <T>          typen til boblen
     */
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject);


    /**
     * Starter en unit-of-work. Disse kan nøstes tre nivåer dypt.
     */
    void beginUnitOfWork();

    /**
     * Committer gjeldende unit-of-work ned på nivået under. Nivået under blir neste gjeldende nivå.
     */
    void commitUnitOfWork();

    /**
     * Avbryter gjeldende unit-of-work og returnerer til nivået under.
     */
    void abortUnitOfWork();

    /**
     * Henter ut transfer med alle endringer fra gjeldende unit-of-work.
     *
     * @return transfer med endrede objekter
     */
    UnitOfWorkTransfer getUnitOfWorkTransfer();

    /**
     * Avslutter gjeldende unit-of-work og returnerer til nivået under. Man må ha kalt {@link #getUnitOfWorkTransfer()}
     * først for å hente ut endringene, siden de ikke overføres til nivået under.
     */
    void endUnitOfWork();

    /**
     * Sjekker om man er i en unit-of-work.
     *
     * @return <code>true</code> hvis en unit-of-work er aktiv
     */
    boolean inUnitOfWork();

    /**
     * Henter ut en service-implementasjon fra den modulen denne Store kommer fra.
     *
     * @param serviceClass service-interface-klassen
     * @param <S>          service-interface-klassen
     * @return en eller annen implementasjon av servicen
     */
    <S> S getInstance(Class<S> serviceClass);
}
