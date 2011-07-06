package no.statkart.skif.exception;

import no.statkart.skif.store.BubbleLock;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.util.*;

/**
 * TODO: Designet av denne klasse bør revurderes. Er det riktig at den bruker Bubblelock eller skal den være mer generell og ikke avhenge av store pakken. Er det noen problemer mht xml mapping. Skal den ligge i pakken store.exception istedet
 */
public class LockedException extends OperationalException {

    /**
     * Locks that could not be aquired
     */
    private List locksNotAquired = new ArrayList();

    /**
     * Cached list of ids not aquired. Derived from {@link #locksNotAquired}
     */
    private transient List idsNotAcquired = null;

    /**
     * The principal who has this item locked
     */
    private String lockedBy;

    /**
     * The principal who tries to lock objects
     */
    private String key;

    private Timestamp exceptionTime = new Timestamp(System.currentTimeMillis());

    /**
     * Creates a lock exception for a single lock that could not be aquired
     *
     * @param lock
     */
    public LockedException(String key, BubbleLock lock) {
        super("");
        this.key = key;
        this.lockedBy = lock.getKey();
        this.locksNotAquired.add(lock);
    }

    /**
     * Creates a lock excpetion for a collection of locks that could not be aquired. Sets {@link #lockedBy} to the
     * locker of the first lock. The locks collection may have other lockers as well.
     *
     * @param locks
     */
    public LockedException(String key, Collection locks) {
        super("");
        if (locks.isEmpty()) {
            throw new ImplementationException("LockedException cannot have empty collection of locks, key=" + key + " locks=" + locks);
        }
        this.key = key;
        locksNotAquired.addAll(locks);
        BubbleLock first = (BubbleLock) locks.iterator().next();
        this.lockedBy = first.getKey();
    }

    /**
     * Returns all BubbleLocks that could not be acquired
     *
     * @return unmodifiable list of BubbleLock
     */
    public List getLocksNotAquired() {
        return Collections.unmodifiableList(locksNotAquired);
    }

    public List getIdsNotAquired() {
        if (idsNotAcquired == null) {
            idsNotAcquired = new ArrayList(locksNotAquired.size());
            for (Iterator iterator = locksNotAquired.iterator(); iterator.hasNext();) {
                BubbleLock bubbleLock = (BubbleLock) iterator.next();
                idsNotAcquired.add(bubbleLock.getId());
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
        return "User " + key + " cannot take/renew lock at " + exceptionTime + " :" + getLocksNotAquiredListString(10);
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
            BubbleLock bubbleLock = (BubbleLock) locksNotAquired.get(i);
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
