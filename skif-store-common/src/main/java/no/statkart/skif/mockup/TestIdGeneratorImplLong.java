package no.statkart.skif.mockup;

import com.google.inject.Singleton;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Enkel implementasjon av {@link TestIdGenerator} for id-typer som bare benytter {@link java.lang.Long} som verditype.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public class TestIdGeneratorImplLong implements TestIdGenerator<Long> {
    private long nextIdValue = 1;

    public Long getNextIdValue(TestNumber testNumber) {
        final long localIdValue = nextIdValue++;

        return testNumber.getPrefix() * 10000 + localIdValue;
    }

    @Override
    public <I extends BubbleId> I getNextId(TestNumber testNumber, Class<I> idClass) {
        final Long idValue = getNextIdValue(testNumber);

        return BubbleIds.createInstance(idClass, idValue, SnapshotVersion.CURRENT);
    }

}
