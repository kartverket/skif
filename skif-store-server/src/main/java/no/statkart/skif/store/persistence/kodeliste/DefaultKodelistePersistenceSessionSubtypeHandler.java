package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersistenceSessionSubtypeHandler;
import org.hibernate.Session;

import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class DefaultKodelistePersistenceSessionSubtypeHandler implements KodelistePersistenceSessionSubtypeHandler {
    private final EnumKodelisteManager enumKodelisteManager;
    private final ServiceContext serviceContext;
    private final HibernatePersistenceSessionMaster persistenceSessionMaster;

    public DefaultKodelistePersistenceSessionSubtypeHandler(HibernatePersistenceSessionMaster persistenceSessionMaster, EnumKodelisteManager enumKodelisteManager, ServiceContext serviceContext) {
        this.persistenceSessionMaster = persistenceSessionMaster;
        this.enumKodelisteManager = enumKodelisteManager;
        this.serviceContext = serviceContext;
    }

    @Override
    public boolean acceptsSubtype(Class<? extends BubbleId> type) {
        return KodeId.class.isAssignableFrom(type) || KodelisteId.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        T bubble;
        if (bubbleId instanceof EnumKodeId) {
            bubble = enumKodelisteManager.get(bubbleId);
            if (bubble == null) {
                throw new ObjectNotFoundException(bubbleId);
            }
            if (!bubbleId.getSnapshotVersion().equals(bubble.getId().getSnapshotVersion())) {
                setSnapshotVersion(Kode.class.cast(bubble), bubbleId.getSnapshotVersion());
            }
            Kode.class.cast(bubble).localize(serviceContext.getLocale().toString());
        } else if (bubbleId instanceof DbKodeId) {
            bubble = persistenceSessionMaster.get(bubbleId);
            DbKode dbKode = DbKode.class.cast(bubble);
            // Må sette kodelisteId på kode da denne ikke hentes fra databasen, men tas fra idklassen
            dbKode.setKodelisteId(dbKode.getId().getKodelisteId());
            dbKode.localize(serviceContext.getLocale().toString());
        } else {
            // bubbleId er en kodelisteId
            bubble = enumKodelisteManager.get(bubbleId);
            if (bubble != null) {
                // Kodeliste for EnumKode
                if (!bubbleId.getSnapshotVersion().equals(bubble.getId().getSnapshotVersion())) {
                    setSnapshotVersion(Kodeliste.class.cast(bubble), bubbleId.getSnapshotVersion());
                }
            } else {
                // Kodeliste for DbKode
                bubble = persistenceSessionMaster.get(bubbleId);
                Kodeliste kodeliste = Kodeliste.class.cast(bubble);
                loadKodeIds(kodeliste);
            }
            Kodeliste.class.cast(bubble).localize(serviceContext.getLocale().toString());
        }

        return bubble;
    }

    private void loadKodeIds(Kodeliste kodeliste) {
        Class<? extends Kode> kodeClass = kodeliste.getKodeClass();
        try {
            Session session = persistenceSessionMaster.reserveSession();
            List<DbKode> list = session.createCriteria(kodeClass).list();
            List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();

            KodelisteId kodelisteId = kodeliste.getId();
            for (DbKode t : list) {
                if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                    throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbKode: " + t + " DbKode.getKodelisteId: " + t.getId().getKodelisteId());
                }
                kodeIds.add(t.getId());
            }
            kodeliste.setKodeIds(kodeIds);
        } finally {
            persistenceSessionMaster.reserveSession();
        }

    }

    private void setSnapshotVersion(Kode kode, SnapshotVersion snapshotVersion) {
        kode.setId(kode.getId().asSnapshotVersion(snapshotVersion));
        kode.setKodelisteId((KodelisteId<?>) kode.getKodelisteId().asSnapshotVersion(snapshotVersion));
    }

    private void setSnapshotVersion(Kodeliste kodeliste, SnapshotVersion snapshotVersion) {
        kodeliste.setId(kodeliste.getId().asSnapshotVersion(snapshotVersion));
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        List<KodeId<?>> newkodeIds = new ArrayList<KodeId<?>>(kodeIds.size());
        for (KodeId<?> kodeId : kodeIds) {
            newkodeIds.add((KodeId) kodeId.asSnapshotVersion(snapshotVersion));
        }
        kodeliste.setKodeIds(newkodeIds);
        kodeliste.localize(serviceContext.getLocale().toString());
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<I> dbKodeIds = new ArrayList<I>();
        List<I> dbKodelisteIds = new ArrayList<I>();
        Set<T> bubbles = new HashSet<T>(bubbleIds.size());

        for (I bubbleId : bubbleIds) {
            if (bubbleId instanceof EnumKodeId) {
                T bubble = enumKodelisteManager.get(bubbleId);
                if (bubble == null) {
                    throw new ObjectNotFoundException(bubbleId);
                }
                Kode.class.cast(bubble).localize(serviceContext.getLocale().toString());
                bubbles.add(bubble);
            } else if (bubbleId instanceof DbKodeId) {
                dbKodeIds.add(bubbleId);
            } else {
                T bubble = enumKodelisteManager.get(bubbleId);
                if (bubble != null) {
                    Kodeliste.class.cast(bubble).localize(serviceContext.getLocale().toString());
                    bubbles.add(bubble);
                } else {
                    dbKodelisteIds.add(bubbleId);
                }
            }
        }

        if (!dbKodeIds.isEmpty()) {
            Collection<? extends T> dbKoder = persistenceSessionMaster.get(dbKodeIds);
            for (T t : dbKoder) {
                Kode.class.cast(t).localize(serviceContext.getLocale().toString());
            }
            bubbles.addAll(dbKoder);
        }

        if (!dbKodeIds.isEmpty()) {
            Collection<? extends T> dbKoderlister = persistenceSessionMaster.get(dbKodelisteIds);
            for (T t : dbKoderlister) {
                Kodeliste.class.cast(t).localize(serviceContext.getLocale().toString());
            }
            bubbles.addAll(dbKoderlister);
        }
        return bubbles;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        if (bubbleId instanceof DbKodeId) {
            persistenceSessionMaster.evict(bubbleId);
        }
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        // No op
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        throw new NotImplementedException();
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        throw new NotImplementedException();
    }

    @Override
    public SnapshotVersion getSnapshot() {
        return persistenceSessionMaster.getSnapshot();
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        return persistenceSessionMaster.setSnapshot(snapshotVersion);
    }

    @Override
    public boolean isSnapshotChangable() {
        return persistenceSessionMaster.isSnapshotChangable();
    }

    @Override
    public boolean acceptsSnapshot(SnapshotVersion snapshotVersion) {
        return persistenceSessionMaster.acceptsSnapshot(snapshotVersion);
    }

    @Override
    public PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        throw new UnsupportedOperationException();
    }

}
