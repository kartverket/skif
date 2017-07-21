package no.statkart.skif.storetest.domain.standalone;

import java.io.Serializable;
import java.util.Objects;

/**
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 * @author Jan Holmen
 * @since 2.1
 */
public class ChildForParent implements Serializable {
    private Long id;
    private ParentBubble parentBubble;
    private ChildBubbleId<?> childBubbleId;

    @SuppressWarnings("unused") // Hibernate
    public ChildForParent() {
    }

    public ChildForParent(ChildBubbleId<?> childBubbleId, Long id) {
        this.childBubbleId = childBubbleId;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChildBubbleId<?> getChildBubbleId() {
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
        return Objects.equals(id, that.id) &&
                Objects.equals(parentBubble, that.parentBubble) &&
                Objects.equals(childBubbleId, that.childBubbleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentBubble, childBubbleId);
    }
}
