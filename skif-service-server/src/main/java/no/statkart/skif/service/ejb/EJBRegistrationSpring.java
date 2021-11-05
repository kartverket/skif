package no.statkart.skif.service.ejb;

import no.statkart.skif.service.ServicesListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

public abstract class EJBRegistrationSpring {
    @Autowired
    private ApplicationContext applicationContext;
    private final ServicesListing servicesListing;

    public EJBRegistrationSpring(ServicesListing servicesListing) {
        this.servicesListing = servicesListing;
    }

    @PostConstruct
    public void configureEJBServices() {
        EJBLookupHelper.setRegisterEjbsFromContextValue(false);
        EJBLookupHelper lookupHelper = EJBLookupHelper.getInstance();
        registerEJBServices(applicationContext, lookupHelper, servicesListing);
    }

    private void registerEJBServices(ApplicationContext applicationContext, EJBLookupHelper lookupHelper, ServicesListing servicesListing) {
        for (Class<?> service : servicesListing.getServices()) {
            lookupHelper.addEjb(service, applicationContext.getBean(service));
        }
    }
}
