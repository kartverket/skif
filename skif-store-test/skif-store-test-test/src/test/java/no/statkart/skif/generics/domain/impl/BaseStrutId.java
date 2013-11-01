package no.statkart.skif.generics.domain.impl;

import no.statkart.skif.generics.domain.AbstractStrut;
import no.statkart.skif.generics.domain.AbstractStrutId;
import no.statkart.skif.generics.service.StrutService;

/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public abstract class BaseStrutId<O extends AbstractStrut> extends AbstractStrutId<Long, O> {

    protected BaseStrutId(Long value) {
        super(value);
    }


    public abstract O getObject(StrutService service);

}
