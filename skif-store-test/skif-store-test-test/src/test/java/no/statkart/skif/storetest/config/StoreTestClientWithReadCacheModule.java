package no.statkart.skif.storetest.config;

import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.StoreCache;
import no.statkart.skif.store.StoreClientReadCacheImpl;
import no.statkart.skif.store.StoreSessionClient;
import no.statkart.skif.store.service.StoreService;

import static no.statkart.skif.store.StoreSessionClient.defaultComparator;

public class StoreTestClientWithReadCacheModule extends StoreTestClientModule {
    public StoreTestClientWithReadCacheModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    protected StoreSessionClient createStoreSessionClient(StoreService storeService, SnapshotVersionContext snapshotVersionContext) {
        return new StoreSessionClient(storeService, snapshotVersionContext, new StoreCache(), defaultComparator(), new StoreClientReadCacheImpl());
    }
}
