package no.statkart.skif.module;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.module.client.RunOnRemoteServerBuilder;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class RunOnServerTest {
    public void testManualSetup() {
        final ModuleBuilder moduleBuilder = new ModuleBuilder();
        moduleBuilder.setSingleVm(true);
        moduleBuilder.setModuleClass(RunOnRemoteServerClientModule.class);
        moduleBuilder.setSingleVmServerModuleClass(RunOnRemoteServerTestServerModule.class);

        final Injector injector = moduleBuilder.buildInjector();
        final BeanManagedTransactionRunOnServerService service = injector.getInstance(BeanManagedTransactionRunOnServerService.class);


        Object result = service.run(new RunOnServerMethod() {
            @Inject
            @Named("modulename")
            private String moduleName;
            @Override
            public Object run() {
                return moduleName;
            }
        });
        assertEquals(result, "TestServerModule");
    }

    public void testBuildBeanManagedService() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        final BeanManagedTransactionRunOnServerService service = builder.buildBeanManagedService();
        Object result = service.run(new RunOnServerMethod() {
            @Inject
            @Named("modulename")
            private String moduleName;
            @Override
            public Object run() {
                return moduleName;
            }
        });
        assertEquals(result, "TestServerModule" );
    }

    public void testBuildContainerManagedService() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        final ContainerManagedTransactionRunOnServerService service = builder.buildContainerManagedService();
        Object result = service.runInTxNotSupported(new RunOnServerMethod() {
            @Inject
            @Named("modulename")
            private String moduleName;

            @Override
            public Object run() {
                return moduleName;
            }
        });
        assertEquals(result, "TestServerModule" );
    }

    public void testBuildStandardGuideModule() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        Injector injector = Guice.createInjector(builder.buildModule());

        final BeanManagedTransactionRunOnServerService service =injector.getInstance(BeanManagedTransactionRunOnServerService.class);

        Object result = service.run(new RunOnServerMethod() {
            @Inject
            @Named("modulename")
            private String moduleName;

            @Override
            public Object run() {
                return moduleName;
            }
        });
        assertEquals(result, "TestServerModule" );
    }

}
