package no.statkart.skif.store;

public class StoreBubbleTransfer extends BubbleTransfer<Void> {
    static final long serialVersionUID = 1L;
    public StoreBubbleTransfer() {
        super(null);
    }

    public StoreBubbleTransfer(Void result) {
        super(result);
    }

    public StoreBubbleTransfer(Void result, Iterable<? extends BubbleObject> objects) {
        super(result, objects);
    }

    public StoreBubbleTransfer(Void result, Iterable<? extends BubbleObject> objects, Iterable<? extends BubbleId> lockedIds) {
        super(result, objects, lockedIds);
    }
}
