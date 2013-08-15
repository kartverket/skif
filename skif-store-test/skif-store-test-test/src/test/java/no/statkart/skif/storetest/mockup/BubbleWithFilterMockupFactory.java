package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BubbleWithFilter;
import no.statkart.skif.storetest.domain.basic.BubbleWithFilterId;
import no.statkart.skif.storetest.domain.relation.X1BOneMockupFactory;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class BubbleWithFilterMockupFactory extends AbstractMockupFactory {
    private final BubbleWithFilterId<?> obj1Id;

    @Inject
    X1BOneMockupFactory x1BOneMockupFactory;

    @Inject
    public BubbleWithFilterMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        obj1Id = getNextId();
    }

    private BubbleWithFilterId<?> getNextId() {
        return getNextId(BubbleWithFilterId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithFilter(obj1Id, 1, null));
    }

    private BubbleWithFilter createBubbleWithFilter(BubbleWithFilterId<?> objId, int nr, String text) {
        BubbleWithFilter obj = new BubbleWithFilter();
        obj.setId(objId);
        obj.setNr(nr);
        obj.setFilter(false);
        obj.setText(text);
        obj.setFilterText(null);
       return obj;
    }
}
