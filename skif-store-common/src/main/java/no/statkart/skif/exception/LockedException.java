package no.statkart.skif.exception;

import com.google.common.collect.ImmutableList;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * Exception for å signalisere at man forsøkte å låse én eller flere bobler som allerede var låst av noen andre.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class LockedException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    /**
     * Locks that could not be aquired
     */
    private final List<? extends LockInfo<?>> locksNotAquired;

    /**
     * Cached list of ids not aquired. Derived from {@link #locksNotAquired}
     */
    private transient List<? extends LockKey<?>> idsNotAcquired = null;

    /**
     * The principal who has this item locked
     */
    private final String lockedBy;

    /**
     * The principal who tries to lock objects
     */
    private final String owner;

    private final Timestamp exceptionTime = new Timestamp(System.currentTimeMillis());

    /**
     * Creates a lockInfo exception for a single lockInfo that could not be aquired
     *
     * @param owner Owner of Lock
     * @param lockInfo LockInfo for lock that caused the exception
     */
    public <T> LockedException(String owner, LockInfo<T> lockInfo) {
        super("");
        this.owner = owner;
        this.lockedBy = lockInfo.getOwner();
        this.locksNotAquired = ImmutableList.of(lockInfo);
    }

    /**
     * Creates a lock excpetion for a collection of lockInfos that could not be aquired. Sets {@link #lockedBy} to the
     * locker of the first lock. The lockInfos collection may have other lockers as well.
     *
     * @param owner Owner of the locks
     * @param lockInfos Collection of lockinfos that caused the exception
     */
    public <T> LockedException(String owner, Collection<LockInfo<T>> lockInfos) {
        super("");
        if (lockInfos.isEmpty()) {
            throw new ImplementationException("LockedException cannot have empty collection of lockInfos, owner=" + owner + " lockInfos=" + lockInfos);
        }
        this.owner = owner;
        locksNotAquired = ImmutableList.copyOf(lockInfos);
        LockInfo first = lockInfos.iterator().next();
        this.lockedBy = first.getOwner();
    }

    /**
     * Returns all IdLocks that could not be acquired
     *
     * @return unmodifiable list of LockInfo
     */
    public List<? extends LockInfo<?>> getLocksNotAquired() {
        return locksNotAquired;
    }

    public List<? extends LockKey<?>> getIdsNotAquired() {
        if (idsNotAcquired == null) {
            ImmutableList.Builder<LockKey<?>> listBuilder = ImmutableList.builder();
            for (LockInfo lockInfo : locksNotAquired) {
                listBuilder.add(lockInfo.getLockKey());
            }
            idsNotAcquired = listBuilder.build();
        }
        return idsNotAcquired;
    }


    /**
     * Returns the name of the principal of the first lock that could not be acquired
     */
    public String getLockedBy() {
        return lockedBy;
    }

    public String getMessage() {
        return "User " + owner + " cannot take/renew lock at " + exceptionTime + " :" + getLocksNotAquiredListString(10);
    }

    /**
     * Returnerer en kommaseparert string som viser opp til <code>maxLocks</code> av låsene i {@link #locksNotAquired} som ikke ble tatt.
     * Hvis antall låser er større enn <code>maxLocks</code> inneholder strengen siste element det totale antall låser.
     *
     * @param maxLocks maksimalt antall låser som strengen skal kunne inneholde
     * @return Returnerer en kommaseparert string som viser opp til <code>maxLocks</code> av låsene i {@link #locksNotAquired} som ikke ble tatt.
     */
    private String getLocksNotAquiredListString(int maxLocks) {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        int max = Math.min(maxLocks, locksNotAquired.size());
        for (int i = 0; i < max; i++) {
            if (i != 0) buf.append(", ");
            buf.append(locksNotAquired.get(i));
        }
        if (locksNotAquired.size() > maxLocks) {
            buf.append(", ...(").append(locksNotAquired.size()).append(" i alt)");
        }
        buf.append("]");
        return buf.toString();
    }

}
