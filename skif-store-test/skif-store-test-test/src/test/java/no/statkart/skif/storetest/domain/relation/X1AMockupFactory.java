package no.statkart.skif.storetest.domain.relation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1A;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BOneId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class X1AMockupFactory extends AbstractMockupFactory {
    private final X1AId<?> a1Id;
    private final X1AId<?> a2Id;
    private final X1AId<?> a3Id;

    @Inject X1BOneMockupFactory x1BOneMockupFactory;

    @Inject
    public X1AMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        a1Id = getNextId();
        a2Id = getNextId();
        a3Id = getNextId();
    }

    private X1AId getNextId() {
        return getNextId(X1AId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createX1A(a1Id, 1, null, x1BOneMockupFactory.getB2Id()));
        store.insert(createX1A(a2Id, 2, null, x1BOneMockupFactory.getB3Id()));
        store.insert(createX1A(a3Id, 3, null, x1BOneMockupFactory.getB3Id()));

        // Skal ikke føre til en ekstra versjon siden objektet har de samme verdier
        store.setSnapshotVersion("2011-10-02 08:00:00.00");
        store.update(createX1A(a1Id, 1, null, x1BOneMockupFactory.getB2Id()));
    }

    private X1A createX1A(X1AId<?> aId, int nr, String text, X1BOneId bId ) {
        X1A a = new X1A();
        a.setId(aId);
        a.setNr(nr);
        a.setText(text);
        a.setbId(bId);
       return a;
    }

    public X1AId<?> getA1Id() {
        return a1Id;
    }

    public X1AId<?> getA2Id() {
        return a2Id;
    }
}
