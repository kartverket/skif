package no.statkart.skif.store;

import java.io.Serializable;

import static no.statkart.skif.guava.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AbstractBubbleObject implements BubbleObject, Serializable{
    protected transient Store store;
    protected BubbleId<?> id;
    private long version = 0;

    @Override
    public BubbleId<?> getBubbleId() {
        return id;
    }

    public AbstractBubbleObject(BubbleId<?> id) {
        this.id = id;
    }

    public AbstractBubbleObject() {
    }

    public BubbleId<?> getId() {
        return id;
    }

    public void setId(BubbleId<?> id ) {
        this.id = id;
    }


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void incremetVersion() {
        version++;
    }


    public void register(Store store) {
        checkState(this.store==null || this.store == store, "BubbleObject allerede registrert med en annen session: %s", this);
        this.store = store;
    }

    public Store store() {
        return store;
    }


    public final boolean equals(Object object) {
       if( this == object ) return true;
       if( object == null || !(object instanceof AbstractBubbleObject) ) return false;
       if( !this.getClass().equals(object.getClass()) ) return false;
       final AbstractBubbleObject bubbleObject = (AbstractBubbleObject) object;
       if( this.getId() == null || bubbleObject.getId() == null ) return false;
       return this.getId().equals(bubbleObject.getId());
    }

    public final int hashCode() {
       return (getId() != null ? getId().hashCode() : 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "id=" + id +
                ", version=" + version +
                '}';
    }
}
