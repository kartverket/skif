package no.statkart.skif.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Enkel implementasjon av {@link IdService} for generering av test-id'er for id-typer
 * som alle benytter {@link Long} som verditype. Test-id'en som genereres bruker
 * {@code TestNumber} som prefix og har en øvre grense på {@code MAX_ANTALL} id'er som
 * kan genereres med samme prefix.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Singleton
public class TestIdServiceLong implements IdService {
    private static final long MAX_ANTALL_ID = 10000L;
    private final TestNumber testNumber;
    private long nextIdValue = 10000;

    @Inject
    public TestIdServiceLong(TestNumber testNumber) {
        this.testNumber = testNumber;
    }

    @Override
    public <T extends BubbleId<?>> Long getNextIdValue(Class<T> idClass) {
        final long localIdValue = nextIdValue++;
        return testNumber.getPrefix() * MAX_ANTALL_ID + localIdValue;
    }

    @Override
    public <T extends BubbleId<?>> T getNextId(Class<T> idClass) {
        final Long idValue = getNextIdValue(idClass);
        return BubbleIds.createInstance(idClass, idValue, SnapshotVersion.CURRENT);
    }

    @Override
    public int getBlockSize() {
        return 1;
    }

    @Override
    public void setBlockSize(int blockSize) {
        // no-op, har ingen effekt
    }

    @Override
    public void clear() {
        // no-op, har ingen effekt
    }
}
