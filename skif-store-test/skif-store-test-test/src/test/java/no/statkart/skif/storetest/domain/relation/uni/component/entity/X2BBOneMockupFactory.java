package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public class X2BBOneMockupFactory extends AbstractMockupFactory {
    private final X2BBOneId<?> b1Id;
    private final X2BBOneId<?> b2Id;
    private final X2BBOneId<?> b3Id;

    @Inject
    public X2BBOneMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        b1Id = getNextId();
        b2Id = getNextId();
        b3Id = getNextId();
    }

    private X2BBOneId getNextId() {
        return getNextId(X2BBOneId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createObject(b1Id, 1, "Ingen A'er peker på denne"));
        store.insert(createObject(b2Id, 2, "Kun en A peker på denne"));
        store.insert(createObject(b3Id, 3, "Flere A'er peker på denne"));
    }

    private X2BBOne createObject(X2BBOneId<?> id, int nr, String text) {
        X2BBOne o = new X2BBOne();
        o.setId(id);
        o.setNr(nr);
        o.setText(text);
       return o;
    }

    public X2BBOneId<?> getB1Id() {
        return b1Id;
    }

    public X2BBOneId<?> getB2Id() {
        return b2Id;
    }

    public X2BBOneId<?> getB3Id() {
        return b3Id;
    }
}
