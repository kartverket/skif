package no.statkart.skif.store.memorydomain;

import no.statkart.skif.store.AbstractBubbleObject;

public abstract class BaseType extends AbstractBubbleObject {
    private static final long serialVersionUID = 1;

    @Override
    public BaseTypeId<?> getId() {
        return (BaseTypeId<?>) super.getId();
    }
}
