package no.statkart.skif.store2;

import no.statkart.skif.store.Store;

import java.io.Serializable;

import static no.statkart.skif.guava.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AbstractBubbleObject2 implements BubbleObject2, Serializable{
    protected transient Store store;
    protected BubbleId2<?> id;
    private long version = 0;

    public AbstractBubbleObject2(BubbleId2<?> id) {
        this.id = id;
    }

    public AbstractBubbleObject2() {
    }

    public BubbleId2<?> getId() {
        return id;
    }

    public void setId(BubbleId2<?> id ) {
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
        checkState(store!=null, "BubbleObject already registered with a session: {0}", this);
        this.store = store;
    }

    public Store store() {
        return store;
    }


    public final boolean equals(Object object) {
       if( this == object ) return true;
       if( object == null || !(object instanceof AbstractBubbleObject2) ) return false;
       if( !this.getClass().equals(object.getClass()) ) return false;
       final AbstractBubbleObject2 bubbleObject = (AbstractBubbleObject2) object;
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
