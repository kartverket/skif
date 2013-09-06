package no.statkart.skif.wsversioning.wsapi.v2.service;

import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteWSServiceModule;
import no.statkart.skif.util.testsupport.SkifTestCase;
import no.statkart.skif.wsversioning.config.WSVersioningServerModule;
import no.statkart.skif.wsversioning.wsapi.v2.config.WSVersioningV2WSServerModule;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.*;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;
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
public class WSVersioningWSv2Test extends SkifTestCase {
    public WSVersioningWSv2Test() {
        setModuleClass(ClientModule.class);
        setSingleVmServerModuleClass(ServerModule.class);
    }

    public void testFindAlleVeger() throws ServiceException {
        VegServiceWSI vegService = injector.getInstance(VegServiceWSI.class);
        WSVersioningContext context = createContext();

        VegIdList alleVeger = vegService.findAlleVeger(context);
        Assert.assertEquals(alleVeger.getItem().get(0).getValue(), 1, "Første vegId");
        Assert.assertEquals(alleVeger.getItem().get(1).getValue(), 2, "Andre vegId");
    }

    public void testStoreGetTjernslia() throws ServiceException {
        StoreServiceWSI storeService = injector.getInstance(StoreServiceWSI.class);

        VegId vegId = new VegId();
        vegId.setValue(1);
        vegId.setSnapshotVersion(createSnapshotVersionCurrent());

        WSVersioningBubble bubble = storeService.getObject(vegId, createContext());

        Assert.assertTrue(bubble instanceof Veg, "bubble instanceof Veg");

        Veg veg = (Veg) bubble;
        Assert.assertEquals(veg.getId().getValue(), 1L, "Id");
        Assert.assertEquals(veg.getAdressenavn(), "Tjernslia", "Adressenavn");
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
            install(new RemoteWSServiceModule(moduleConfiguration, Arrays.<Class<?>>asList(VegServiceWSI.class, StoreServiceWSI.class)));
        }
    }

    public static class ServerModule extends WSVersioningServerModule {

        public ServerModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected void configure() {
            super.configure();

            install(new WSVersioningV2WSServerModule(moduleConfiguration, getClass().getClassLoader()));
        }
    }
}
