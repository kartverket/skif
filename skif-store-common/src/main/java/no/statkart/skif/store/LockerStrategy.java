package no.statkart.skif.store;

import no.statkart.skif.exception.LockedException;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface LockerStrategy {

    /**
     * Låser id for owner dersom dette er mulig.
     *
     * @param id    Id som skal låses
     * @param owner Bruker id skal låses for
     * @return true Dersom lås er tatt og er ny, false dersom lås er tatt men er gammel
     * @throws LockedException Dersom element er låst av annen bruker
     */
    public boolean lock(BubbleId id, String owner) throws LockedException;

    /**
     * Låser opp gjeldende id dersom denne kan låses opp. Nye elementer og endrede/slettede elementer kan ikke låses opp.
     * Elementer som er låst i nåværende transaksjon vil bli forsøkt låst opp direkte, mens elementer som har blitt låst
     * tidligere vil bli lagt i en liste som skal låses opp ved commit av denne transaksjonen. (Låses opp ved kall til
     * {@link #releaseAllLocksOnCommit(String)})
     *
     * @param id    Id som skal låses opp
     * @param owner Bruker man skal låse opp for
     */
    void unlock(BubbleId id, String owner);

    /**
     * Sjekker om id er låst av owner.
     *
     * @param id    Id som skal sjekkes
     * @param owner Bruker lås skal sjekkes for
     * @return true dersom owner har en lås på id
     */
    public boolean isLockedBy(BubbleId id, String owner);

    /**
     * Sjekker om id er låst av en annen bruker enn owner
     *
     * @param id    Id som skal sjekkes
     * @param owner Bruker man skal sjekke for
     * @return true dersom det finnes en lås på id, og eier av låsen ikke er owner
     */
    public boolean isLockedByOther(BubbleId id, String owner);

    /**
     * Slipper alle låser for owner der objekter ikke er modifisert
     *
     * @param owner Bruker som eier låser som skal låses opp
     */
    public void releaseAllLocks(String owner);

    /**
     * Slipper alle låser for owner, inkludert låser ikke tatt i denne transaksjonen
     *
     * @param owner Bruker som eier låser som skal låses opp
     */
    public void releaseAllLocksOnCommit(String owner);

    /**
     * Slipper alle låser for owner som er tatt i denne transaksjonen. Rører ikke låser som owner eier fra andre transaksjoner
     *
     * @param owner Bruker som eier låser som skal låses opp
     */
    public void releaseAllLocksOnRollback(String owner);

    /**
     * Tømmer innhold i strategy-klassen
     */
    public void clear();

    /**
     * Registrer en insert i transaksjonen. Brukes for å bestemme om elementet kan tas låser på/kan låses opp
     *
     * @param id Id som skal registreres
     */
    public void registerInserted(BubbleId id);

    /**
     * Registrer en update i transaksjonen. Brukes for å holde rede på elementer som ikke kan låses opp. Vil feile dersom
     * owner ikke holder en lås på id
     *
     * @param id    Id som skal registreres
     * @param owner Bruker id skal registreres for
     */
    public void registerUpdated(BubbleId id, String owner);

    /**
     * Registrer en remove i transaksjonen. Brukes for å holde rede på elementer som ikke kan låses opp. Vil feile dersom
     * owner ikke holder en lås på id
     *
     * @param id    Id som skal registreres
     * @param owner Bruker id skal registreres for
     */
    public void registerRemoved(BubbleId id, String owner);
}
