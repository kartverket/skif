package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.service.AbstractServiceContextMapper;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class WSVersioningServiceContextMapper extends AbstractServiceContextMapper<WSVersioningContext> {
    @Inject
    public WSVersioningServiceContextMapper(Provider<ServiceContext> serviceContextProvider) {
        super(serviceContextProvider);
    }

    @Override
    public WSVersioningContext createWSServiceContextFromDomainServiceContext(Mapping map) {
        ServiceContext serviceContext= serviceContextProvider.get();
        WSVersioningContext context = new WSVersioningContext();
        context.setClientVersion(serviceContext.getSystemVersion());
        context.setLocale(serviceContext.getLocale().toString());
        return context;
    }

    @Override
    public void setDomainServiceContextFromWSServiceContext(Mapping map, WSVersioningContext apiContext) {
        ServiceContext serviceContext= serviceContextProvider.get();
        serviceContext.setSystemVersion(apiContext.getClientVersion());
        serviceContext.setLocale(localeFromString(apiContext.getLocale()));
    }
}
