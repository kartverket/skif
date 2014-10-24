package no.statkart.skif.store;

import com.google.common.collect.ImmutableSet;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class AbstractBubbleObject implements BubbleObject, Serializable {
    private static final long serialVersionUID = 1L;

    protected transient Store store;
    private transient boolean flushed = false;
    protected BubbleId<?> id;

    @Deprecated
    private long versjonId = 0;

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

    @Override
    public void setFlushed(boolean flushed) {
        this.flushed = flushed;
    }

    @Override
    public boolean isFlushed() {
        return flushed;
    }

    @Deprecated
    public long getVersjonId() {
        return versjonId;
    }

    @Deprecated // WS-mapping krever public, men man skal normal ikke bruke denne metoden
    public void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }


    public void register(Store store) {
        checkState(this.store==null || this.store == store, "BubbleObject allerede registrert med en annen session: %s", this);
        this.store = store;
    }

    public Store store() {
        return store;
    }
    protected final boolean hasSnapshotVersionCurrentId() {
        return id != null && id.getSnapshotVersion() == SnapshotVersion.CURRENT;
    }

    protected final <T extends BubbleId<?>> T onChangeRelation(RelationName relationName, T oldValue, T newValue) {
        if (hasSnapshotVersionCurrentId() && store!=null && oldValue!=newValue) {
            store.getInstance(StoreRelationCache.class).onChangeRelation(relationName, id, oldValue, newValue);
        }
        return newValue;
    }

    protected <T> T unwrap(Map<? extends BubbleId<?>, T> mapOfResults) {
        return mapOfResults.get(getId());
    }

    protected <T> T finder(Class<T> type) {
        return store.getInstance(type);
    }

    protected <T extends Set> T idAsSet() { return (T) ImmutableSet.of(getId()); }

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
                ", versjonId=" + versjonId +
                '}';
    }
}
