package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.util.CopyHelper;

/**
 *
 * @author Henrik Fredholm
 */
public class StoreEntry {
    final static int MAX_LEVELS = 4;
    BubbleId<?> id;
    protected BubbleObject persistentBubbleObject;
    protected BubbleObject[] bubbleObject = new BubbleObject[MAX_LEVELS];
    protected StoreEntryState[] state = new StoreEntryState[MAX_LEVELS];

    // TODO: Denne kan tas bort og modelleres via StoreEntryState.LOCKED_UNCHANED
    protected boolean[] locked = new boolean[MAX_LEVELS];
    protected int loadedByLevel;
    protected int lockCreatedByLevel;

    public StoreEntry(int level, BubbleObject bubbleObject, StoreEntryState state) {
        this(bubbleObject.getId());
        this.bubbleObject[level] = bubbleObject;
        this.state[level] = state;
    }

    public StoreEntry(BubbleObject bubbleObject, StoreEntryState state) {
        this(bubbleObject.getId());
        this.bubbleObject[0] = bubbleObject;
        this.state[0] = state;
    }

    public void setPersistentBubbleObject(BubbleObject bubbleObject, BubbleObject persistentBubbleObject) {
        this.persistentBubbleObject = persistentBubbleObject;
        this.bubbleObject[0] = bubbleObject;
    }

    public BubbleObject getPersistentBubbleObject() {
        return persistentBubbleObject;
    }

    public boolean hasSeparatePersistentBubbleObject() {
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

    public StoreEntry(BubbleId<?> id) {
        this.id = id;
        for (int i = 0; i < state.length; i++) {
            state[i] = StoreEntryState.NULL;

        }
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

    public void setStateAndCheckLocked(int level, StoreEntryState state) {
        if (this.locked[level] != true) {
            throw new ImplementationException("Object not locked for StoreSession level " + level + ": " + id);
        }
        this.state[level] = state;
    }

    /**
     * Beregner hvilket level eksisterende lås gjelder for startende fra {@code level}
     *
     * @param level
     * @return level som lås gjelder for eller -1 hvis ingen lås
     */
    public int calcLockLevelStartingFrom(int level) {

        while (locked[level] == false) {
            level = level - 1;
            if (level == -1) break;
        }
        return level;
    }

    /**
     * Setter level til locked og sette bubbleObject som må være dekoplet underliggende session
     *
     * @param level
     * @param bubbleObject
     */
    public void setLocked(int level, BubbleObject bubbleObject) {
        if (this.locked[level] == true) {
            throw new ImplementationException("Object already locked for StoreSession level " + level + ": " + bubbleObject.getId());
        }
        makeStale(level);
        this.bubbleObject[level] = bubbleObject;
        this.locked[level] = true;
        this.state[level] = StoreEntryState.UNCHANGED;
    }

    public void unlock(int level) {
        this.locked[level]=false;
    }


    private void makeStale(int level) {
        if (bubbleObject[level] != null) {
            // TODO implement
        }
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
            lockCreatedByLevel = level - 1;
        }
        clear(level);
    }

    public void abort(int level) {
        clear(level);
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


    public BubbleObject getDerivedBubbleObjectCopyIfLocked(int level, Store store) {
        if (bubbleObject[level]!=null) return bubbleObject[level];

        int l = getLevelForDerivedBubbleObject(level);
        if (isLocked()) {
            if (l==0) {
                store.ensureFullyLoaded(bubbleObject[0]);
            }
            BubbleObject copy = CopyHelper.copy(bubbleObject[l]);
            copy.register(store);
            bubbleObject[level] = copy;
            l = level;
        }
        return bubbleObject[l];
    }
}
