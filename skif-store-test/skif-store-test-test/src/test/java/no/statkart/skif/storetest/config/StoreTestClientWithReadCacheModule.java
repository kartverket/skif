package no.statkart.skif.storetest.config;

import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.StoreCache;
import no.statkart.skif.store.StoreClientReadCacheImpl;
import no.statkart.skif.store.StoreSessionClient;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;

public class StoreTestClientWithReadCacheModule extends StoreTestClientModule {
    public StoreTestClientWithReadCacheModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected StoreSessionClient createStoreSessionClient(StoreService storeService, LockService lockService, SnapshotVersionContext snapshotVersionContext) {
        return new StoreSessionClient(storeService, lockService, snapshotVersionContext, new StoreCache(), null, new StoreClientReadCacheImpl());
    }
}
