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
public class X2CCManyMockupFactory extends AbstractMockupFactory {
    private final X2CCManyId<?> c1Id;
    private final X2CCManyId<?> c2Id;
    private final X2CCManyId<?> c3Id;

    @Inject
    public X2CCManyMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        c1Id = getNextId();
        c2Id = getNextId();
        c3Id = getNextId();
    }

    private X2CCManyId getNextId() {
        return getNextId(X2CCManyId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createObject(c1Id, 1, "Ingen A'er peker på denne"));
        store.insert(createObject(c2Id, 2, "A3 peker på denne"));
        store.insert(createObject(c3Id, 3, "A3 peker på denne"));
    }

    private X2CCMany createObject(X2CCManyId<?> id, int nr, String text) {
        X2CCMany o = new X2CCMany();
        o.setId(id);
        o.setNr(nr);
        o.setText(text);
       return o;
    }

    public X2CCManyId<?> getC1Id() {
        return c1Id;
    }

    public X2CCManyId<?> getC2Id() {
        return c2Id;
    }

    public X2CCManyId<?> getC3Id() {
        return c3Id;
    }
}
