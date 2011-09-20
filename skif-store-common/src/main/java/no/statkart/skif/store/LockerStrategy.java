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

    void unlock(BubbleId id, String owner);

    public boolean isLockedBy(BubbleId id, String owner);

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

    public void clear();

    public void registerInserted(BubbleId id);

    public void registerUpdated(BubbleId id, String owner);

    public void registerRemoved(BubbleId id, String owner);
}
