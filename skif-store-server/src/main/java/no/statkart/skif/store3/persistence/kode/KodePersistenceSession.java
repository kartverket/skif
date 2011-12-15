package no.statkart.skif.store3.persistence.kode;

import no.statkart.skif.exception.*;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store3.persistence.PersistenceSession;
import no.statkart.skif.store3.persistence.SnapshotSessionEventListener;
import no.statkart.skif.store3.persistence.SnapshotSessionEventSource;
import no.statkart.skif.store3.persistence.hibernate.HibernatePersistenceSession;
import no.statkart.skif.util.KodeMsg;
import org.hibernate.*;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class KodePersistenceSession implements PersistenceSession {
    protected final Session session;
    protected final SnapshotSessionEventSource eventSource;
    protected final SnapshotVersionSeed snapshotVersionSeed;

    private final EnumKodeManager enumKodeManager;
    private final KodeMsg kodeMsg;
    private final ServiceContext serviceContext;

    private final HibernatePersistenceSession dbKodeSession;

    public KodePersistenceSession(Session session, SnapshotSessionEventSource eventSource, SnapshotVersionSeed snapshotVersionSeed, EnumKodeManager enumKodeManager, KodeMsg kodeMsg, ServiceContext serviceContext) {
        this.session = session;
        this.eventSource = eventSource;
        this.snapshotVersionSeed = snapshotVersionSeed;
        this.enumKodeManager = enumKodeManager;
        this.kodeMsg = kodeMsg;
        this.serviceContext = serviceContext;

        eventSource.addListener(new SnapshotSessionEventListener(){
            @Override
            public void onChangeSnapshot() {
                // TODO? dbKodeSession får jo selv denne levert direkte på døra, så da trenger ikke denne videresende.
            }

            @Override
            public void onClear() {
                // TODO Hva betyr dette?
            }

            @Override
            public void onClose() {
                KodePersistenceSession.this.eventSource.removeListener(this);
            }
        });

        dbKodeSession = new HibernatePersistenceSession(session, eventSource, snapshotVersionSeed);
    }

    protected final <T extends BubbleObject, I extends BubbleId<? extends T>> void checkSnapshotVersion(I bubbleId) {
        SnapshotVersion snapshotVersionFromHolder = snapshotVersionSeed.get();
        SnapshotVersion snapshotVersionInId = bubbleId.getSnapshotVersion();

        if (!snapshotVersionFromHolder.equals(snapshotVersionInId)) {
            throw new ImplementationException("Id har feil SnapshotVersion for session:" + bubbleId);
        }
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
        checkSnapshotVersion(bubbleId);

        T bubble;
        if (bubbleId instanceof EnumKodeId) {
            bubble = enumKodeManager.get(bubbleId);
            if (bubble == null) {
                throw new no.statkart.skif.exception.ObjectNotFoundException(bubbleId);
            }
        } else if (bubbleId instanceof DbKodeId) {
            bubble = dbKodeSession.get(bubbleId);
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
            checkSnapshotVersion(bubbleId);
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
            bubbles.addAll(dbKodeSession.get(dbKodeIds));
        }

        for (T bubble : bubbles) {
            localizeObject(serviceContext.getLocale(), bubble);
        }

        return bubbles;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        if (bubbleId instanceof DbKodeId) {
            dbKodeSession.evict(bubbleId);
        }
    }
}
