package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.*;
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
    private final BubbleWithValueObjectId<?> withSharedBeloepId;
    private final BubbleWithValueObjectId<?> withBeloepSetId;
    private final BeloepValueObject beloepNOK1WithOutText = new BeloepValueObject("NOK", 1, null);
    private final BeloepValueObject beloepNOK1WithText = new BeloepValueObject("NOK", 1, "I have text");
    private final BeloepValueObject beloepNOK1 = new BeloepValueObject("NOK", 1, null);
    private final BeloepValueObject beloepDKK1 = new BeloepValueObject("DKK", 1, null);
    private final BeloepValueObject beloepSKR1 = new BeloepValueObject("SKR", 1, null);

    @Inject
    public BubbleWithValueObjectMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNullBeloepId = getNextId();
        withSharedBeloepId = getNextId();
        withBeloepSetId = getNextId();
    }

    private BubbleWithValueObjectId<?> getNextId() {
        return getNextId(BubbleWithValueObjectId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithKode(withNullBeloepId, 1, null, null, null));
        BeloepValueObject sharedBeloep = CopyHelper.copy(beloepNOK1WithText);
        store.insert(createBubbleWithKode(withSharedBeloepId, 1, null, sharedBeloep, sharedBeloep));
        store.insert(createBubbleWithKode(withBeloepSetId, 1, null, beloepNOK1, beloepDKK1, ImmutableSet.of(beloepNOK1, beloepDKK1)));
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

    public BubbleWithValueObjectId<?> getWithSharedBeloepId() {
        return withSharedBeloepId;
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

    public BeloepValueObject getBeloepDKK1() {
        return beloepDKK1;
    }

    public BeloepValueObject getBeloepSKR1() {
        return beloepSKR1;
    }
}
