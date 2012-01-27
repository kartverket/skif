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

    @Override
    public <I extends BubbleId> I getNextId(TestNumber testNumber, Class<I> idClass) {
        final long localIdValue = nextIdValue++;

        final long idValue = testNumber.getPrefix() * 10000 + localIdValue;

        return BubbleIds.createInstance(idClass, idValue, SnapshotVersion.CURRENT);
    }

}
