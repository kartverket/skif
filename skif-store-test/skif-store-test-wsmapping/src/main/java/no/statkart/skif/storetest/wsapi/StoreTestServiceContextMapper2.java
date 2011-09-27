package no.statkart.skif.storetest.wsapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.AbstractServiceContextMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestContext2;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestServiceContextMapper2 extends AbstractServiceContextMapper<StoreTestContext2> {
    @Inject
    public StoreTestServiceContextMapper2(Provider<ServiceContext> serviceContextProvider) {
        super(serviceContextProvider);
    }

    @Override
    public StoreTestContext2 createWSServiceContextFromDomainServiceContext() {
        ServiceContext serviceContext= serviceContextProvider.get();
        StoreTestContext2 context = new StoreTestContext2();
        context.setSystemVersion(serviceContext.getSystemVersion());
        context.setLocale(serviceContext.getLocale().toString());
        return context;
    }

    @Override
    public void setDomainServiceContextFromWSServiceContext(StoreTestContext2 apiContext) {
        ServiceContext serviceContext= serviceContextProvider.get();
        serviceContext.setSystemVersion(apiContext.getSystemVersion());
        serviceContext.setLocale(localeFromString(apiContext.getLocale()));
    }
}
