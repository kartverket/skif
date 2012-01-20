package no.statkart.skif.storetest.service.store;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.config.StoreTestServerModule5;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import sun.security.pkcs11.Secmod;

import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreTestServerModeTest {
    static interface ServiceAction {
        void runInServiceMode(Object obj, Method m, Object... args);
    }
    public void test() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(StoreTestServerModule5.class);
        builder.setConfigurationFilename("skif.properties");
        builder.setSingleVm(true);
        Module m = builder.buildModule();
        Injector injector = com.google.inject.Guice.createInjector(m, new AbstractModule() {
            @Override
            protected void configure() {
            }
        });



    }
}
