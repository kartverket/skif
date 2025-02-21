package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 * @since 2.1
 * @author Jan Holmen
 */
public class ParentBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text;
    private Set<ChildForParent> childForParents = new HashSet<ChildForParent>();

    public ParentBubble() {
    }

    public ParentBubble(BubbleId<?> id) {
        super(id);
    }

    public ParentBubble(BubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId(id);
    }

    @Override
    public ParentBubbleId<?> getId() {
        return (ParentBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Set<ChildForParent> getChildForParrents2() {
        return childForParents;
    }

    public void setChildForParrents2(Set<ChildForParent> childForParents) {
        this.childForParents = childForParents;
    }


    public void addChild(ChildBubbleId<?> childId, Long id) {
        addChildForParrent(new ChildForParent(childId, id));
    }

    public boolean addChildForParrent(ChildForParent cfp) {
        cfp.setParentBubble(this);
        return childForParents.add(cfp);
    }

    public Set<ChildBubbleId<?>> getChildBubbleIds() {
        Set<ChildBubbleId<?>> ids = new HashSet<ChildBubbleId<?>>();
        for (ChildForParent cfp : childForParents) {
            ids.add(cfp.getChildBubbleId());
        }
        return ids;
    }

    public ChildForParent getChildForParrent(ChildBubbleId childId) {
        ChildForParent ret = null;
        for (ChildForParent cfp : childForParents) {
            if (cfp.getChildBubble().getId().equals(childId)) {
                return cfp;//ret = cfp;
            }
        }
        return ret;
    }

    public Set<ChildForParent> getChildForParents() {
        return Collections.unmodifiableSet(childForParents);
    }

    public void setChildForParents(Set<ChildForParent> childForParents) {
        this.childForParents = childForParents;
    }

    public void clearChildren() {
        childForParents.clear();
    }


}
