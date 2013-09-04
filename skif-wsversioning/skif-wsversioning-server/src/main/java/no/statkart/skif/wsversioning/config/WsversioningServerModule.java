package no.statkart.skif.wsversioning.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import no.statkart.skif.config.SkifServerModule;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.*;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.wsversioning.domain.Veg;
import no.statkart.skif.wsversioning.domain.VegId;

import java.sql.Connection;
import java.util.*;

/**
 * Server-modul for WSVersioning-prosjektet. Den underbygger Store med et statisk map og støtter hverken id-generering
 * eller låsing.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningServerModule extends SkifServerModule {
    public WSVersioningServerModule(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void configure() {
        super.configure();
        install(new RunOnServerServiceModule(moduleConfiguration));

        bind(Store.class).to(StoreServer.class);
        bind(BubbleDependencyComparator.class).to(BubbleDependencyComparatorNoReordering.class);

        install(new ServerServiceModule(moduleConfiguration, new WSVersioningServices().getServices()));
    }

    @Provides
    @Singleton
    protected LockerStrategy lockerStrategy() {
        return new LockerStrategy() {
            @Override
            public boolean lock(BubbleId id) throws LockedException {
                return false;
            }

            @Override
            public void unlock(BubbleId id) {
            }

            @Override
            public boolean isLockedByCaller(BubbleId id) {
                return true;
            }

            @Override
            public boolean isLockedByOther(BubbleId id) {
                return false;
            }

            @Override
            public void releaseAllLocks() {
            }

            @Override
            public void releaseLocksOnRollback() {
            }

            @Override
            public void clear() {
            }

            @Override
            public void registerInserted(BubbleId id) {
            }

            @Override
            public void registerUpdated(BubbleId id) {
            }

            @Override
            public void registerRemoved(BubbleId id) {
            }

            @Override
            public void consumeAllLocks() {
            }

            @Override
            public void releaseLocksOnNonTransactionalScopeCompletion() {
            }
        };
    }

    @Provides
    protected Connection noConnectionProvider() {
        return null;
    }

    @Provides
    protected IdService noIdService() {
        return null;
    }

    @Provides
    @ServiceRequestScoped
    protected StoreServer provideStoreServer(PersistenceSessionManager persistenceSessionManager, Injector injector, BubbleDependencyComparator bubbleDependencyComparator, Provider<VersionFinder> versionFinderProvider, LockerStrategy lockerStrategy) {
        List<StoreSessionReadListener> readListeners = ImmutableList.of();
        List<StoreSessionWriteListener> writeListeners = ImmutableList.of();
        List<StoreSessionFinishListener> finishListeners = ImmutableList.of();
        StoreServer storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, versionFinderProvider, lockerStrategy, bubbleDependencyComparator, readListeners, writeListeners, finishListeners), injector);
        return storeServer;
    }

    @Provides
    @ServiceRequestScoped
    @Named("bubbles")
    protected Map<BubbleId<?>, BubbleObject> createBubbleMap() {
        ImmutableMap.Builder<BubbleId<?>, BubbleObject> builder = ImmutableMap.builder();
        builder.put(new VegId(1L), new Veg(new VegId(1L), "Tjernslia"));
        builder.put(new VegId(2L), new Veg(new VegId(2L), "Kartverksveien"));
        return builder.build();
    }

    @Provides
    @ServiceRequestScoped
    protected PersistenceSessionManager dummyPersistenceSessionManager(@Named("bubbles") final Map<BubbleId<?>, BubbleObject> bubbleMap) {
        return new PersistenceSessionManager() {
            private boolean active = false;

            @Override
            public PersistenceSessionForSnapshot getForSnapshotVersion(SnapshotVersion snapshotVersion) {
                return null;
            }

            @Override
            public PersistenceSessionForSnapshot lockForSnapshot(SnapshotVersion snapshotVersion) {
                return null;
            }

            @Override
            public void unlock(PersistenceSessionForSnapshot persistenceSessionForSnapshot) {
            }

            @Override
            public void clear() {
            }

            @Override
            public void verifySessionIsEmpty() {
            }

            @Override
            public <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {
                BubbleObject bubbleObject = bubbleMap.get(bubbleId);
                if (bubbleObject == null) {
                    throw new ObjectNotFoundException(bubbleId);
                }
                return bubbleId.getType().cast(bubbleObject);
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
                Set<T> result = new LinkedHashSet<T>(bubbleIds.size());
                for (I bubbleId : bubbleIds) {
                    result.add(get(bubbleId));
                }
                return result;
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
                throw new NotImplementedException("insert");
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
                throw new NotImplementedException("update");
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
                throw new NotImplementedException("delete");
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
            }

            @Override
            public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
            }

            @Override
            public <T extends BubbleObject> T refresh(BubbleId<? extends T> bubbleId) {
                return get(bubbleId);
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> refresh(Collection<I> bubbleIds) {
                return get(bubbleIds);
            }

            @Override
            public <T extends BubbleObject> void refresh(T bubble) {
            }

            @Override
            public void beginTransaction() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void commit() {
            }

            @Override
            public void rollback() {
            }

            @Override
            public void close() {
            }

            @Override
            public boolean isActive() {
                return active;
            }

            @Override
            public void setActive() {
                active = true;
            }
        };
    }

    @Provides
    @ServiceRequestScoped
    protected ResourceManager provideResourceManager() {
        return new DefaultResourceManager();
    }
}
