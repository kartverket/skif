package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Jan Holmen
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
    public void setId(BubbleId<?> id) {
        super.setId((FilteredBubbleId<?>) id);
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
