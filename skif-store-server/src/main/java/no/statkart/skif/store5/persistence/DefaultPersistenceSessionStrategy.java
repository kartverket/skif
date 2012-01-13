package no.statkart.skif.store5.persistence;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class DefaultPersistenceSessionStrategy implements PersistenceSessionStrategy{
    protected final PersistenceSessionMaster master;
    protected final PersistenceSessionSubtypeHandler[] handlers;
    protected final Map<Class<? extends BubbleId>, PersistenceSessionForSnapshot> subtypeMapping = new HashMap<Class<? extends BubbleId>, PersistenceSessionForSnapshot>();


    public DefaultPersistenceSessionStrategy(PersistenceSessionMaster master, PersistenceSessionSubtypeHandler... handlers) {
        this.master = master;
        this.handlers = handlers;

    }
    @Override
    public SnapshotVersion getShapshot() {
        return master.getShapshot();
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        return master.setSnapshot(snapshotVersion);
    }

    @Override
    public boolean isSnapshotChangable() {
        return master.isSnapshotChangable();
    }

    @Override
    public boolean acceptsSnapshot(SnapshotVersion snapshotVersion) {
        return master.acceptsSnapshot(snapshotVersion);
    }

    @Override
    public PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type) {
        PersistenceSessionForSnapshot sessionForSnapshot = subtypeMapping.get(type);
        if (sessionForSnapshot==null) {
            for (int i = 0; i < handlers.length; i++) {
                PersistenceSessionSubtypeHandler handler = handlers[i];
                if (handler.acceptsSubtype(type)) {
                    sessionForSnapshot = handler;
                    break;
                }
            }
            if (sessionForSnapshot==null)  {
                sessionForSnapshot = master;
            }
           subtypeMapping.put(type, sessionForSnapshot);
        }
        return sessionForSnapshot;
    }

    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        if (interfaceType.isAssignableFrom(this.getClass())) {
            return (T) this;
        } else {
            if (interfaceType.isAssignableFrom(master.getClass())) {
                return (T) master;
            } else {
                for (int i = 0; i < handlers.length; i++) {
                    PersistenceSessionSubtypeHandler handler = handlers[i];
                    if (interfaceType.isAssignableFrom(handler.getClass())) {
                        return (T) handler;
                    }
                }
                throw new ImplementationException("Fant ingen implementasjon for interface:" + interfaceType);
            }
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubbleId.getClass());
        return forBubbleId.get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        throw new NotImplementedException() ;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubble.getId().getClass());
        forBubbleId.insert(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubble.getId().getClass());
        forBubbleId.update(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubble.getId().getClass());
        forBubbleId.delete(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubbleId.getClass());
        forBubbleId.evict(bubbleId);

    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubble.getId().getClass());
        forBubbleId.ensureFullyLoaded(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubbleId.getClass());
        return forBubbleId.refresh(bubbleId);
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        PersistenceSessionForSnapshot forBubbleId = getForBubbleId(bubble.getId().getClass());
        forBubbleId.refresh(bubble);
    }
}
