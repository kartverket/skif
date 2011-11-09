package no.statkart.skif.service.ejb;

import no.statkart.skif.exception.*;
import no.statkart.skif.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/**
 * Hjelpeklasse for dynamsik lookup av EJB'er som omgår problemet med at EJB 3.0 standarden har ikke noen enkel måte å
 * gjøre dynamisk  lookup av EJB'er. EJB 3.0 krever at EJB'er som refereres må defineres eksplisitt av den kallende container
 * managed klassen. Først da blir EJB'ene tilgjengelig for lookup via {@code InitialContext}. Dette problemet skal
 * muligvis være løst i EJB 3.1.
 * <p/>
 * Problemet med ovenstående er at EJB referanser må defineres opp alle steder hvor de brukes. Referansen må stå i den
 * kallende container managed klassen og ikke der den faktisk brukes. Den klassen som bruker EJB referanse kan få tak i
 * referanse via en parameter til klassen eller via {@code InitialContext}. Dersom den kallende container managed klassen
 * ikke refererer en EJB eksplisitt vil den ikke være tilgjengelig som parameter eller via {@code InitialContext}. Dette
 * skaper et vedlikeholdsproblem siden hver EJB'er da må angi alle andre EJB'er de kunne tenkes å bruke.
 * <p/>
 * Problemet er løst her ved å ha en lookup hjelpeklasse hvor man kan registrerer alle EJB referanser for senere lookup.
 * For å få registerert alle EJB referanser må lookupklassen kalles fra en container managed klasse som har definert opp
 * referanse til alle EJB'er. Dette trenger ikke å være fra en EJB klasse men kan også være fra andre container managed
 * klasser som f.eks en ServletListener klasse som kalles under startup av web applikasjonen.
 * <p/>
 * Det er muig å registere EJB referanser i flere omganger. Hvergang {@link #registerEjbsFromContext()} }
 * kalles sjekkes {@code InitialContext} for EJB referanser og nye legges til i lookup klassen. Det eneste kravet er
 * at EJB referansen må være registret i lookupklassen før den kan hentes ut.
 * <p/>
 * Uthenting skjer via metoden {@link #lookupEjb(Class)}.
 * @author Henrik Fredholm
 */
public class EJBLookupHelper {
    private static Logger logger = LoggerFactory.getLogger(EJBLookupHelper.class);
    private static EJBLookupHelper instance;

    private ConcurrentMap<Class<? extends Object>, Object> ejbRegistry = new ConcurrentHashMap<Class<? extends Object>, Object>();

    public List<Class<? extends Object>> registerEjbsFromContext() {
        List<Class<? extends Object>> foundEJBServices = new ArrayList<Class<? extends Object>>();
        try {
            Context ctx = new InitialContext();
            NamingEnumeration<Binding> iterator = ctx.listBindings("java:comp/env/ejb");
            while (iterator.hasMore()) {
                Binding binding = iterator.next();
                Class<Object> serviceClass = addBinding(binding);
                if (serviceClass !=null) {
                    foundEJBServices.add(serviceClass);
                }
            }
        } catch (NameNotFoundException e) {
            // Ignore
        } catch (NamingException e) {
            // TODO: uncomment
            //throw new SkifConfigurationException(e);
        }
        return foundEJBServices;
    }

    private Class<Object> addBinding(Binding binding) {
        Object obj = binding.getObject();
        for (Class<?> c : obj.getClass().getInterfaces()) {
            if (Object.class.isAssignableFrom(c)) {
                Class<Object> serviceClass = (Class<Object>) c;
                addEjb(serviceClass, (Object) obj);
                return serviceClass;
            }
        }
        return null;
    }


    public void addEjb(Class<? extends Object> serviceClass, Object ejbService) {
        Object old = ejbRegistry.putIfAbsent(serviceClass, ejbService);
        // TODO: Bruk logging 
        if (old == null) {
            if (logger.isInfoEnabled()) {
                logger.info("Legger til ejb service for service class: " + serviceClass.getName() + " instans: " + ejbService);
            }
        } else {
            // Service allerede allerede bunnet. Det er ok. Gjør ingen ting da.
        }
    }

    /**
     * Finner ejb referanse ut fra service interface. 
     * @param serviceClass
     * @param <T>
     * @return
     */
    public <T extends Object> T lookupEjb(Class<T> serviceClass) {
        Object ejb = ejbRegistry.get(serviceClass);
        if (ejb == null) {
            throw new ConfigurationException("Fant ikke EJB for interface: " + serviceClass.getName()+ " Sjekk at tilhørende EJB'en har blitt registrert i skif-rammeverket via EJBRegistration klassen, f.eks via en servlet-listner i Web servicens web.xml");
        }
        return (T) ejb;
    }


    public static synchronized EJBLookupHelper getInstance() {
        if (instance == null) {
            instance = new EJBLookupHelper();
        }
        return instance;
    }

    public Set<Class<? extends Object>> getServices() {
        return ejbRegistry.keySet();
    }
}