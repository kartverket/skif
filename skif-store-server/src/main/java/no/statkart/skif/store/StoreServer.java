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

    /**
     * Fjerner alle umodifiserte objekter fra Store og underliggende sessioner. Hvis Store inneholder modifiserte objekter
     * så  utføres det {@link #evictAll()}  kall. Hvis Store ikke inneholder modifiserte objekter så nullstilles
     * Store og underliggende sessioner. Informasjon låste objekter kastes fra minnet, men finnes forsatt i database
     * og går derfor ikke tapt.
     */
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

    /**
     * Flusher endringer, kaller finishListeners, flusher på nytt om nødvendig. Tar deretter og fjerner alle
     * objekter som har blitt slettet fra Store og setter status for alle andre endret objekter til {@code
     * UNCHANGED}.
     * <p/>
     * Kall til finish() bør etterfølges av kall til enten {@link #commitTransaction()} eller {@link
     * #rollbackTransaction()} uten at det utføres andre mellomliggende operasjoner på {@code StoreServer}.
     */
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
