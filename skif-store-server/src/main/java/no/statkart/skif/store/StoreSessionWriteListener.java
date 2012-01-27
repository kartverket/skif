package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionWriteListener {
    <T extends BubbleObject> T onInsert(T storeBubbleObject, T persistentBubbleObject);
    <T extends BubbleObject> T onUpdate(T storeBubbleObject, T persistentBubbleObject);
    <T extends BubbleObject> T onDelete(T storeBubbleObject, T persistentBubbleObject);

}
