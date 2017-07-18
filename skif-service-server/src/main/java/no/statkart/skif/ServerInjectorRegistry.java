package no.statkart.skif;

import com.google.inject.Injector;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.ejb.EJBLookupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Klasse som holder på registrerte skif server injectors. Denne klasse gjør det mulig å ha mer
 * enn en server injector per applikasjon om ønskelig.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerInjectorRegistry {
    private static Logger logger = LoggerFactory.getLogger(ServerInjectorRegistry.class);

    private static final Map<Object, Injector> injectorMap = new HashMap<>();

    public static void init() {
        // Dette er nødvendig for å få bunnet EJB'ene slik at man kan gjøre dynamisk lookup av dem serere.
        EJBLookupHelper lookupHelper = EJBLookupHelper.getInstance();
        lookupHelper.registerEjbsFromContext();

        final Collection<Class<?>> services = lookupHelper.getServices();
        for (Class<?> service : services) {
            logger.info("Binder service " + service.getName());
        }
    }

    /**
     * Henter ut en registrert injector, eller oppretter den fra en {@link ModuleBuilder} dersom den ikke eksisterer.
     * ModuleBuilderen tvinges til JEE-modus.
     *
     * @param key                      unik identifikator
     * @param moduleBuilder            modulebuilder
     * @return injectoren
     * @deprecated det er bedre å bruke {@link #getInjector(String, Supplier)} siden ModuleBuilder da kan opprettes kun ved behov
     */
    public static synchronized Injector getInjector(String key, ModuleBuilder moduleBuilder) {
        return injectorMap.computeIfAbsent(key, k -> createInjector(moduleBuilder));
    }

    /**
     * Henter ut en registrert injector, eller oppretter den fra en {@link ModuleBuilder} dersom den ikke eksisterer.
     * ModuleBuilderen besørges av
     *
     * @param key                      unik identifikator
     * @param moduleBuilderSupplier    callback som kalles ved behov for å opprette ModuleBuilder
     * @return injectoren
     * @since 2.4.0
     */
    public static synchronized Injector getInjector(String key, Supplier<ModuleBuilder> moduleBuilderSupplier) {
        return injectorMap.computeIfAbsent(key, k -> createInjector(moduleBuilderSupplier.get()));
    }

    private static Injector createInjector(ModuleBuilder moduleBuilder) {
        init();
        return moduleBuilder.setServiceMode(ServiceMode.JEE).buildInjector();
    }

    /**
     * Oppretter en injector ved å kalle en supplier dersom den ikke allerede finnes.
     *
     * @param key               unik identifikator
     * @param supplier          callback som blir kalt dersom injectoren ikke er opprettet
     * @return injector
     * @since 2.4.0
     */
    public static synchronized Injector getInjectorCustom(String key, Supplier<Injector> supplier) {
        return injectorMap.computeIfAbsent(key, k -> supplier.get());
    }
}
