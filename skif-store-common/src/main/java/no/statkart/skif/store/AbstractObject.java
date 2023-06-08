package no.statkart.skif.store;

import java.io.Serializable;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractObject implements Serializable {
    private static final long serialVersionUID = 1L;

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
        final AbstractObject bubbleObject = (AbstractObject) object;
        return Objects.equals(getClass(), object.getClass())
                && Objects.equals(getId(), bubbleObject.getId())
                ;
    }

    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "id=" + getId() +
                '}';
    }
}
