package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * @author Henrik Fredholm
 */
@Singleton
public class SimpleMockupFactory extends AbstractMockupFactory {
    private final SimpleId<?> simpleId1;
    private final SimpleId<?> simpleId2;
    private final SimpleId<?> simpleId3;


    @Inject
    public SimpleMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        simpleId1 = getNextId();
        simpleId2 = getNextId();
        simpleId3 = getNextId();
    }

    private SimpleId<?> getNextId() {
        return getNextId(SimpleId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createSimple(simpleId1, 1, "Ingen BubbleWithRelation peker til denne"));
        store.insert(createSimple(simpleId2, 2, "En BubbleWithRelation (nr 1) peker til denne"));
        store.insert(createSimple(simpleId3, 3, "Flere BubbleWithRelation (nr 2 og 3) peker til denne"));
    }

    private Simple createSimple(SimpleId<?> aId, int nr, String text ) {
        Simple obj = new Simple();
        obj.setId(aId);
        obj.setNr(nr);
        obj.setText(text);
       return obj;
    }

    public SimpleId<?> getSimpleId1() {
        return simpleId1;
    }

    public SimpleId<?> getSimpleId2() {
        return simpleId2;
    }

    public SimpleId<?> getSimpleId3() {
        return simpleId3;
    }
}
