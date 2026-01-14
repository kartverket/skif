package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

/**
 * Boble uten historikk med filtered property.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithFilter extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private boolean filter;
    private String filterText;

    public BubbleWithFilter() {
    }

    public BubbleWithFilter(BubbleWithFilterId<?> id) {
        super(id);
    }

    public BubbleWithFilter(BubbleWithFilterId<?> id, String text, boolean filtrer, String filterText) {
        super(id);
        this.text = text;
        this.filter = filtrer;
        this.filterText = filterText;
    }

    @Override
    public BubbleWithFilterId<?> getId() {
        return (BubbleWithFilterId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
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
