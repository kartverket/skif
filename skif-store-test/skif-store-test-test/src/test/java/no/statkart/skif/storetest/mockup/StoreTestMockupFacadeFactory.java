package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mockup.AbstractMockupFacadeFactory;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.SnapshotVersion;

/**
 * MockupFacadeFactory for StoreTest tester
 *
 * @author Henrik Fredholm
 * @since 2.3.0
 */
public class StoreTestMockupFacadeFactory extends AbstractMockupFacadeFactory<StoreTestMockupFacade> {
    @Inject
    public StoreTestMockupFacadeFactory(TestdataService testdataService, Provider<ServiceContext> serviceContextProvider) {
        super(StoreTestMockupFacade.class, testdataService, serviceContextProvider);
//        setDefaultSnapshotVersion(SnapshotVersion.START);
    }
}
