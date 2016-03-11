package no.statkart.skif.storetest.domain.relation;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCManyId;

import java.util.Collections;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class X1AAMockupFactory extends AbstractMockupFactory {
    private final X1AAId<?> a1Id;
    private final X1AAId<?> a2Id;
    private final X1AAId<?> a3Id;

    @Inject
    X1BBOneMockupFactory x1BBOneMockupFactory;

    @Inject
    X1CCManyMockupFactory x1CCManyMockupFactory;

    @Inject
    public X1AAMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        a1Id = getNextId();
        a2Id = getNextId();
        a3Id = getNextId();
    }

    private X1AAId getNextId() {
        return getNextId(X1AAId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createX1AA(a1Id, 1, null, x1BBOneMockupFactory.getB2Id()));
        store.insert(createX1AA(a2Id, 2, null, x1BBOneMockupFactory.getB3Id()));
        store.insert(createX1AA(a3Id, 3, null, x1BBOneMockupFactory.getB3Id(), ImmutableSet.of(x1CCManyMockupFactory.getC2Id(), x1CCManyMockupFactory.getC3Id())));
    }

    private X1AA createX1AA(X1AAId<?> aId, int nr, String text, X1BBOneId someBBId) {
        return createX1AA(aId, nr, text, someBBId, Collections.< X1CCManyId <?>>emptySet());
    }

    private X1AA createX1AA(X1AAId<?> aId, int nr, String text, X1BBOneId someBBId, Set<X1CCManyId<?>> ccManyIds) {
        X1AA a = new X1AA();
        a.setId(aId);
        a.setNr(nr);
        a.setText(text);
        a.setSomeBBId(someBBId);
        a.setSomeCCsIds(ccManyIds);
        a.setUniqueOnX1AA(String.format("Unique: [%d,%d]", getTestNumber().getNumber(), nr));
        a.setNonUniqueOnX1AA(String.format("NonUnique: [%d,%d]", getTestNumber().getNumber(), nr/2 ));
        return a;
    }

    public X1AAId<?> getA1Id() {
        return a1Id;
    }

    public X1AAId<?> getA2Id() {
        return a2Id;
    }

    public X1AAId<?> getA3Id() {
        return a3Id;
    }
}
