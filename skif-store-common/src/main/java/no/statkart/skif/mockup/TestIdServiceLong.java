package no.statkart.skif.mockup;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Enkel implementasjon av {@link IdService} for generering av test-id'er for id-typer
 * som alle benytter {@link Long} som verditype. Test-id'en som genereres bruker
 * {@link TestNumber} som prefix og har en øvre grense på {@link #PREFIX_FACTOR} id'er som
 * kan genereres med samme prefix.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Singleton
public class TestIdServiceLong implements IdService {
    //bestemmer plassering av prefix for testNumber
    private static final int PREFIX_FACTOR = 1_000_000;

    private final TestNumber testNumber;
    private int localIdValue = 100_000; //lokal suffix av id verdi - setter av et område tiltenkt hardkodede verdier i mockupsettet

    @Inject
    public TestIdServiceLong(TestNumber testNumber) {
        this.testNumber = testNumber;
    }

    /**
     * Beregner id-verdi for mockupsekvens.
     * Et mockupsett får tildelt egen sekvens:
     * <pre>{@code
     *
     *       1PPPP MMMMMM
     *     <prefix> <id-value>
     *
     * }</pre>
     *
     * PS: anbefalt størrelse på returnert verdi bør overstige {@link Integer#MAX_VALUE} - dvs 11 siffer eller mer.
     *  - Dette for å verifisere at modell kan håndtere long verdier og at man ikke feilaktig har brukt int for id-felter.
     *
     * @see TestdataService#getNextTestNumber() implementasjon av getNextTestNumber() for konfigurasjon av prefix verdi
     * @see TestNumber#getPrefix() getPrefix() for beregning av prefix verdi
     */
    public long calculateIdValue(int localIdValue) {
        return (testNumber.getPrefix() * PREFIX_FACTOR) + localIdValue;
    }

    @Override
    public <T extends BubbleId<?>> Long getNextIdValue(Class<T> idClass) {
        localIdValue++;
        Preconditions.checkArgument(localIdValue < PREFIX_FACTOR, "Mockup overflow - count exceeds %s", PREFIX_FACTOR);
        return calculateIdValue(localIdValue);
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
