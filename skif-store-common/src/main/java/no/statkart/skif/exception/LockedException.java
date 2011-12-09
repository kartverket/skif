package no.statkart.skif.exception;

import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;

import java.sql.Timestamp;
import java.util.*;

/**
 *
 */
public class LockedException extends OperationalException {

    /**
     * Locks that could not be aquired
     */
    private List<LockInfo> locksNotAquired = new ArrayList<LockInfo>();

    /**
     * Cached list of ids not aquired. Derived from {@link #locksNotAquired}
     */
    private transient List<LockKey> idsNotAcquired = null;

    /**
     * The principal who has this item locked
     */
    private String lockedBy;

    /**
     * The principal who tries to lock objects
     */
    private String owner;

    private Timestamp exceptionTime = new Timestamp(System.currentTimeMillis());

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
        this.locksNotAquired.add(lockInfo);
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
        locksNotAquired.addAll(lockInfos);
        LockInfo first = lockInfos.iterator().next();
        this.lockedBy = first.getOwner();
    }

    /**
     * Returns all IdLocks that could not be acquired
     *
     * @return unmodifiable list of LockInfo
     */
    public List<LockInfo> getLocksNotAquired() {
        return Collections.unmodifiableList(locksNotAquired);
    }

    public List<LockKey> getIdsNotAquired() {
        if (idsNotAcquired == null) {
            idsNotAcquired = new ArrayList<LockKey>(locksNotAquired.size());
            for (LockInfo lockInfo : locksNotAquired) {
                idsNotAcquired.add(lockInfo.getLockKey());
            }
        }
        return Collections.unmodifiableList(idsNotAcquired);
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
            LockInfo lockInfo = locksNotAquired.get(i);
            if (i != 0) buf.append(", ");
            buf.append(locksNotAquired.get(i));
        }
        if (locksNotAquired.size() > maxLocks) {
            buf.append(", ...(" + locksNotAquired.size() + " i alt)");
        }
        buf.append("]");
        return buf.toString();
    }

}
