package no.statkart.skif.store;

import no.statkart.skif.exception.LockedException;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface LockerStrategy {

    /**
     * Låser id for owner dersom dette er mulig.
     *
     * @param id    Id som skal låses
     * @return true Dersom lås er tatt og er ny, false dersom lås er tatt men er gammel
     * @throws no.statkart.skif.exception.LockedException
     *          Dersom element er låst av annen bruker
     */
    boolean lock(BubbleId id) throws LockedException;

    /**
     * Låser id-er for owner dersom dette er mulig.
     *
     * @param ids    Id-er som skal låses
     * @return id-er hvor det ble tatt nye låser
     * @throws no.statkart.skif.exception.LockedException
     *          Dersom element er låst av annen bruker
     */
    Set<BubbleId> lock(Set<BubbleId> ids) throws LockedException;

    /**
     * Låser opp gjeldende id dersom denne kan låses opp. Nye elementer og endrede/slettede elementer kan ikke låses opp.
     * Elementer som er låst i nåværende transaksjon vil bli forsøkt låst opp direkte, mens elementer som har blitt låst
     * tidligere vil bli lagt i en liste som skal låses opp ved commit av denne transaksjonen. (Låses opp ved kall til
     * {@link #consumeAllLocks()} eller {@link #releaseLocksOnNonTransactionalScopeCompletion()}.)
     *
     * @param id    Id som skal låses opp
     */
    void unlock(BubbleId id);

    /**
     * Låser opp gjeldende id-er dersom disse kan låses opp. Nye elementer og endrede/slettede elementer kan ikke låses opp.
     * Elementer som er låst i nåværende transaksjon vil bli forsøkt låst opp direkte, mens elementer som har blitt låst
     * tidligere vil bli lagt i en liste som skal låses opp ved commit av denne transaksjonen. (Låses opp ved kall til
     * {@link #consumeAllLocks()} eller {@link #releaseLocksOnNonTransactionalScopeCompletion()}.)
     *
     * @param ids    Id-er som skal låses opp
     */
    void unlock(Set<BubbleId> ids);

    /**
     * Sjekker om id er låst av owner.
     *
     * @param id    Id som skal sjekkes
     * @return true dersom owner har en lås på id
     */
    boolean isLockedByCaller(BubbleId id);

    /**
     * Sjekker om id er låst av en annen bruker enn owner.
     *
     * @param id    Id som skal sjekkes
     * @return true dersom det finnes en lås på id, og eier av låsen ikke er owner
     */
    boolean isLockedByOther(BubbleId id);

    /**
     * Slipper alle låser for owner der objekter ikke er modifisert.
     */
    void releaseAllLocks();

    /**
     * Slipper alle låser for owner som er tatt i denne transaksjonen. Rører ikke låser som owner eier fra andre transaksjoner.
     */
    void releaseLocksOnRollback();

    /**
     * Tømmer innhold i strategy-klassen
     */
    void clear();

    /**
     * Registrer en insert i transaksjonen. Brukes for å bestemme om elementet kan tas låser på/kan låses opp.
     *
     * @param id Id som skal registreres
     */
    void registerInserted(BubbleId id);

    /**
     * Registrer en update i transaksjonen. Brukes for å holde rede på elementer som ikke kan låses opp. Vil feile dersom
     * owner ikke holder en lås på id.
     *
     * @param id    Id som skal registreres
     */
    void registerUpdated(BubbleId id);

    /**
     * Registrer en remove i transaksjonen. Brukes for å holde rede på elementer som ikke kan låses opp. Vil feile dersom
     * owner ikke holder en lås på id.
     *
     * @param id    Id som skal registreres
     */
    void registerRemoved(BubbleId id);

    /**
     * Låser opp alle brukerens låser i transaksjonen og sjekker at antallet stemmer.
     *
     * @throws no.statkart.skif.exception.OperationalException
     *          dersom antall låser som ble låst opp avviker fra det som er forventet
     */
    void consumeAllLocks();

    /**
     * Låser opp de låsene brukeren har kalt unlock på i løpet av et scope, men som var låst fra før.
     */
    void releaseLocksOnNonTransactionalScopeCompletion();

}
