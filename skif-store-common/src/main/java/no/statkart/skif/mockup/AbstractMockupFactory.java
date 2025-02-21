package no.statkart.skif.mockup;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Baseklasse for mockupfactories. Holder på sentrale ting som store og testnummer.
 * SKIF-applikasjoner definerer opp en eller flere egne implementasjoner av denne i en facade-implementasjon.
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

    /**
     * Finner alle definerte id-er for gitt klasse. Obs! Denne returnerer ikke for subklasser.
     *
     * @param idClass    id-klasse som skal finnes
     * @return id-ene
     */
    public <I extends BubbleId> Set<I> getAllIds(Class<I> idClass) {
        return getAllIds(idClass, false);
    }

    /**
     * Finner alle definerte id-er for gitt klasse.
     *
     * @param idClass            id-klasse som skal finnes
     * @param includeSubTypes    om subklasser av gitt id-klasse også skal returneres
     * @return id-ene
     */
    public <I extends BubbleId> Set<I> getAllIds(Class<I> idClass, boolean includeSubTypes) {
        Set<I> ids = new LinkedHashSet<I>(); // Ønsker å bevare rekkefølge samt gjøre funksjonen deterministisk (dvs at rekkefølgen ikke avhenger av hashkoden til id-verdien)

        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                if ((includeSubTypes && idClass.isAssignableFrom(field.getType())) || (!includeSubTypes && field.getType().equals(idClass))) {
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
