package no.statkart.skif.store5.persistence.kode;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store5.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store5.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.util.KodeMsg;

import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class DefaultKodePersistenceSession implements KodePersistenceSession {
    private final EnumKodeManager enumKodeManager;
    private final KodeMsg kodeMsg;
    private final ServiceContext serviceContext;
    private final HibernatePersistenceSessionMaster persistenceSessionMaster;

    public DefaultKodePersistenceSession(HibernatePersistenceSessionMaster persistenceSessionMaster, EnumKodeManager enumKodeManager, KodeMsg kodeMsg, ServiceContext serviceContext) {
        this.enumKodeManager = enumKodeManager;
        this.kodeMsg = kodeMsg;
        this.serviceContext = serviceContext;
        this.persistenceSessionMaster = persistenceSessionMaster;
    }

    @Override
    public boolean acceptsSubtype(Class<? extends BubbleId> type) {
        // TODO: Implementasjon for kodeliste
        return KodeId.class.isAssignableFrom(type) || KodelisteId.class.isAssignableFrom(type);
    }

    private <T extends BubbleObject> T localizeObject(Locale lokale, T bubbleObject) {
        if (bubbleObject instanceof EnumKode) {
            EnumKode bubbleKode = (EnumKode) bubbleObject;
            String beskrivelsesKey = bubbleKode.getBeskrivelsesKey();
            bubbleKode.setBeskrivelsesKey(beskrivelsesKey);
            String lokalisertBeskrivelse = kodeMsg.getString(beskrivelsesKey, lokale);
            bubbleKode.setBeskrivelse(lokalisertBeskrivelse);
        } else if (bubbleObject instanceof DbKode) {
            DbKode bubbleKode = (DbKode) bubbleObject;
            String lokalisertBeskrivelse = bubbleKode.getLokalisertBeskrivelse().get(lokale.toString());
            bubbleKode.setBeskrivelse(lokalisertBeskrivelse);
        } else if (bubbleObject instanceof EnumKodeliste) {
            EnumKodeliste kodeliste = (EnumKodeliste) bubbleObject;
            String beskrivelsesKey = kodeliste.getBeskrivelsesKey();
            String lokalisertBeskrivelse = kodeMsg.getString(beskrivelsesKey,lokale);
            kodeliste.setBeskrivelse(lokalisertBeskrivelse);
            kodeliste.setBeskrivelsesKey(beskrivelsesKey);
        } else {
            DbKodeliste kodeliste = (DbKodeliste) bubbleObject;
            String lokalisertBeskrivelse = kodeliste.getLokalisertBeskrivelse().get(lokale.toString());
            kodeliste.setBeskrivelse(lokalisertBeskrivelse);
        }
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        T bubble;
        if (bubbleId instanceof EnumKodeId) {
            bubble = enumKodeManager.get(bubbleId);
            if (bubble == null) {
                throw new ObjectNotFoundException(bubbleId);
            }
        } else if (bubbleId instanceof DbKodeId) {
            bubble = persistenceSessionMaster.get(bubbleId);
        } else {
            throw new ImplementationException("Fikk noe annet enn EnumKodeId eller DbKodeId: " + bubbleId.getClass().toString());
        }

        return localizeObject(serviceContext.getLocale(), bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<I> dbKodeIds = new ArrayList<I>();
        Set<T> bubbles = new HashSet<T>(bubbleIds.size());

        for (I bubbleId : bubbleIds) {
            if (bubbleId instanceof EnumKodeId) {
                T bubble = enumKodeManager.get(bubbleId);
                if (bubble == null) {
                    throw new no.statkart.skif.exception.ObjectNotFoundException(bubbleId);
                }
                bubbles.add(bubble);
            } else if (bubbleId instanceof DbKodeId) {
                dbKodeIds.add(bubbleId);
            } else {
                throw new ImplementationException("Fikk noe annet enn EnumKodeId eller DbKodeId: " + bubbleId.getClass().toString());
            }
        }

        if (!dbKodeIds.isEmpty()) {
            bubbles.addAll(persistenceSessionMaster.get(dbKodeIds));
        }

        for (T bubble : bubbles) {
            localizeObject(serviceContext.getLocale(), bubble);
        }

        return bubbles;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        if (bubbleId instanceof DbKodeId) {
            persistenceSessionMaster.evict(bubbleId);
        }
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        throw new NotImplementedException();
    }

    @Override
    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        throw new NotImplementedException();
    }

}
