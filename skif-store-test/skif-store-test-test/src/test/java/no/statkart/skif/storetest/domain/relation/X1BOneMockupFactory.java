package no.statkart.skif.storetest.domain.relation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1A;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BOneId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class X1BOneMockupFactory extends AbstractMockupFactory {
    private final X1BOneId<?> b1Id;
    private final X1BOneId<?> b2Id;
    private final X1BOneId<?> b3Id;

    @Inject
    public X1BOneMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        b1Id = getNextId();
        b2Id = getNextId();
        b3Id = getNextId();
    }

    private X1BOneId getNextId() {
        return getNextId(X1BOneId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createObject(b1Id, 1, "Ingen A'er peper på denne"));
        store.insert(createObject(b2Id, 2, "Kun en A peper på denne"));
        store.insert(createObject(b3Id, 3, "Flere A'er peker på denne"));
    }

    private X1BOne createObject(X1BOneId<?> id, int nr, String text) {
        X1BOne o = new X1BOne();
        o.setId(id);
        o.setNr(nr);
        o.setText(text);
       return o;
    }

    public X1BOneId<?> getB1Id() {
        return b1Id;
    }

    public X1BOneId<?> getB2Id() {
        return b2Id;
    }

    public X1BOneId<?> getB3Id() {
        return b3Id;
    }
}
