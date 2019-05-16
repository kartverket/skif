package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

/**
 *
 * @author Henrik Fredholm
 */
public class StoreEntry {
    public final static int MAX_LEVELS = 10;
    BubbleId<?> id;
    protected BubbleObject persistentBubbleObject;
    protected BubbleObject[] bubbleObject = new BubbleObject[MAX_LEVELS];
    protected StoreEntryState[] state = new StoreEntryState[MAX_LEVELS];

    protected boolean[] locked = new boolean[MAX_LEVELS]; // Denne kan tas bort og modelleres via StoreEntryState.LOCKED_UNCHANGED
    protected int loadedByLevel;
    protected int lockCreatedByLevel;

    public StoreEntry(BubbleId<?> id) {
        this.id = id;
        for (int i = 0; i < state.length; i++) {
            state[i] = StoreEntryState.NULL;
        }
    }

    public void setPersistentBubbleObject(BubbleObject bubbleObject, BubbleObject persistentBubbleObject) {
        this.persistentBubbleObject = persistentBubbleObject;
        this.bubbleObject[0] = bubbleObject;
    }

    public BubbleObject getPersistentBubbleObject() {
        return persistentBubbleObject;
    }

    public boolean isLevel0PersistentBubbleObject() {
        return persistentBubbleObject==this.bubbleObject[0];
    }

    public int getLoadedByLevel() {
        return loadedByLevel;
    }

    public void setLoadedByLevel(int loadedByLevel) {
        this.loadedByLevel = loadedByLevel;
    }

    public int getLockCreatedByLevel() {
        return lockCreatedByLevel;
    }

    public void setLockCreatedByLevel(int lockCreatedByLevel) {
        this.lockCreatedByLevel = lockCreatedByLevel;
    }

    public BubbleId<?> getId() {
        return id;
    }

    public BubbleObject getBubbleObject(int level) {
        return bubbleObject[level];
    }

    public void setBubbleObject(int level, BubbleObject bubbleObject) {
        this.bubbleObject[level] = bubbleObject;
    }

    public BubbleObject getDerivedBubbleObject(int level) {
        while (level>0 && bubbleObject[level] == null) level--;
        return bubbleObject[level];
    }

    public StoreEntryState getState(int level) {
        return state[level];
    }

    public void setState(int level, StoreEntryState state) {
        this.state[level] = state;
    }

    public StoreEntryState getDerivedState(int level) {
        while (level>0 && state[level] == StoreEntryState.NULL) level--;
        return state[level];
    }

    /**
     * Beregner hvilket level eksisterende lås gjelder for startende fra {@code level}
     *
     * @return level som lås gjelder for eller -1 hvis ingen lås
     */
    public int calcLockLevelStartingFrom(int level) {
        while (!locked[level]) {
            level = level - 1;
            if (level == -1) break;
        }
        return level;
    }

    /**
     * Setter level til locked og setter bubbleObject som må være dekoplet underliggende session
     *
     */
    public void setLocked(int level, BubbleObject bubbleObject) {
        if (this.locked[level]) {
            throw new ImplementationException("Object already locked for StoreSession level " + level + ": " + bubbleObject.getId());
        }
        this.bubbleObject[level] = bubbleObject;
        this.locked[level] = true;
        this.state[level] = StoreEntryState.UNCHANGED;
    }

    public void unlock(int level) {
        this.locked[level]=false;
    }

    public void setLocked(int level) {
        if (this.bubbleObject[level] == null) {
            throw new ImplementationException("Object does not exist in StoreSession level " + level + ": " + id);
        }
//        if (this.locked[level] == true) {
//            throw new ImplementationException("Object already locked for StoreSession level " + level + ": " + id);
//        }
        this.locked[level] = true;
    }

    public void setLockedCreatedByLevel(int level) {
        this.lockCreatedByLevel= level;
    }

    public void checkNotDerivedInstance(int level, BubbleObject bubbleObject) {
        for (int l = level - 1; l >= 0; l--) {
            if (bubbleObject == this.bubbleObject[l]) {
                throw new ImplementationException("Attempt at updating StoreSession(level= " + level + ") with instance from lower StoreSession(level=" + l + ") for id:" + id);
            }
        }
    }

    public void commit(int level) {
        locked[level - 1] |= locked[level];
        if (lockCreatedByLevel==level) {
            lockCreatedByLevel = level - 1;
        }
        if (loadedByLevel==level) {
            loadedByLevel = level - 1;
        }
        clear(level);
    }

    public boolean abort(int level) {
        // Hvis objektet var inserted i dette level kan entry fjernes
        boolean removeEntry = state[level] == StoreEntryState.INSERTED || state[level] == StoreEntryState.INSERTED_DELETED;
        state[level] = StoreEntryState.NULL;
        bubbleObject[level] = null;
        // Kan ikke nullstille locked, da opplåsing av objekt er en egen ting som skjer etter at denne metoden blir kalt
        return removeEntry;
    }

    public void clear(int level) {
        state[level] = StoreEntryState.NULL;
        bubbleObject[level] = null;
        locked [level] = false;
    }

    public boolean isModified() {
        boolean isModified = false;
        for (StoreEntryState storeEntryState : state) {
           isModified |= storeEntryState.ordinal() > StoreEntryState.UNCHANGED.ordinal();

        }
        return isModified;
    }

    public boolean isLockedByLevel(int level) {
        return lockCreatedByLevel == level;
    }

    public boolean isLocked() {
        boolean isLocked = false;
        for (boolean l : locked) {
           isLocked |= l;

        }
        return isLocked;
    }
    public int getLevelForDerivedBubbleObject(int level) {
        while (level>0 && bubbleObject[level] == null) level--;
        return level;
    }
}
