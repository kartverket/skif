package no.statkart.skif.locker;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class LockInfo<T> {

    /**
     * LockKey of locked object
     */
    private LockKey<T> lockKey;
    /**
     * Owner of the lock
     */
    private String owner;
    /**
     * When the lock expires and may be taken by other users
     */
    private Timestamp expires;

    /**
     * True if the lock must released on transaction rollback
     */
    private boolean isNew;

    public LockInfo(LockKey<T> lockKey, String owner) {
        this.lockKey = lockKey;
        this.owner = owner;
    }

    public LockInfo(LockKey<T> lockKey, String owner, Timestamp expires, boolean isNew) {
        this.expires = expires;
        this.lockKey = lockKey;
        this.isNew = isNew;
        this.owner = owner;
    }

    public LockKey<T> getLockKey() {
        return lockKey;
    }

    public void setLockKey(LockKey<T> lockKey) {
        this.lockKey = lockKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isOwnedBy(String key) {
        return this.owner.equals(key);
    }

    public boolean expired() {
        return System.currentTimeMillis() > expires.getTime();
    }

    public boolean expiresBefore(long milliseconds) {
        return System.currentTimeMillis() + milliseconds > expires.getTime();
    }

    public Timestamp getExpires() {
        return expires;
    }

    public boolean isNew() {
        return isNew;
    }

    @Override
    public String toString() {
        return "LockInfo{" +
                "lockKey=" + lockKey +
                ", owner='" + owner +
                ", expires=" + expires +
                ", isNew=" + isNew +
                '}';
    }
}
