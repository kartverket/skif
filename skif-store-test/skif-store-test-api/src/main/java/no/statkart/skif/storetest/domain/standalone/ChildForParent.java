package no.statkart.skif.storetest.domain.standalone;

import java.io.Serializable;

/**
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 * @author Jan Holmen
 * @since 2.1
 */
public class ChildForParent implements Serializable { //extends AbstractObject{
    private Long id;
    private ParentBubble parentBubble;
    private ChildBubbleId<ChildBubble> childBubbleId;

    public ChildForParent() {
    }

    public ChildForParent(ChildBubbleId<ChildBubble> childBubbleId, Long id) {
        this.childBubbleId = childBubbleId;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChildBubbleId<ChildBubble> getChildBubbleId() {
        return childBubbleId;
    }

    public void setChildBubbleId(ChildBubbleId  childBubbleId) {
        this.childBubbleId = childBubbleId;
    }

    public ParentBubble getParentBubble() {
        return parentBubble;
    }

    public void setParentBubble(ParentBubble parentBubble) {
        this.parentBubble = parentBubble;
    }
    public  ChildBubble getChildBubble(){
        return getParentBubble().store().get(childBubbleId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChildForParent that = (ChildForParent) o;

        if (childBubbleId != null ? !childBubbleId.equals(that.childBubbleId) : that.childBubbleId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parentBubble != null ? !parentBubble.equals(that.parentBubble) : that.parentBubble != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentBubble != null ? parentBubble.hashCode() : 0);
        result = 31 * result + (childBubbleId != null ? childBubbleId.hashCode() : 0);
        return result;
    }
}
