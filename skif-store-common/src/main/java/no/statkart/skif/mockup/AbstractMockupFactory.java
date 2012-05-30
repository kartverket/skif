package no.statkart.skif.mockup;

import com.google.inject.Inject;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;

/**
 * Baseklasse for mockupfactories. Holder på sentrale ting som store og testnummer.
 * SKIF-applikasjonerer definerer opp en eller flere egne implementasjoner av denne i en facade-implementasjon.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class AbstractMockupFactory {
    protected final MockupStore store;
    private final TestNumber testNumber;
    private final IdService testIdGenerator;

    protected AbstractMockupFactory(MockupStore store, TestNumber testNumber) {
        this.store = store;
        this.testNumber = testNumber;
        this.testIdGenerator = store.getInstance(IdService.class);
    }

    public TestNumber getTestNumber() {
        return testNumber;
    }

    protected <I extends BubbleId> I getNextId(Class<I> idClass) {
        return testIdGenerator.getNextId(idClass);
    }

    /**
     * Oppretter alle mockup-objektene denne factory er ansvarlig for og putter dem i mockupens store.
     */
    public abstract void createAllMockups();
}
