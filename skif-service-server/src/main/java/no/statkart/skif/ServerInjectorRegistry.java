package no.statkart.skif;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.module.ModuleBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse som holder på registrerte skif server injectors. Denne klasse gjør det mulig å ha mer
 * enn en server injector per applikasjon om ønskelig.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerInjectorRegistry {
    private static final Map<Object, Injector> injectorMap = new HashMap<>();

    /**
     * Henter ut en registrert injector, eller oppretter den fra en {@link ModuleBuilder} dersom den ikke eksisterer.
     * ModuleBuilderen tvinges til JEE-modus.
     *
     * @param key                      unik identifikator
     * @param moduleBuilder            modulebuilder
     * @return injectoren
     * @deprecated det er bedre å bruke {@link #getInjector(String, com.google.common.base.Supplier)} siden ModuleBuilder da kan opprettes kun ved behov
     */
    public static synchronized Injector getInjector(String key, ModuleBuilder moduleBuilder) {
        Injector injector = injectorMap.get(key);
        if (injector == null) {
            injector = createInjector(moduleBuilder);
            injectorMap.put(key, injector);
        }
        return injector;
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
        Injector injector = injectorMap.get(key);
        if (injector == null) {
            injector = createInjector(moduleBuilderSupplier.get());
            injectorMap.put(key, injector);
        }
        return injector;
    }

    private static Injector createInjector(ModuleBuilder moduleBuilder) {
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
        Injector injector = injectorMap.get(key);
        if (injector == null) {
            injector = supplier.get();
            injectorMap.put(key, injector);
        }
        return injector;
    }
}
