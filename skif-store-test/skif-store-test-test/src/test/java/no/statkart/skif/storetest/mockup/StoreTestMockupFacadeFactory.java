package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacadeFactory;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.SnapshotVersionContext;

/**
 * MockupFacadeFactory for StoreTest tester
 *
 * @author Henrik Fredholm
 * @since 2.3.0
 */
public class StoreTestMockupFacadeFactory extends AbstractMockupFacadeFactory<StoreTestMockupFacade> {
    @Inject
    public StoreTestMockupFacadeFactory(TestdataService testdataService, SnapshotVersionContext snapshotVersionContext) {
        super(StoreTestMockupFacade.class, testdataService, snapshotVersionContext);
//        setDefaultSnapshotVersion(SnapshotVersion.START);
    }
}
