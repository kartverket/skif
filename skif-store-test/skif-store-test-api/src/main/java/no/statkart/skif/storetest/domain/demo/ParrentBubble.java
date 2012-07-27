package no.statkart.skif.storetest.domain.demo;

import com.vividsolutions.jts.util.Assert;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jan Holmen
 * @since 2.1
 */
public class ParrentBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text;
    private Set<ChildForParrent> childForParrents = new HashSet<ChildForParrent>();

    public ParrentBubble() {
    }

    public ParrentBubble(BubbleId<?> id) {
        super(id);
    }

    public ParrentBubble(BubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId((ParrentBubbleId<?>) id);
    }

    @Override
    public ParrentBubbleId<?> getId() {
        return (ParrentBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


//    public Set<ChildForParrent> getChildForParrents() {
//        return childForParrents;
//    }

//    public void setChildForParrents(Set<ChildForParrent> childForParrents) {
//        this.childForParrents = childForParrents;
//    }


    public void addChild(ChildBubbleId<ChildBubble> childId, Long id) {
        addChildForParrent(new ChildForParrent(childId, id));
    }

    public boolean addChildForParrent(ChildForParrent cfp) {
        cfp.setParrentBubble(this);
        return childForParrents.add(cfp);
    }

    public Set getChildBubbleIds() {
        Set<ChildBubbleId<ChildBubble>> ids = new HashSet<ChildBubbleId<ChildBubble>>();
        for (ChildForParrent cfp : childForParrents) {
            ids.add(cfp.getChildBubbleId());
        }
        return ids;
    }

    public ChildForParrent getChildForParrent(ChildBubbleId childId) {
        ChildForParrent ret = null;
        for (ChildForParrent cfp : childForParrents) {
            if (cfp.getChildBubble().getId().equals(childId)) {
                return cfp;//ret = cfp;
            }
        }
        return ret;
    }

    public Set<ChildForParrent> getChildForParrents() {
        return Collections.unmodifiableSet(childForParrents);
    }

    public void setChildForParrents(Set<ChildForParrent> childForParrents) {
        this.childForParrents = childForParrents;
    }

    public void clearChildren() {
        childForParrents.clear();
    }


}
