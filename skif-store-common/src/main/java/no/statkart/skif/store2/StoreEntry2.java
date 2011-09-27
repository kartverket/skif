package no.statkart.skif.store2;

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 */
public class StoreEntry2<T extends BubbleObject2> {
    BubbleId2<?> id;
    protected T bubbleObject;
    protected StoreEntryState2 state;
    private boolean locked;

    public StoreEntry2(T bubbleObject) {
        this(bubbleObject, StoreEntryState2.UNLOCKED);
    }

    public StoreEntry2(T bubbleObject, StoreEntryState2 state) {
        this(bubbleObject.getId());
        this.bubbleObject = bubbleObject;
        this.state = state;
    }
    public StoreEntry2(BubbleId2<?> id) {
        this.id = id;
    }

    public BubbleId2<T> getId() {
        return (BubbleId2<T>) id;
    }

    public T getBubbleObject() {
        return bubbleObject;
    }

    public void setBubbleObject(T bubbleObject) {
        this.bubbleObject = bubbleObject;
    }

    public T getBubbleObjectIfLocked() {
        if (state == StoreEntryState2.UNLOCKED) {
            return null;
        } else {
            return bubbleObject;
        }
    }




    public StoreEntryState2 getState() {
        return state;
    }


    public void replaceIfExistingIsUnlockedAndOlder(StoreEntry2<T> newEntry, Store2 store, StoreEntryState2 newState) {
        if (state == StoreEntryState2.UNLOCKED) {
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

    public void replaceObjectIfUnlocked(StoreEntry2<T> newEntry, Store2 store) {
        if (state == StoreEntryState2.UNLOCKED) {
            if (bubbleObject==null || bubbleObject.getVersion() < newEntry.bubbleObject.getVersion()) {
                // Replace the original
                bubbleObject = newEntry.bubbleObject;
                bubbleObject.register(store);
            }  else {
                // Keep the original
            }
            state = StoreEntryState2.LOCKED;
        } else {
           if (bubbleObject.getVersion() < newEntry.getBubbleObject().getVersion()) {
               throw new ImplementationException("Attempt to register a bubbleObject with a higher version than the existing locked object. Call update() instead");
           } else {
               // Keep the original
           }
           // No need to change state
        }
    }


    public boolean reloadNeeded(LockMode2 lockMode) {
        if (lockMode == LockMode2.WRITE && state == StoreEntryState2.UNLOCKED) {
            return true;
        } else {
            return bubbleObject==null;
        }
    }

    public boolean isLocked() {
        return state != StoreEntryState2.UNLOCKED;
    }
}
