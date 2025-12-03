package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteWSServiceModule;
import no.statkart.skif.util.testsupport.SkifTestCase;
import no.statkart.skif.wsversioning.config.WSVersioningServerModule;
import no.statkart.skif.wsversioning.config.WSVersioningV1ServerModule;
import no.statkart.skif.wsversioning.wsapi.v1.config.WSVersioningV1WSServerModule;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.Gate;
import no.statkart.skif.wsversioning.wsapi.v1.domain.GateId;
import no.statkart.skif.wsversioning.wsapi.v1.domain.GateIdList;
import no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubble;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;
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
        GateService gateService = injector.getInstance(GateService.class);
        WSVersioningContext context = createContext();

        GateIdList alleGater = gateService.findAlleGater(context);
        Assert.assertEquals(alleGater.getItem().get(0).getValue(), 1, "Første gateId");
        Assert.assertEquals(alleGater.getItem().get(1).getValue(), 2, "Andre gateId");
    }

    public void testStoreGetTjernslia() throws ServiceException {
        StoreService storeService = injector.getInstance(StoreService.class);

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
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory(ModuleConfiguration moduleConfiguration) {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteWSServiceModule(moduleConfiguration, Arrays.<Class<?>>asList(GateService.class, StoreService.class)));
        }
    }

    public static class ServerModule extends WSVersioningServerModule {

        public ServerModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected void configure() {
            super.configure();

            install(new WSVersioningV1ServerModule(moduleConfiguration));
            install(new WSVersioningV1WSServerModule(moduleConfiguration, getClass().getClassLoader()));
        }
    }
}
