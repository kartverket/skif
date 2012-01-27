package no.statkart.skif.storetest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService;

/**
 * Implementasjon av {@link TestService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class TestServiceImpl implements TestService {
    @Inject
    private SequenceBlockAllocatorService sequenceBlockAllocatorService;

    @Override
    public int getNextTestNumber() {
        return (int) sequenceBlockAllocatorService.allocateSequenceBlock("TEST_NUMBER", 1);
    }

    @Override
    public void saveSnapshotTransfer(MockupTransfer transfer, SnapshotVersion snapshotVersion) {
        throw new NotImplementedException(); // TODO
    }
}
