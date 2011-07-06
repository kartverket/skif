package no.statkart.skif.store;

import static no.statkart.skif.guava.Preconditions.checkState;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 */
public class AbstractBubbleObject implements BubbleObject, Serializable{
    protected transient Store store;
    protected AbstractBubbleId<?> id;
    private long version = 0;

    public AbstractBubbleObject(AbstractBubbleId<?> id) {
        this.id = id;
    }

    public AbstractBubbleObject() {
    }

    public AbstractBubbleId<?> getId() {
        return id;
    }

    public void setId(AbstractBubbleId<?> id ) {
        this.id = id;
    }

    public void setId(BubbleId<?> id ) {
        this.id = (AbstractBubbleId<?>) id;
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
        checkState(store!=null, "AbstractBubbleObject already registered with a session: {0}", this);
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
