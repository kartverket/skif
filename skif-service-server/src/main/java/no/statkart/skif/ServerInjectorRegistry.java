package no.statkart.skif;

import com.google.inject.Injector;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.ejb.EJBLookupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasse som holder på registrerte skif server injectors. Denne klasse gjør det mulig å ha mer
 * enn en server injector per applikasjon om ønskelig.
 * @author Henrik Fredholm
 * @since 0.5
 */
public class ServerInjectorRegistry {
    private static Logger logger = LoggerFactory.getLogger(ServerInjectorRegistry.class);

    public static final String DEFAULT_SERVER_CONFIG = "skif-server.configuration";
    private static final Map<Object, Injector> injectorMap = new HashMap<Object, Injector>();

    public static void init() {
        // Dette er nødvendig for å få bunnet EJB'ene slik at man kan gjøre dynamisk lookup av dem serere.
        EJBLookupHelper lookupHelper = EJBLookupHelper.getInstance();
        lookupHelper.registerEjbsFromContext();

        final Collection<Class<?>> services = lookupHelper.getServices();
        for (Class<?> service : services) {
            logger.info("Binder service " + service.getName());
        }
    }

    public static synchronized Injector getInjector() {
        return getInjector("skif-server.configuration");
    }

    public static synchronized Injector getInjector(String configfile) {
        Injector injector = injectorMap.get(configfile);
        if (injector==null) {
           ModuleBuilder moduleBuilder = new ModuleBuilder()
                    .setConfigurationFilename(configfile);
            injector = createInjector(moduleBuilder);
        }
        return injector;
    }

    public static synchronized Injector getInjector(Class<? extends Configuration> configurationClass) {
        Injector injector = injectorMap.get(configurationClass.getName());

        if (injector==null) {
            final Configuration configuration = SkifUtil.newInstance(configurationClass);
            ModuleBuilder moduleBuilder = new ModuleBuilder()
                    .setConfiguration(configuration);
            injector = createInjector(moduleBuilder);
        }
        return injector;

    }

    public static synchronized Injector getInjector(String key, ModuleBuilder moduleBuilder) {
        Injector injector = injectorMap.get(key);
        if (injector==null) {
            injector = createInjector(moduleBuilder);
            injectorMap.put(key, injector);
        }
        return injector;
    }

    private static Injector createInjector(ModuleBuilder moduleBuilder) {
        init();
        return moduleBuilder.setServiceMode(ServiceMode.JEE).buildInjector();
    }


}
