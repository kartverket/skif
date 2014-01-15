package no.statkart.skif.storetest.wsapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.SnapshotVersionTypeMapper;
import no.statkart.skif.service.AbstractServiceContextMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServiceContextMapper extends AbstractServiceContextMapper<StoreTestContext>  {
    private final SnapshotVersionContext snapshotVersionContext;

    @Inject
    public StoreTestServiceContextMapper(Provider<ServiceContext> serviceContextProvider, SnapshotVersionContext snapshotVersionContext) {
        super(serviceContextProvider);
        this.snapshotVersionContext = snapshotVersionContext;
    }

    @Override
    public StoreTestContext createWSServiceContextFromDomainServiceContext(Mapping map) {
        ServiceContext serviceContext= serviceContextProvider.get();
        StoreTestContext context = new StoreTestContext();
        context.setSystemVersion(serviceContext.getSystemVersion());
        context.setLocale(serviceContext.getLocale().toString());
        context.setSnapshotVersion(map.d2w(snapshotVersionContext.getInstance().getSnapshotVersion(), Timestamp.class));
        return context;
    }

    @Override
    public void setDomainServiceContextFromWSServiceContext(Mapping map, StoreTestContext apiContext) {
        ServiceContext serviceContext= serviceContextProvider.get();
        serviceContext.setSystemVersion(apiContext.getSystemVersion());
        serviceContext.setLocale(localeFromString(apiContext.getLocale()));
        snapshotVersionContext.getInstance().setSnapshotVersion(map.w2d(apiContext.getSnapshotVersion(), SnapshotVersion.class));
    }
}
