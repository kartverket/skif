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
 * <p>
 * Problemet med ovenstående er at EJB referanser må defineres opp alle steder hvor de brukes. Referansen må stå i den
 * kallende container managed klassen og ikke der den faktisk brukes. Den klassen som bruker EJB referanse kan få tak i
 * referanse via en parameter til klassen eller via {@code InitialContext}. Dersom den kallende container managed klassen
 * ikke refererer en EJB eksplisitt vil den ikke være tilgjengelig som parameter eller via {@code InitialContext}. Dette
 * skaper et vedlikeholdsproblem siden hver EJB'er da må angi alle andre EJB'er de kunne tenkes å bruke.
 * <p>
 * Problemet er løst her ved å ha en lookup hjelpeklasse hvor man kan registrerer alle EJB referanser for senere lookup.
 * For å få registerert alle EJB referanser må lookupklassen kalles fra en container managed klasse som har definert opp
 * referanse til alle EJB'er. Dette trenger ikke å være fra en EJB klasse men kan også være fra andre container managed
 * klasser som f.eks en ServletListener klasse som kalles under startup av web applikasjonen.
 * <p>
 * Det er muig å registere EJB referanser i flere omganger. Hvergang {@link #registerEjbsFromContext()} }
 * kalles sjekkes {@code InitialContext} for EJB referanser og nye legges til i lookup klassen. Det eneste kravet er
 * at EJB referansen må være registret i lookupklassen før den kan hentes ut.
 * <p>
 * Uthenting skjer via metoden {@link #lookupEjb(Class)}.
 * @author Henrik Fredholm
 */
public class EJBLookupHelper {
    private static Logger logger = LoggerFactory.getLogger(EJBLookupHelper.class);
    private static EJBLookupHelper instance;

    private ConcurrentMap<Class<?>, Object> ejbRegistry = new ConcurrentHashMap<>();

    public List<Class<?>> registerEjbsFromContext() {
        List<Class<?>> foundEJBServices = new ArrayList<>();
        try {
            Context ctx = new InitialContext();
            NamingEnumeration<Binding> iterator = ctx.listBindings("java:comp/env/ejb");
            while (iterator.hasMore()) {
                Binding binding = iterator.next();
                Class<?> serviceClass = addBinding(binding);
                if (serviceClass !=null) {
                    foundEJBServices.add(serviceClass);
                }
            }
        } catch (NameNotFoundException e) {
            // Ignore
        } catch (NamingException e) {
            throw new ImplementationException(e);
        }
        return foundEJBServices;
    }

    private Class<?> addBinding(Binding binding) {
        Object obj = binding.getObject();
        for (Class<?> c : obj.getClass().getInterfaces()) {
            if (Object.class.isAssignableFrom(c)) {
                addEjb(c, obj);
                return c;
            }
        }
        return null;
    }


    public void addEjb(Class<?> serviceClass, Object ejbService) {
        Object old = ejbRegistry.putIfAbsent(serviceClass, ejbService);
        if (old == null) {
            logger.info("Adding EJB for service class: {} instance: {}", serviceClass.getName(), ejbService);
        } // else {
            // Service allerede allerede bunnet. Det er ok. Gjør ingen ting da.
        //}
    }

    /**
     * Finner ejb referanse ut fra service interface. 
     */
    public <T> T lookupEjb(Class<T> serviceClass) {
        Object ejb = ejbRegistry.get(serviceClass);
        if (ejb == null) {
            throw new ConfigurationException("Could not find EJB for interface: " + serviceClass.getName()+ " Check that corresponding EJB has been registered in SKIF by the EJBRegistration class; i.e. by a servlet listener in the Web service's web.xml");
        }
        return serviceClass.cast(ejb);
    }


    public static synchronized EJBLookupHelper getInstance() {
        if (instance == null) {
            instance = new EJBLookupHelper();
        }
        return instance;
    }

    public Set<Class<?>> getServices() {
        return ejbRegistry.keySet();
    }
}