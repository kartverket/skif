package no.statkart.skif.storetest.mockupframework;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacadeFactory;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.service.test.TestdataService;

/**
 * MockupFacadeFactory for isolert testing av mockup rammeverket.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public class MockupFacadeFactory extends AbstractMockupFacadeFactory<MockupFacade> {
    @Inject
    public MockupFacadeFactory(TestdataService testdataService, Provider<ServiceContext> serviceContextProvider) {
        super(MockupFacade.class, testdataService, serviceContextProvider);

        setDefaultSnapshotVersion(SnapshotVersion.START);
    }
}
