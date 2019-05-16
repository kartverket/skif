package no.statkart.skif.storetest.mapping;

import com.google.inject.Inject;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.*;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.sequence.IdServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.module.common.RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVmXml;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.config.*;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.persistence.SnapshotVersions;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester persistering av mockupsett i SingleVM via XML.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
@Test(groups = "singlevm-required")
public class WSMappingTest extends StoreTestTestCase {
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    Store store;

    public WSMappingTest() {
        setSingleVmServerModuleClass(StoreTestServerModule.class);
        setModuleClass(ClientModule.class);
    }

    public void testWSMapping() {
        // Lager et write sett her for å tvinge generering av testsett ved hver kørsel
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        checkMockupDataSet(mockupFacade);
    }

    private void checkMockupDataSet(StoreTestMockupFacade mockupFacade) {
        X1AA a1 = store.get(mockupFacade.getX1AAMockupFactory().getA1Id());
        Assert.assertEquals(a1.getId(), mockupFacade.getX1AAMockupFactory().getA1Id());
        Assert.assertEquals(a1.getNr(), 1);

        // Sjekker at snapshotVersion settes riktig på id-er
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1().asSnapshotVersion(SnapshotVersions.S2);
        HistSimple histSimple1 = store.get(histSimpleId1);
        Assert.assertEquals(histSimple1.getId(), histSimpleId1);
    }

    public static class ClientModule extends StoreTestClientModule {
        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            ClientModuleStrategyFactory clientModuleStrategyFactory = new ClientModuleStrategyFactory();

            // Kjører dermed på en måte alltid i SINGLE_VM_XML-modus (det finnes kanskje en bedre måte å gjøre dette på)
            clientModuleStrategyFactory.addPrototype(RemoteServiceModule.class, new StrategyTuple<RemoteServiceModuleStrategy>(null, RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVmXml.class, RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVmXml.class));

            return clientModuleStrategyFactory;
        }

        @Override
        protected void configure() {
            final StoreTestMapping mapping = new StoreTestMapper(getProvider(SnapshotVersion.class)).getMapping();
            final StoreTestExceptionMapping exceptionMapping = new StoreTestExceptionMapper(mapping).getMapping();

            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices(), mapping).setExceptionMapping(exceptionMapping));
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreServices().getServices(), mapping)
                    .setExceptionMapping(exceptionMapping)
                    .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
            );
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestStoreUpdateServices().getServices(), mapping)
                    .setExceptionMapping(exceptionMapping)
                    .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
            );
            install(new RemoteServiceModule(moduleConfiguration, new StoreTestSequenceBlockAllocatorServices().getServices(), mapping)
                    .setExceptionMapping(exceptionMapping)
                    .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
            );


            bind(SequenceBlockAllocatorService.class).to(no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService.class);
            bind(IdService.class).to(IdServiceImpl.class);
            bind(Store.class).to(StoreClient.class);

            bind(StoreService.class).to(no.statkart.skif.storetest.service.store.StoreService.class);
            bind(LockService.class).to(no.statkart.skif.storetest.service.lock.LockService.class);

            install(new RemoteServiceModule(moduleConfiguration, new StoreTestTestServices().getServices(), mapping)
                    .setExceptionMapping(exceptionMapping)
                    .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
            );
            bind(no.statkart.skif.service.test.TestdataService.class).to(no.statkart.skif.storetest.service.test.TestdataService.class);
        }
    }
}
