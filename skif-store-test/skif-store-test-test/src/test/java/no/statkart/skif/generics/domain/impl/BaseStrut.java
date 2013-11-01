package no.statkart.skif.generics.domain.impl;

import no.statkart.skif.generics.domain.AbstractStrut;

/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public abstract class BaseStrut<I extends BaseStrutId<?>> extends AbstractStrut<I> {
    @Override
    public I getId() {
        return id;
    }

    @Override
    public I setId(I strutId) {
        return id = strutId;
    }

}
