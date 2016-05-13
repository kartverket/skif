package no.statkart.skif.generics.domain.impl;

import no.statkart.skif.generics.service.StrutService;

/**
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public class AStrutId<O extends AStrut> extends BaseStrutId<O> {

    public AStrutId(long value) {
        super(value);
    }

    @Override
    public O getObject(StrutService service) {
        return (O)service.getStrut(this);
    }


//implementing methods..


    @Override
    public Long getValue() {
        return value;
    }
}
