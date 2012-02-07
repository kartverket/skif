package no.statkart.skif.storetest.domain.demo;

import java.io.Serializable;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class ChildForParrent implements Serializable { //extends AbstractObject{
    private Long id;
    private ParrentBubble parrentBubble;
    private ChildBubbleId<ChildBubble> childBubbleId;

    public ChildForParrent() {
    }

    public ChildForParrent(ChildBubbleId<ChildBubble> childBubbleId, Long id) {
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

    public ParrentBubble getParrentBubble() {
        return parrentBubble;
    }

    public void setParrentBubble(ParrentBubble parrentBubble) {
        this.parrentBubble = parrentBubble;
    }
    public  ChildBubble getChildBubble(){
        return getParrentBubble().store().get(childBubbleId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChildForParrent that = (ChildForParrent) o;

        if (childBubbleId != null ? !childBubbleId.equals(that.childBubbleId) : that.childBubbleId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parrentBubble != null ? !parrentBubble.equals(that.parrentBubble) : that.parrentBubble != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parrentBubble != null ? parrentBubble.hashCode() : 0);
        result = 31 * result + (childBubbleId != null ? childBubbleId.hashCode() : 0);
        return result;
    }
}
