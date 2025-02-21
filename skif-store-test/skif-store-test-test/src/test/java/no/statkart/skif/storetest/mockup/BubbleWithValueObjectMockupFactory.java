package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObjectId;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class BubbleWithValueObjectMockupFactory extends AbstractMockupFactory {
    private final BubbleWithValueObjectId<?> withNullBeloepId;
    private final BubbleWithValueObjectId<?> withSameBeloepId;
    private final BubbleWithValueObjectId<?> withBeloepSetId;
    private final BubbleWithValueObjectId<?> withBeloepSetId2;
    private final BeloepValueObject beloepNOK1WithOutText = new BeloepValueObject("NOK", 1, null);
    private final BeloepValueObject beloepNOK1WithText = new BeloepValueObject("NOK", 1, "I have text");
    private final BeloepValueObject beloepNOK1 = new BeloepValueObject("NOK", 1, null);
    private final BeloepValueObject beloepDKR1 = new BeloepValueObject("DKR", 1, null);
    private final BeloepValueObject beloepSEK20 = new BeloepValueObject("SEK", 20, null);

    @Inject
    public BubbleWithValueObjectMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNullBeloepId = getNextId();
        withSameBeloepId = getNextId();
        withBeloepSetId = getNextId();
        withBeloepSetId2 = getNextId();
    }

    private BubbleWithValueObjectId<?> getNextId() {
        return getNextId(BubbleWithValueObjectId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithKode(withNullBeloepId, 1, null, null, null));
        BeloepValueObject sharedBeloep = CopyHelper.copy(beloepNOK1WithText);
        store.insert(createBubbleWithKode(withSameBeloepId, 1, null, sharedBeloep, sharedBeloep));
        store.insert(createBubbleWithKode(withBeloepSetId, 1, null, beloepNOK1, beloepDKR1, ImmutableSet.of(beloepNOK1, beloepDKR1)));
        store.insert(createBubbleWithKode(withBeloepSetId2, 2, "Another set with beløp", beloepNOK1, beloepDKR1, ImmutableSet.of(beloepDKR1, beloepSEK20)));
    }

    private BubbleWithValueObject createBubbleWithKode(BubbleWithValueObjectId<?> id, int nr, String text, @Nullable BeloepValueObject a, @Nullable BeloepValueObject b) {
        return createBubbleWithKode(id, nr, text, a, b, Sets.<BeloepValueObject>newHashSet());
    }
    private BubbleWithValueObject createBubbleWithKode(BubbleWithValueObjectId<?> id, int nr, String text, @Nullable BeloepValueObject a, @Nullable BeloepValueObject b, Set<BeloepValueObject> beloepSet) {
        BubbleWithValueObject obj = new BubbleWithValueObject();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setA(a);
        obj.setB(b);
        obj.setBeloepSet(beloepSet);
       return obj;
    }

    public BubbleWithValueObjectId<?> getWithNullBeloepId() {
        return withNullBeloepId;
    }

    public BubbleWithValueObjectId<?> getWithSameBeloepId() {
        return withSameBeloepId;
    }

    public BubbleWithValueObjectId<?> getWithBeloepSetId() {
        return withBeloepSetId;
    }

    public BeloepValueObject getBeloepNOK1WithOutText() {
        return beloepNOK1WithOutText;
    }

    public BeloepValueObject getBeloepNOK1WithText() {
        return beloepNOK1WithText;
    }

    public BeloepValueObject getBeloepNOK1() {
        return beloepNOK1;
    }

    public BeloepValueObject getBeloepDKR1() {
        return beloepDKR1;
    }

    public BeloepValueObject getBeloepSEK20() {
        return beloepSEK20;
    }

    public BubbleWithValueObjectId<?> getWithBeloepSetId2() {
        return withBeloepSetId2;
    }
}
