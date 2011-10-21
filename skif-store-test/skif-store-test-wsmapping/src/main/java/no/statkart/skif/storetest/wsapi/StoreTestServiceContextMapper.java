package no.statkart.skif.storetest.wsapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.AbstractServiceContextMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestServiceContextMapper extends AbstractServiceContextMapper<StoreTestContext> {
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
        return context;
    }

    @Override
    public void setDomainServiceContextFromWSServiceContext(StoreTestContext apiContext) {
        ServiceContext serviceContext= serviceContextProvider.get();
        serviceContext.setSystemVersion(apiContext.getSystemVersion());
        serviceContext.setLocale(localeFromString(apiContext.getLocale()));
    }
}
