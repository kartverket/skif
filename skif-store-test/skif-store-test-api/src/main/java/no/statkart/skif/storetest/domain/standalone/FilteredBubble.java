package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 * @author Jan Holmen
 * @since 2.1
 */
public class FilteredBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text;
    private boolean filter;
    private String filterText;

    public FilteredBubble() {
    }

    public FilteredBubble(BubbleId<?> id) {
        super(id);
    }

    public FilteredBubble(BubbleId<?> id, String text, boolean filtrer, String filterText) {
        super(id);
        this.text = text;
        this.filter = filtrer;
        this.filterText = filterText;
    }

    @Override
    public FilteredBubbleId<?> getId() {
        return (FilteredBubbleId<?>) super.getId();
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
