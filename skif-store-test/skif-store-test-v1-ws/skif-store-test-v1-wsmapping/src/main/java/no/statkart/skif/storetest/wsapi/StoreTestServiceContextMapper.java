package no.statkart.skif.storetest.wsapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mapper.SnapshotVersionTypeMapper;
import no.statkart.skif.service.AbstractServiceContextMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServiceContextMapper extends AbstractServiceContextMapper<StoreTestContext> {
    private final SnapshotVersionTypeMapper<Timestamp> snapshotVersionTypeMapper = SnapshotVersionTypeMapper.create(Timestamp.class);

    @Inject
    public StoreTestServiceContextMapper(Provider<ServiceContext> serviceContextProvider) {
        super(serviceContextProvider);
    }

    @Override
    public StoreTestContext createWSServiceContextFromDomainServiceContext() {
        ServiceContext serviceContext= serviceContextProvider.get();
        StoreTestContext context = new StoreTestContext();
        context.setSystemVersion(serviceContext.getSystemVersion());
        context.setLocale(serviceContext.getLocale().toString());
        context.setSnapshotVersion(snapshotVersionTypeMapper.mapDomainObject(serviceContext.getSnapshotVersion()));
        return context;
    }

    @Override
    public void setDomainServiceContextFromWSServiceContext(StoreTestContext apiContext) {
        ServiceContext serviceContext= serviceContextProvider.get();
        serviceContext.setSystemVersion(apiContext.getSystemVersion());
        serviceContext.setLocale(localeFromString(apiContext.getLocale()));
        serviceContext.setSnapshotVersion(snapshotVersionTypeMapper.mapWsapiObject(apiContext.getSnapshotVersion()));
    }
}
