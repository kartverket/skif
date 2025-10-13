package no.statkart.skif.storetest.domain.relation.many;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import java.util.ArrayList;
import java.util.List;

/**
 * Boble som eier en relasjon til mangle {@link ManyBubbles}.
 */
public class BubbleWithManyBubbles extends AbstractStoreTestBubble {
    private String text;
    private List<ManyBubblesId<?>> myBubbleIds = new ArrayList<>();

    @Override
    public BubbleWithManyBubblesId<?> getId() {
        return (BubbleWithManyBubblesId<?>) super.getId();
    }

    public void setId(BubbleWithManyBubblesId<?> id) {
        super.setId(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ManyBubbles> getManyBubbles() {
        return store().getOrdered(getMyBubbleIds());
    }

    public List<ManyBubblesId<?>> getMyBubbleIds() {
        return myBubbleIds;
    }

    public void setMyBubbleIds(List<ManyBubblesId<?>> myBubbleIds) {
        this.myBubbleIds = myBubbleIds;
    }

    // Hibernate
    private List<ManyBubblesId<?>> getMyBubblesIdsSet() {
        return myBubbleIds;
    }

    // Hibernate
    private void setMyBubblesIdsSet(List<ManyBubblesId<?>> manyBubbleIds) {
        this.myBubbleIds = manyBubbleIds;
    }
}
