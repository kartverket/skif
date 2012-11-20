package no.statkart.skif.store;

import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;

import java.util.LinkedHashSet;

/**
 * @author Henrik Fredholm
 */
public class StoreServer extends AbstractStore {

    public StoreServer(StoreSessionServer storeSessionServer, Injector injector) {
        super(storeSessionServer, injector);
        storeSessionServer.setStore(this);
    }

    protected StoreSessionServer storeServerSession() {
        if (storeSession instanceof StoreSessionServer) return (StoreSessionServer) storeSession;
        throw new ImplementationException("UnitOfWork is active");
    }

    @Override
    public void clear() {
        storeServerSession().clear();
    }

    /**
     * Starter en ny transaksjon ved manuell transaksjonshåndtering.
     * <p/>
     * Denne metoden er kun en hjelpemetode for verktøy som kjører utelukkende i tjenermodus. Når Store kjører i
     * servicerammeverket er det {@link no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernate#beginService()}
     * eller {@link no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks#beginService()}
     * som utfører denne jobben. Disse må derfor være holdes synkronisert.
     */
    public void beginTransaction() {
        storeServerSession().beginTransaction();
    }

    /**
     * Committer en transaksjon ved manuell transaksjonshåndtering.
     * <p/>
     * Denne metoden er kun en hjelpemetode for verktøy som kjører utelukkende i tjenermodus. Når Store kjører i
     * servicerammeverket er det {@link no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernate#completeService()}
     * eller {@link no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks#completeService()}
     * som utfører denne jobben. Disse må derfor være holdes synkronisert.
     */
    public void commitTransaction() {
        storeServerSession().commitTransaction();
    }

    /**
     * Ruller tilbake en transaksjon ved manuell transaksjonshåndtering.
     * <p/>
     * Denne metoden er kun en hjelpemetode for verktøy som kjører utelukkende i tjenermodus. Når Store kjører i
     * servicerammeverket er det {@link no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernate#abortService()}
     * eller {@link no.statkart.skif.store.service.ejb.EJBResourceProxyHandlerForHibernateWithLocks#abortService()}
     * som utfører denne jobben. Disse må derfor være holdes synkronisert.
     */
    public void rollbackTransaction() {
        storeServerSession().rollbackTransaction();
    }

    public void flush() {
        storeServerSession().flush();
    }

    public void finish() {
        storeServerSession().finish();
    }

    public LinkedHashSet<BubbleId<?>> getDeletedIds() {
        return storeServerSession().getDeletedIds();

    }

    public LinkedHashSet<BubbleId<?>> getInsertedIds() {
        return storeServerSession().getInsertedIds();

    }

    public LinkedHashSet<BubbleId<?>> getLockedIds() {
        return storeServerSession().getLockedIds();

    }

    public LinkedHashSet<BubbleId<?>> getUpdatedIds() {
        return storeServerSession().getUpdatedIds();

    }
}
