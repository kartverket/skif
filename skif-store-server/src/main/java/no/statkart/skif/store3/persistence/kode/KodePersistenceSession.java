package no.statkart.skif.store3.persistence.kode;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store3.persistence.PersistenceSession;
import no.statkart.skif.store3.persistence.SnapshotSessionEventListener;
import no.statkart.skif.store3.persistence.SnapshotSessionEventSource;
import org.hibernate.Session;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public class KodePersistenceSession implements PersistenceSession {
    protected final Session session;
    protected final SnapshotSessionEventSource eventSource;
    protected final SnapshotVersionSeed snapshotVersionSeed;

    public KodePersistenceSession(Session session, SnapshotSessionEventSource eventSource, SnapshotVersionSeed snapshotVersionSeed) {
        this.session = session;
        this.eventSource = eventSource;
        this.snapshotVersionSeed = snapshotVersionSeed;

        eventSource.addListener(new SnapshotSessionEventListener(){
            @Override
            public void onChangeSnapshot() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onClear() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onClose() {
                KodePersistenceSession.this.eventSource.removeListener(this);
            }
        });
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
