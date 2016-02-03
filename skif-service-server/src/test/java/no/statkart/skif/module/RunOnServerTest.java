package no.statkart.skif.module;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import no.statkart.skif.service.*;
import no.statkart.skif.service.module.client.RunOnRemoteServerBuilder;
import org.testng.annotations.Test;

import java.util.List;

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
        moduleBuilder.setModuleClass(RunOnRemoteServerTestClientModule.class);
        moduleBuilder.setSingleVmServerModuleClass(RunOnRemoteServerTestServerModule.class);

        final Injector injector = moduleBuilder.buildInjector();
        final RunOnServerWithTxBeanManagedService service = injector.getInstance(RunOnServerWithTxBeanManagedService.class);


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
        final RunOnServerWithTxBeanManagedService service = builder.buildBeanManagedService();
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

    public void testBuildContainerManagedNotSupportedTransactionService() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        final RunOnServerWithTxNotSupportedService service = builder.buildContainerManagedNotSupportedTranactionService();
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

    public void testBuildContainerManagedRequiresNewTransactionService() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        final RunOnServerWithTxRequiresNewService service = builder.buildContainerManagedRequiresNewTranactionService();
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

    public void testBuildContainerManagedRequiredTransactionService() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        final RunOnServerWithTxRequiredService service = builder.buildContainerManagedRequiredTranactionService();
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

    public void testBuildStandardGuiceModule() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class);
        Injector injector = Guice.createInjector(builder.buildModule());

        final RunOnServerWithTxBeanManagedService service =injector.getInstance(RunOnServerWithTxBeanManagedService.class);

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


    public void testBuildWithExtensionModule() {
        final RunOnRemoteServerBuilder builder = new RunOnRemoteServerBuilder(RunOnRemoteServerTestServerModule.class, TestExtModule.class, TestExtEJBServiceChainProxyHandler.class);
        Injector injector = Guice.createInjector(builder.buildModule());

        final RunOnServerWithTxBeanManagedService service =injector.getInstance(RunOnServerWithTxBeanManagedService.class);

        Object result = service.run(new RunOnServerMethod() {
            @Inject
            @Named("modulename")
            private String moduleName;

            @Inject
            @Named("testExt2")
            List list;

            @Override
            public Object run() {
                return moduleName + " list.get(0): " + list.get(0);
            }
        });
        assertEquals(result, "TestServerModule list.get(0): Inserted by TestExtEJBServiceChainProxyHandler" );
    }

}
