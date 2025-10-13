package no.statkart.skif.storetest.domain.relation.many;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

/**
 * Disse tilhører {@link BubbleWithManyBubbles}, som eier relasjonen mellom dem.
 */
public class ManyBubbles extends AbstractStoreTestBubble {
    private String text;

    @Override
    public ManyBubblesId<?> getId() {
        return (ManyBubblesId<?>) super.getId();
    }

    public void setId(ManyBubblesId<?> id) {
        super.setId(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
