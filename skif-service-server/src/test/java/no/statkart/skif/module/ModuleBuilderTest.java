package no.statkart.skif.module;

import com.google.inject.*;
import com.google.inject.name.Names;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.MapConfiguration;
import no.statkart.skif.config.SystemConfiguration;
import no.statkart.skif.config.internal.ConfigurationUtils;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

@Test
public class ModuleBuilderTest {
    private HashMap builderConfig;
    private SystemConfiguration systemConfiguration;

    /**
     * Setter opp en klon av SystemConfiguration. Må bruke en klon slik at endringer gjort på system configuration
     * i en test ikke påvirket andre tester.
     */
    @BeforeMethod
    public void setupSystemConfigurationClone() {
        systemConfiguration = (SystemConfiguration) ConfigurationUtils.cloneConfiguration(new SystemConfiguration());
    }

    // Configuration c = ConfigurationFactory.create()
    // configuration c = ConfigurationFactory.cateate(new String[]{skif-default.properties, historikk-default.properties}, new String[] {skif.properties, histrokk.properties})

    public void testMinimaltOppsettStandaloneModule() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestModule.class);
        Module m = builder.buildModule();
        assertSame(m.getClass(), TestModule.class);

        Module m2 = builder.buildModule();
        assertNotSame(m, m2);

        Injector injector = Guice.createInjector(m);
        Injector injector2 = Guice.createInjector(m2);
        final Configuration config = injector.getInstance(Configuration.class);
        final Configuration config2 = injector2.getInstance(Configuration.class);
        assertNotSame(config, config2);

    }

    public void testMinimaltOppsettStandaloneModuleMedConfiguration() {
        Configuration configuration = new MapConfiguration();
        configuration.setProperty("key1", "value1");
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestModule.class);
        builder.setConfiguration(configuration);

        Injector injector = builder.buildInjector();
        final Configuration config = injector.getInstance(Configuration.class);

        assertEquals(config.getString("key1"), "value1");
        assertNull(config.getString("key2"));
    }

    public void testOppsettMedDeltConfiguration() {
        Configuration configuration = new MapConfiguration();
        configuration.setProperty("key1", "value1");
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestModule.class);
        builder.setConfiguration(configuration);

        Injector injector = builder.buildInjector();
        Injector injector2 = builder.buildInjector();

        // Test at hver modul har sin egen configuration instans (bare underliggende elementer der delt)
        final Configuration config = injector.getInstance(Configuration.class);
        final Configuration config2 = injector2.getInstance(Configuration.class);
        assertNotSame(config, config2);

        // Begge moduler ser samme properties
        assertEquals(config.getString("key1"), "value1");
        assertEquals(config2.getString("key1"), "value1");
        assertNull(config.getString("key2"));
        assertNull(config2.getString("key2"));

        // Test at underliggende konfigurasjon er delt
        configuration.setProperty("key2", "value2");
        assertEquals(config.getString("key2"), "value2");
        assertEquals(config2.getString("key2"), "value2");

        // Nye Properties blir tilgjengelig på tvers av moduler
        config.setProperty("key2", "value2-changed");
        assertEquals(config2.getString("key2"), "value2-changed");
    }

    public void testDeltConfigurationOppsettMedSystemProperties() {
        systemConfiguration.setProperty("key1", "system-value1");
        systemConfiguration.setProperty("key2", "system-value2");
        ModuleBuilder builder = new ModuleBuilder(systemConfiguration);
        builder.setModuleClass(TestModule.class);
        Configuration configuration = new MapConfiguration();
        configuration.setProperty("key1", "value1");
        builder.setConfiguration(configuration);

        Injector injector = builder.buildInjector();
        Injector injector2 = builder.buildInjector();

        // Test at hver modul har sin egen configuration instans (bare underliggende elementer der delt)
        final Configuration config = injector.getInstance(Configuration.class);
        final Configuration config2 = injector2.getInstance(Configuration.class);
        assertNotSame(config, config2);

        // Begge moduler ser samme properties og at system properties overstyrer
        assertEquals(config.getString("key1"), "system-value1");
        assertEquals(config2.getString("key1"), "system-value1");

        // Nye Properties blir tilgjengelig på tvers av moduler og endre ikke på opprinnelig system properties
        config.setProperty("key2", "value2-changed");
        assertEquals(config2.getString("key2"), "value2-changed");
        assertEquals(systemConfiguration.getString("key2"), "system-value2");

        // Nye moduler dele forsatt configurasjon med tidligere moduler så lenge ny configuration ikke er satt
        final Configuration config3 = builder.buildInjector().getInstance(Configuration.class);
        assertEquals(config3.getString("key2"), "value2-changed");

        // Sett ny configurasjon på builder. Module vil da ikke dele properties med forrige moduler
        builder.setConfiguration(null);
        final Configuration config4 = builder.buildInjector().getInstance(Configuration.class);
        assertEquals(config4.getString("key2"), "system-value2");
    }

    public void testMinimaltOppsettStandaloneModuleMedConfigurationFile() {
        systemConfiguration.setProperty("key2", "system-value2");
        ModuleBuilder builder = new ModuleBuilder(systemConfiguration);
        builder.setModuleClass(TestModule.class);
        builder.setConfigurationFilename( getClass().getResource("ModuleBuilderTest.properties").toString());
        final Injector injector = builder.buildInjector();
        final Configuration config = injector.getInstance(Configuration.class);
        assertEquals(config.getString("key1"), "value1");
        assertEquals(config.getString("key2"), "system-value2");

        builder.setConfigurationFilename( getClass().getResource("ModuleBuilderTest2.properties").toString());
        final Injector injector2 = builder.buildInjector();
        final Configuration config2 = injector2.getInstance(Configuration.class);
        assertEquals(config2.getString("key1"), "valueA");
        assertEquals(config2.getString("key2"), "system-value2");
    }

    public void testMinimaltOppsettClientServerModule() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestClientModule.class);
        builder.setSingleVmServerModuleClass(TestServerModule.class);
        builder.setServiceMode(ServiceMode.SINGLE_VM);
        Injector injector1 = builder.buildInjector();
        assertEquals(injector1.getInstance(Key.get(String.class, Names.named("modulename"))), "TestClientModule");
        final SingleVmServer singleVmServer = injector1.getInstance(SingleVmServer.class);
        assertEquals(singleVmServer.getInjector().getInstance(Key.get(String.class, Names.named("modulename"))), "TestServerModule");
    }

    public void testClientServerOppsettMedDeltServer() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestClientModule.class);
        builder.setSingleVmServerModuleClass(TestServerModule.class);
        builder.setServiceMode(ServiceMode.SINGLE_VM);
        Injector injector1 = builder.buildInjector();
        Injector injector2 = builder.buildInjector();

        final SingleVmServer singleVmServer1 = injector1.getInstance(SingleVmServer.class);
        final SingleVmServer singleVmServer2 = injector2.getInstance(SingleVmServer.class);
        assertSame(singleVmServer1.getInjector(), singleVmServer2.getInjector());
    }

    public void testClientServerOppsettMedIkkeDeltServer() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestClientModule.class);
        builder.setSingleVmServerModuleClass(TestServerModule.class);
        builder.setServiceMode(ServiceMode.SINGLE_VM);
        Injector injector1 = builder.buildInjector();
        // Setter ny konfigurasjon. Skal føre til at builderen lager egen server modul
        builder.setSingleVmServerConfiguration(null);
        Injector injector2 = builder.buildInjector();

        final SingleVmServer singleVmServer1 = injector1.getInstance(SingleVmServer.class);
        final SingleVmServer singleVmServer2 = injector2.getInstance(SingleVmServer.class);
        assertNotSame(singleVmServer1.getInjector(), singleVmServer2.getInjector());
    }


    public void testUdvidetOppsettViaSetters() {
        Configuration clientProperties= new MapConfiguration();
        clientProperties.setProperty("key1", "clientValue1" ) ;
        clientProperties.setProperty("clientKey2", "clientValue2" ) ;

        Configuration serverProperties= new MapConfiguration();
        serverProperties.setProperty("key1", "serverValue1" ) ;
        serverProperties.setProperty("serverKey2", "serverValue2" ) ;

        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestClientModule.class);
        builder.setConfiguration(clientProperties);
        builder.setModuleStrategyFactoryClass(ClientModuleStrategyFactory.class);
        builder.setSingleVmServerModuleClass(TestServerModule.class);
        builder.setSingleVmServerModuleStrategyFactoryClass(ServerModuleStrategyFactory.class);
        builder.setSingleVmServerConfiguration(serverProperties);;
        builder.setServiceMode(ServiceMode.SINGLE_VM);

        Module module = builder.buildModule();
        assertSame(module.getClass(), TestClientModule.class);

        Injector injector = builder.buildInjector();
        Configuration config = injector.getInstance(Configuration.class);
        assertEquals(config.getString("key1"), "clientValue1");
        assertEquals(config.getString("clientKey2"), "clientValue2");

        final SingleVmServer singleVmServer = injector.getInstance(SingleVmServer.class);
        Injector serverInjector = builder.getSingleVmServerInjector();
        assertSame(serverInjector, singleVmServer.getInjector());

        config = serverInjector.getInstance(Configuration.class);
        assertEquals(config.getString("key1"), "serverValue1");
        assertEquals(config.getString("serverKey2"), "serverValue2");
    }

    public void testSingleVmPaavirkerIkkeStandaloneModuler() {
        ModuleBuilder builder = new ModuleBuilder();
        builder.setModuleClass(TestModule.class);
        builder.setServiceMode(ServiceMode.SINGLE_VM);
        Injector injector1 = builder.buildInjector();
        assertEquals(injector1.getInstance(Key.get(String.class, Names.named("modulename"))), "TestModule");
        assertTrue(injector1.findBindingsByType(new TypeLiteral<SingleVmServer>(){}).isEmpty()) ;
    }

}
