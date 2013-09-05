package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteWSServiceModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.util.testsupport.SkifTestCase;
import no.statkart.skif.wsversioning.config.WSVersioningCompatServicesV1;
import no.statkart.skif.wsversioning.config.WSVersioningServerModule;
import no.statkart.skif.wsversioning.config.WSVersioningServicesV1;
import no.statkart.skif.wsversioning.wsapi.v1.config.WSVersioningV1Module;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.*;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;
import no.statkart.skif.wsversioning.wsapi.v1.exception.mapping.WSVersioningExceptionMapper;
import no.statkart.skif.wsversioning.wsapi.v1.mapping.WSVersioningMapper;
import no.statkart.skif.wsversioning.wsapi.v1.mapping.WSVersioningMapping;
import no.statkart.skif.wsversioning.wsapi.v1.mapping.WSVersioningServiceContextMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Locale;

/**
 * Tester at webservicene returnerer korrekt mappede objekter.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test
public class WSVersioningWSv1Test extends SkifTestCase {
    public WSVersioningWSv1Test() {
        setModuleClass(ClientModule.class);
        setSingleVmServerModuleClass(ServerModule.class);
    }

    public void testFindAlleGater() throws ServiceException {
        GateServiceWSI gateService = injector.getInstance(GateServiceWSI.class);
        WSVersioningContext context = createContext();

        GateIdList alleGater = gateService.findAlleGater(context);
        Assert.assertEquals(alleGater.getItem().get(0).getValue(), 1, "Første gateId");
        Assert.assertEquals(alleGater.getItem().get(1).getValue(), 2, "Andre gateId");
    }

    public void testStoreGetTjernslia() throws ServiceException {
        StoreServiceWSI storeService = injector.getInstance(StoreServiceWSI.class);

        GateId gateId = new GateId();
        gateId.setValue(1);
        gateId.setSnapshotVersion(createSnapshotVersionCurrent());

        WSVersioningBubble bubble = storeService.getObject(gateId, createContext());

        Assert.assertTrue(bubble instanceof Gate, "bubble instanceof Gate");

        Gate gate = (Gate) bubble;
        Assert.assertEquals(gate.getId().getValue(), 1L, "Id");
        Assert.assertEquals(gate.getGatenavn(), "Tjernslia", "Adressenavn");
    }

    private WSVersioningContext createContext() {
        WSVersioningContext context = new WSVersioningContext();
        context.setLocale(Locale.getDefault().toString());
        context.setClientVersion("2.0");
        return context;
    }

    private SnapshotVersion createSnapshotVersionCurrent() {
        SnapshotVersion snapshotVersion = new SnapshotVersion();
        snapshotVersion.setTime(253370761200000L);
        return snapshotVersion;
    }

    public static class ClientModule extends SkifModule {

        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteWSServiceModule(moduleConfiguration, Arrays.<Class<?>>asList(GateServiceWSI.class, StoreServiceWSI.class)));
        }
    }

    public static class ServerModule extends WSVersioningServerModule {

        public ServerModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected void configure() {
            super.configure();

            install(new WSVersioningV1Module(moduleConfiguration, getClass().getClassLoader()));
        }
    }
}
