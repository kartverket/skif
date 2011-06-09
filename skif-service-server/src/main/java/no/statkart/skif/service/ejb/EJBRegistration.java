package no.statkart.skif.service.ejb;

import no.statkart.skif.service.ServicesListing;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hjelpeklasse for å definere opp referanse til alle lokale EJB'er slik at EJB referanse kan hentes ut
 * fra en vilkårlig komponent på serveren. Hver subsystem/api som definere opp et set med EJB'er må lage en subklasse
 * av denne klassen som lister alle implementerte EJB'er i en @EJBs annotation. Dette
 * gjør at EJB'ene blir tilgjengelige for lookup via {@code InitialContext} og EJB'ene dermed kan
 * gjemmes i en hjelpeklasse {@link EJBLookupHelper} som benyttes for senere lookup
 * fra klasser hvor {@code InitialContext} ikke har definert opp referanse til alle EJB'ene.
 * </p>
 * Dette er i hack som omgår det problem at EJB 3.0 har begrenset støtte å dynamisk lookup av EJB'er. Dette skal være
 * løst i EJB 3.1.
 * <p/>
 * For at EJB'ene skal bli registrert må klassen kjøres fra en ServletContextListner minst en gang.
 * <p/>
 * TODO: Pt må alle slike kjøres før servermodulen opprettes. Kunne være fint hvis dette ikke var nødvendig
 *
 * @author Henrik Fredholm
 * @since 0.5
 */
public abstract class EJBRegistration implements ServletContextListener {
    final protected ServicesListing servicesListing;

    public EJBRegistration(ServicesListing servicesListing) {
        this.servicesListing = servicesListing;
    }

    protected List<Class<?>> getRequiredServices() {
        return servicesListing.getServices();

    }

    private void registerEJBs() {
        // Dette er nødvendig for å få bunnet EJB'ene slik at man kan gjøre dynamisk lookup av dem serere.
        EJBLookupHelper lookupHelper = EJBLookupHelper.getInstance();
        Set<Class<?>> servicesFromEJBContext = new HashSet<Class<?>>(lookupHelper.registerEjbsFromContext());

        final Collection<Class<?>> requiredServices = getRequiredServices();
        for (Class<?> service : requiredServices) {
            if (!servicesFromEJBContext.contains(service)) {
                // TODO: Bruk logging
                System.out.println("XXXXX - Missing @EJB service ref: " + service.getName());
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        registerEJBs();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Not used
    }

}
