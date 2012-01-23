package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreEntry {
    final int MAX_LEVELS = 4;
    BubbleId<?> id;
    protected BubbleObject[] bubbleObject = new BubbleObject[MAX_LEVELS];
    protected StoreEntryState[] state = new StoreEntryState[MAX_LEVELS];
    protected boolean[] locked = new boolean[MAX_LEVELS];
    protected boolean[] lockedByLevel = new boolean[MAX_LEVELS];

    public StoreEntry(BubbleObject bubbleObject) {
        this(bubbleObject, StoreEntryState.UNCHANGED);
    }

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

    public StoreEntry(BubbleId<?> id) {
        this.id = id;
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
        while (bubbleObject[level] == null) level--;
        return bubbleObject[level];
    }

    public StoreEntryState getState(int level) {
        return state[level];
    }

    public void setState(int level, StoreEntryState state) {
        this.state[level] = state;
    }

    public StoreEntryState getDerivedState(int level) {
        while (state[level] == null) level--;
        return state[level];
    }

    public void setStateCheckLocked(int level, StoreEntryState state) {
        if (this.locked[level]!= true) {
            throw new ImplementationException("Objekt har ikke blitt låst for StoreSession Level " + level  +": " + id);
        }
        this.state[level] = state;
    }


//    public T getBubbleObjectIfLocked() {
//        if (state == StoreEntryState.UNLOCKED) {
//            return null;
//        } else {
//            return bubbleObject;
//        }
//    }


//    public void replaceIfExistingIsUnlockedAndOlder(StoreEntry<T> newEntry, Store store, StoreEntryState newState) {
//        if (state == StoreEntryState.UNLOCKED) {
//            if (bubbleObject==null || bubbleObject.getVersion() < newEntry.bubbleObject.getVersion()) {
//                // Replace the original
//                bubbleObject = newEntry.bubbleObject;
//                bubbleObject.register(store);
//            }  else {
//                // Keep the original
//            }
//            state = newState;
//        } else {
//           if (bubbleObject.getVersion() < newEntry.getBubbleObject().getVersion()) {
//               throw new ImplementationException("Attempt to register a bubbleObject with a higher version than the existing locked object. Call update() instead");
//           } else {
//               // Keep the original
//           }
//           // No need to change state
//        }
//    }

//    public void replaceObjectIfUnlocked(StoreEntry<T> newEntry, Store store) {
//        if (state == StoreEntryState.UNLOCKED) {
//            if (bubbleObject==null || bubbleObject.getVersion() < newEntry.bubbleObject.getVersion()) {
//                // Replace the original
//                bubbleObject = newEntry.bubbleObject;
//                bubbleObject.register(store);
//            }  else {
//                // Keep the original
//            }
//            state = StoreEntryState.LOCKED;
//        } else {
//           if (bubbleObject.getVersion() < newEntry.getBubbleObject().getVersion()) {
//               throw new ImplementationException("Attempt to register a bubbleObject with a higher version than the existing locked object. Call update() instead");
//           } else {
//               // Keep the original
//           }
//           // No need to change state
//        }
//    }
//
//
//    public boolean reloadNeeded(LockMode lockMode) {
//        if (lockMode == LockMode.WRITE && state == StoreEntryState.UNLOCKED) {
//            return true;
//        } else {
//            return bubbleObject==null;
//        }
//    }
//
//    public boolean isLocked() {
//        return state != StoreEntryState.UNLOCKED;
//    }

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
     * @param level
     * @param bubbleObject
     */
    public void setLocked(int level, BubbleObject bubbleObject) {
        if (this.locked[level]== true) {
            throw new ImplementationException("Objekt er allerede låst for StoreSession Level " + level  +": " + bubbleObject.getId());
        }
        makeStale(level);
        this.bubbleObject[level] = bubbleObject;
        this.locked[level] = true;
    }

    private void makeStale(int level) {
        if (bubbleObject[level]!=null) {
            // TODO implement
        }
    }

    public void setLocked() {
        if (this.bubbleObject[0]== null) {
            throw new ImplementationException("Objekt er ikke satt for StoreSession Level " + 0  +": " + id);
        }
        if (this.locked[0]== true) {
            throw new ImplementationException("Objekt er allerede låst for StoreSession Level " + 0  +": " + id);
        }
        this.locked[0] = true;
    }

    public void checkNotDerivedInstance(int level, BubbleObject bubbleObject) {
        for(int l = level-1; l>=0; l-- ) {
            if (bubbleObject == this.bubbleObject[level]) {
                throw new ImplementationException("Forsøk på å oppdaterer StoreSession(level= "+level+") med instans fra underliggende StoreSession(level="+ l +") for id:" + id);
            }
        }
    }
}
