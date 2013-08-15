package no.statkart.skif.storetest.domain.relation;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacadeFactory;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.SnapshotVersion;

/**
 * MockupFacadeFactory for tester som bruker mockup data fra relation subdomenet.
 *
 * <P>Denne factory kan kun brukes av tester som krever singlevm da domeneklassene ikke støtter WS-mapping
 *
 * @author Henrik Fredholm
 * @since 2.3.0
 */
public class RelationMockupFacadeFactory extends AbstractMockupFacadeFactory<RelationMockupFacade> {
    @Inject
    public RelationMockupFacadeFactory(TestdataService testdataService) {
        super(RelationMockupFacade.class, testdataService);
        setDefaultSnapshotVersion(SnapshotVersion.START);
    }
}
