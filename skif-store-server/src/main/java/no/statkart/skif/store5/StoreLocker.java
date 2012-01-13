package no.statkart.skif.store5;

import no.statkart.skif.store.BubbleId;

import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface StoreLocker {
    /**
     * Unlocks a previously locked object. If the object has been locked in the current transaction it
     * is unlocked immediately. If the object has been locked in a previous transaction it is unlocked
     * on commit. Objects that have been modified cannot be unlocked.
     */
    public abstract void unlock(BubbleId id);

    /**
     * Unlocks previously locked objects. If the object has been locked in the current transaction it
     * is unlocked immediately. If the object has been locked in a previous transaction it is unlocked
     * on commit. Objects that have been modified cannot be unlocked.
     */
    public abstract void unlock(Set ids);

    public abstract boolean isLockedByCaller(BubbleId bubbleId);

    /**
     * Causes the StoreLocker to rollback all lock modifications when {@link #close} is
     * called.
     */
    public abstract void setRollbackOnly();

    /**
     * Causes the StoreLocker to either commit or rollback all lock modifications based on
     * whether {@link @setRollbackOnly} has been called.
     */
    public abstract void close();

    public abstract void consumeLocks();

}
