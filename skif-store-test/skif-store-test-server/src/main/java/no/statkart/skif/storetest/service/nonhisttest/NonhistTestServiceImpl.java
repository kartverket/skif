package no.statkart.skif.storetest.service.nonhisttest;

import com.google.inject.Inject;
import no.statkart.skif.persistence.BarFinder;
import no.statkart.skif.persistence.FooFinder;
import no.statkart.skif.persistence.GeometriFinder;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
import no.statkart.skif.storetest.domain.nonhist.BarId;
import no.statkart.skif.storetest.domain.nonhist.Foo;
import no.statkart.skif.storetest.domain.nonhist.FooId;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @author Steinar Hansen
 */
public class NonhistTestServiceImpl implements NonhistTestService {
    private final HibernateStoreSessionManager sessionManager;
    private final Store store;

    private final FooFinder fooFinder;

    /**
     * @since 2.1
     */
    private final BarFinder barFinder;

    private final GeometriFinder geometriFinder;

    @Inject
    public NonhistTestServiceImpl(HibernateStoreSessionManager sessionManager, Store store, FooFinder fooFinder, BarFinder barFinder, GeometriFinder geometriFinder) {
        this.sessionManager = sessionManager;
        this.store = store;
        this.fooFinder = fooFinder;
        this.barFinder = barFinder;
        this.geometriFinder = geometriFinder;
    }

    @Override
    public Set<FooId<?>> findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion) {

        HibernateStoreSession storeSession = null;
        try {
            storeSession = sessionManager.acquireSnapshotStoreSession(snapshotVersion);
            Session wrappedSession = storeSession.getWrappedSession();
            Query query = wrappedSession.createQuery("from no.statkart.skif.storetest.domain.nonhist.Foo");
            List<Foo> foos = query.list();

            System.out.println(storeSession.getSnapshotVersion().getTimestampString());
            Query query2 = wrappedSession.createQuery("from no.statkart.skif.storetest.domain.demo.Foo");
            List<no.statkart.skif.storetest.domain.demo.Foo> foos2 = query2.list();
//            Query query = storeSession.getWrappedSession().createQuery("from no.statk f where f.navn = :navn");
//            List<Foo> foos = query.setString("navn", navn).list();
            Set<BubbleObject> result = new HashSet<BubbleObject>(foos.size()+foos2.size());

            for (Foo foo : foos) {
                result.add(foo);
            }
            for (no.statkart.skif.storetest.domain.demo.Foo foo2 : foos2) {
                result.add(foo2);
                
            }
            return null;
        } finally {
            sessionManager.releaseSnapshotStoreSession(storeSession);
        }
    }

    public Set<BubbleObject> findAllCurrentFoos(){
        HibernateStoreSession storeSession = null;
        try {
            storeSession = sessionManager.acquireSnapshotStoreSession(SnapshotVersion.CURRENT);
            Session wrappedSession = storeSession.getWrappedSession();
            Query query = wrappedSession.createQuery("from no.statkart.skif.storetest.domain.nonhist.Foo");
            List<Foo> foos = query.list();

            System.out.println(storeSession.getSnapshotVersion().getTimestampString());
            Query query2 = wrappedSession.createQuery("from no.statkart.skif.storetest.domain.demo.Foo");
            List<no.statkart.skif.storetest.domain.demo.Foo> foos2 = query2.list();
            Set<BubbleObject> result = new HashSet<BubbleObject>(foos.size()+foos2.size());

            for (Foo foo : foos) {
                result.add(foo);
            }
            for (no.statkart.skif.storetest.domain.demo.Foo foo2 : foos2) {
                result.add(foo2);

            }
            return result;
        } finally {
            sessionManager.releaseSnapshotStoreSession(storeSession);
        }
    }
    
    @Override
    public Set<FooId<Foo>> findFooIdsForNr(long nr) {
        return new HashSet<FooId<Foo>>();
    }

    @Override
    public List<BarId> findBarIdsAliveAtSnapshot(Set<BarId<?>> barIds, SnapshotVersion snapshotVersion) {
        return new ArrayList<BarId>();
    }

    @Override
    public Map<FooId<?>, Set<BarId<?>>> findBarIdsForFooIds(Set<FooId<?>> fooIds, SnapshotVersion snapshotVersion) {
        return new HashMap<FooId<?>, Set<BarId<?>>>();
    }

}
