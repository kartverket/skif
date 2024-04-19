package no.statkart.skif.store;

import com.google.common.collect.ImmutableSet;
import no.statkart.skif.domain.EqualityByFields;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class AbstractBubbleObject implements BubbleObject, Serializable, EqualityByFields {
    private static final long serialVersionUID = 1L;

    protected transient Store store;
    private transient boolean flushed = false;
    protected BubbleId<?> id;

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

    public void setId(BubbleId<?> id) {
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

    public void register(Store store) {
        checkState(this.store == null || this.store == store, "BubbleObject allerede registrert med en annen session: %s", this);
        this.store = store;
    }

    public Store store() {
        return store;
    }

    protected final boolean hasSnapshotVersionCurrentId() {
        return id != null && id.getSnapshotVersion() == SnapshotVersion.CURRENT;
    }

    protected <T> T unwrap(Map<? extends BubbleId<?>, T> mapOfResults) {
        return mapOfResults.get(getId());
    }

    protected <T> T finder(Class<T> type) {
        return store.getInstance(type);
    }

    protected final boolean isRelationCacheEnabled() {
        return store != null && store.getRelationCache().isEnabled();
    }

    protected <T extends Set> T idAsSet() {
        return (T) ImmutableSet.of(getId());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AbstractBubbleObject)) return false;
        final AbstractBubbleObject bubbleObject = (AbstractBubbleObject) object;
        // Det finnes en del kode i applikasjonene som avhenger av at bobler uten id ikke er equals.
        // Enten dette var intensjonen med den opprinnelige implementasjonen eller ikke, så må det være sånn inntil videre.
        if (this.getId() == null || bubbleObject.getId() == null) return false;
        return Objects.equals(getClass(), object.getClass())
            && Objects.equals(getId(), bubbleObject.getId())
            ;
    }

    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' +
            "id=" + getId() +
            '}';
    }
}
