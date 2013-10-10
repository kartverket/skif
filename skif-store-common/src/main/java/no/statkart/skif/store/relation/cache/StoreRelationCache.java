package no.statkart.skif.store.relation.cache;

import no.statkart.skif.store.*;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkState;

/**
 * En facade for håndtering av relation caching i Store. Klassen henter ut aktivt UnitOfWork level fra Store og
 * bruker dette level i kall videre til {@link RelationCache}.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public abstract class StoreRelationCache {
    protected RelationCache relationCache = new RelationCache();
    protected boolean enabled;

    protected abstract WrappableStoreSession getStoreSession();

    protected final int getLevel() {
        return getStoreSession().getLevel();
    }

    protected final boolean inAttachedMode() {
        return getStoreSession().inAttachedMode();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public <T extends BubbleId<?>> void onChangeRelation(RelationName relationName, BubbleId<?> sourceId, @Nullable T oldValue, @Nullable T newValue) {
        if (enabled) {
            relationCache.onChangeRelation(getLevel(), inAttachedMode(), relationName, sourceId, oldValue, newValue);
        }
    }

    public RelationValueHolder getRelationValue(RelationName relationName, BubbleId<?> id) {
        checkState(enabled);
        return relationCache.getRelationValue(getLevel(), relationName, id);
    }

    public Object setRelationValue(RelationName relationName, BubbleId<?> id, Object relationValue) {
        checkState(enabled);
        return relationCache.setRelationValue(getLevel(), relationName, id, relationValue);
    }

    public RelationName getRelationNameReturnNullIfDisabled(Method method) {
        if (enabled) {
            return relationCache.getRelationName(method);
        } else {
            return null;
        }
    }
}
