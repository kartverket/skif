package no.statkart.skif.skiftest.wsapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.AbstractServiceContextMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.store.SnapshotVersion;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestServiceContextMapper extends AbstractServiceContextMapper<SkifTestContext> {
    @Inject
    public SkifTestServiceContextMapper(Provider<ServiceContext> serviceContextProvider) {
        super(serviceContextProvider);
    }

    @Override
    public SkifTestContext createWSServiceContextFromDomainServiceContext(Mapping map) {
        ServiceContext serviceContext= serviceContextProvider.get();
        SkifTestContext context = new SkifTestContext();
        context.setSystemVersion(serviceContext.getSystemVersion());
        context.setLocale(serviceContext.getLocale().toString());
        return context;
    }

    @Override
    public void setDomainServiceContextFromWSServiceContext(Mapping map, SkifTestContext apiContext) {
        ServiceContext serviceContext= serviceContextProvider.get();
        serviceContext.setSystemVersion(apiContext.getSystemVersion());
        serviceContext.setLocale(localeFromString(apiContext.getLocale()));
    }
}
