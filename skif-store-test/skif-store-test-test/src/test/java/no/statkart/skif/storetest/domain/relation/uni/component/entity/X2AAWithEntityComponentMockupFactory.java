package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;

import java.util.Collections;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public class X2AAWithEntityComponentMockupFactory extends AbstractMockupFactory {
    private final X2AAWithEntityComponentId<?> a1Id;
    private final X2AAWithEntityComponentId<?> a2Id;
    private final X2AAWithEntityComponentId<?> a3Id;

    @Inject
    X2BBOneMockupFactory X2BBOneMockupFactory;

    @Inject
    X2CCManyMockupFactory X2CCManyMockupFactory;

    @Inject
    public X2AAWithEntityComponentMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        a1Id = getNextId();
        a2Id = getNextId();
        a3Id = getNextId();
    }

    private X2AAWithEntityComponentId getNextId() {
        return getNextId(X2AAWithEntityComponentId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createX2AA(a1Id, 1, null, X2BBOneMockupFactory.getB2Id()));
        store.insert(createX2AA(a2Id, 2, null, X2BBOneMockupFactory.getB3Id()));
        store.insert(createX2AA(a3Id, 3, null, X2BBOneMockupFactory.getB3Id(), ImmutableSet.of(X2CCManyMockupFactory.getC2Id(), X2CCManyMockupFactory.getC3Id())));
    }

    private X2AAWithEntityComponent createX2AA(X2AAWithEntityComponentId<?> aId, int nr, String text, X2BBOneId someBBId) {
        return createX2AA(aId, nr, text, someBBId, Collections.< X2CCManyId <?>>emptySet());
    }

    private X2AAWithEntityComponent createX2AA(X2AAWithEntityComponentId<?> aId, int nr, String text, X2BBOneId someBBId, Set<X2CCManyId<?>> ccManyIds) {
        X2AAWithEntityComponent a = new X2AAWithEntityComponent();
        a.setId(aId);
        a.setNr(nr);
        a.setText(text);
        a.setEntityComponentOne(new X2EntityComponentOne());
        a.getEntityComponentOne().setSomeBBId(someBBId);
        a.getEntityComponentOne().setSomeCCsIds(ccManyIds);
        return a;
    }

    public X2AAWithEntityComponentId<?> getA1Id() {
        return a1Id;
    }

    public X2AAWithEntityComponentId<?> getA2Id() {
        return a2Id;
    }

    public X2AAWithEntityComponentId<?> getA3Id() {
        return a3Id;
    }
}
