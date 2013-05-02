package no.statkart.skif.mockup;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

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

    public <I extends BubbleId> Set<I> getAllIds(Class<I> idClass) {
        Set<I> ids = new HashSet<I>();

        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(idClass)) {
                    field.setAccessible(true); // Foreldreklasser har tydeligvis ikke lov til å tukle med sine barns private deler, men det blåser vi i
                    ids.add(idClass.cast(field.get(this)));
                }
            }
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Could not access field(s)", e);
        }

        return ids;
    }
}
