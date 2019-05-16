package no.statkart.skif.store;

import no.statkart.skif.domain.EqualityByFields;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractCompositeComponent<O, T> implements CompositeComponent<O, T>, EqualityByFields {
    private static final long serialVersionUID = 1L;

    protected T owner;

    @Override
    public final T getOwner() {
        return owner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final O getCompositeRootOwner() {
        if (owner==null) return null;
        if (owner instanceof CompositeComponent) return ((CompositeComponent<O,?>) owner).getCompositeRootOwner();
        return (O)owner;
    }

    @Override
    public final void setOwner(T owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
        if (getCompositeRootOwner()!=null) {
            onSetCompositeRootOwner();
        }
    }

    protected void onSetCompositeRootOwner(EntityComponentWithOwnerReference<O> entityComponent) {
        if (entityComponent!=null) entityComponent.setOwner(getCompositeRootOwner());
    }

    protected void onSetCompositeRootOwner(CompositeComponent<O, ?> compositeComponent) {
        if (compositeComponent!=null) {
            compositeComponent.onSetCompositeRootOwner();
        }
    }

    protected void onSetCompositeRootOwner(Collection<? extends EntityComponentWithOwnerReference<O>> collectionWithOwnerReference) {
        O compositeRootOwner = getCompositeRootOwner();
        for (EntityComponentWithOwnerReference<O> element : collectionWithOwnerReference) {
            element.setOwner(compositeRootOwner);
        }
    }

}
