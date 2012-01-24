package no.statkart.skif.store;

import java.io.Serializable;

import static no.statkart.skif.guava.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractObject implements Serializable{
    protected transient Store store;
    public abstract Long getId();

    public AbstractObject() {
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
       if( object == null || !(object instanceof AbstractObject) ) return false;
       if( !this.getClass().equals(object.getClass()) ) return false;
       final AbstractObject bubbleObject = (AbstractObject) object;
       if( this.getId() == null || bubbleObject.getId() == null ) return false;
       return this.getId().equals(bubbleObject.getId());
    }

    public final int hashCode() {
       return (getId() != null ? getId().hashCode() : 0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "id=" + getId() +
                '}';
    }
}
