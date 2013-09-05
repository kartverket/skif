package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import no.statkart.skif.wsversioning.domain.Veg;
import no.statkart.skif.wsversioning.wsapi.v1.domain.Gate;
import no.statkart.skif.wsversioning.wsapi.v1.domain.GateId;

/**
 * Mapper mellom {@link Veg} og {@link Gate}
 */
public class GateMapper extends AbstractWSVersioningTypeMapper<Gate, Veg> {
    protected GateMapper() {
        super(Gate.class, Veg.class);
    }

    @Override
    public Gate mapDomainObject(Veg source) {
        Gate target = new Gate();
        target.setId(getMapping().d2w(source.getId()));
        target.setGatenavn(source.getAdressenavn());
        return target;
    }

    @Override
    public Veg mapWsapiObject(Gate source) {
        Veg target = new Veg();
        target.setId(getMapping().w2d((GateId) source.getId()));
        target.setAdressenavn(source.getGatenavn());
        return target;
    }
}
