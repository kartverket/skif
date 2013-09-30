package no.statkart.skif.storetest.domain.relation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class X1BBOneMockupFactory extends AbstractMockupFactory {
    private final X1BBOneId<?> b1Id;
    private final X1BBOneId<?> b2Id;
    private final X1BBOneId<?> b3Id;

    @Inject
    public X1BBOneMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        b1Id = getNextId();
        b2Id = getNextId();
        b3Id = getNextId();
    }

    private X1BBOneId getNextId() {
        return getNextId(X1BBOneId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createObject(b1Id, 1, "Ingen A'er peper på denne"));
        store.insert(createObject(b2Id, 2, "Kun en A peper på denne"));
        store.insert(createObject(b3Id, 3, "Flere A'er peker på denne"));
    }

    private X1BBOne createObject(X1BBOneId<?> id, int nr, String text) {
        X1BBOne o = new X1BBOne();
        o.setId(id);
        o.setNr(nr);
        o.setText(text);
       return o;
    }

    public X1BBOneId<?> getB1Id() {
        return b1Id;
    }

    public X1BBOneId<?> getB2Id() {
        return b2Id;
    }

    public X1BBOneId<?> getB3Id() {
        return b3Id;
    }
}
