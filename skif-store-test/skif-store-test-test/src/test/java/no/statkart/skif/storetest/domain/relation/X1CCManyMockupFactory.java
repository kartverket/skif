package no.statkart.skif.storetest.domain.relation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCMany;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCManyId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class X1CCManyMockupFactory extends AbstractMockupFactory {
    private final X1CCManyId<?> c1Id;
    private final X1CCManyId<?> c2Id;
    private final X1CCManyId<?> c3Id;

    @Inject
    public X1CCManyMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        c1Id = getNextId();
        c2Id = getNextId();
        c3Id = getNextId();
    }

    private X1CCManyId getNextId() {
        return getNextId(X1CCManyId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createObject(c1Id, 1, "Ingen A'er peker på denne"));
        store.insert(createObject(c2Id, 2, "A3 peker på denne"));
        store.insert(createObject(c3Id, 3, "A3 peker på denne"));
    }

    private X1CCMany createObject(X1CCManyId<?> id, int nr, String text) {
        X1CCMany o = new X1CCMany();
        o.setId(id);
        o.setNr(nr);
        o.setText(text);
       return o;
    }

    public X1CCManyId<?> getC1Id() {
        return c1Id;
    }

    public X1CCManyId<?> getC2Id() {
        return c2Id;
    }

    public X1CCManyId<?> getC3Id() {
        return c3Id;
    }
}
