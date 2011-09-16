package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreEntry<T extends BubbleObject> {
    BubbleId<?> id;
    protected T bubbleObject;
    protected StoreEntryState state;
    private boolean locked;

    public StoreEntry(T bubbleObject) {
        this(bubbleObject, StoreEntryState.UNLOCKED);
    }

    public StoreEntry(T bubbleObject, StoreEntryState state) {
        this(bubbleObject.getId());
        this.bubbleObject = bubbleObject;
        this.state = state;
    }
    public StoreEntry(BubbleId<?> id) {
        this.id = id;
    }

    public BubbleId<T> getId() {
        return (BubbleId<T>) id;
    }

    public T getBubbleObject() {
        return bubbleObject;
    }

    public void setBubbleObject(T bubbleObject) {
        this.bubbleObject = bubbleObject;
    }

    public T getBubbleObjectIfLocked() {
        if (state == StoreEntryState.UNLOCKED) {
            return null;
        } else {
            return bubbleObject;
        }
    }




    public StoreEntryState getState() {
        return state;
    }


    public void replaceIfExistingIsUnlockedAndOlder(StoreEntry<T> newEntry, Store store, StoreEntryState newState) {
        if (state == StoreEntryState.UNLOCKED) {
            if (bubbleObject==null || bubbleObject.getVersion() < newEntry.bubbleObject.getVersion()) {
                // Replace the original
                bubbleObject = newEntry.bubbleObject;
                bubbleObject.register(store);
            }  else {
                // Keep the original
            }
            state = newState;
        } else {
           if (bubbleObject.getVersion() < newEntry.getBubbleObject().getVersion()) {
               throw new ImplementationException("Attempt to register a bubbleObject with a higher version than the existing locked object. Call update() instead");
           } else {
               // Keep the original
           }
           // No need to change state 
        }
    }

    public void replaceObjectIfUnlocked(StoreEntry<T> newEntry, Store store) {
        if (state == StoreEntryState.UNLOCKED) {
            if (bubbleObject==null || bubbleObject.getVersion() < newEntry.bubbleObject.getVersion()) {
                // Replace the original
                bubbleObject = newEntry.bubbleObject;
                bubbleObject.register(store);
            }  else {
                // Keep the original
            }
            state = StoreEntryState.LOCKED;
        } else {
           if (bubbleObject.getVersion() < newEntry.getBubbleObject().getVersion()) {
               throw new ImplementationException("Attempt to register a bubbleObject with a higher version than the existing locked object. Call update() instead");
           } else {
               // Keep the original
           }
           // No need to change state
        }
    }


    public boolean reloadNeeded(LockMode lockMode) {
        if (lockMode == LockMode.WRITE && state == StoreEntryState.UNLOCKED) {
            return true;
        } else {
            return bubbleObject==null;
        }
    }

    public boolean isLocked() {
        return state != StoreEntryState.UNLOCKED;
    }
}
