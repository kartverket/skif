package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.wsapi.domain.basic.Endringsklasse;

/**
 * @author Thomas Berg
 */
public class EndringMapper extends AbstractTypeMapper<Endringsklasse,Class<? extends Endring>,Mapping> {

    public EndringMapper(Class<Endringsklasse> wsapiClass, Class<Class<? extends Endring>> domainClass) {
        super(wsapiClass, domainClass, Mapping.class);
    }

    @Override
    public Endringsklasse mapDomainObject(Class<? extends Endring> source) {
        Endringsklasse endringsklasse = new Endringsklasse();
        endringsklasse.setValue(source.getSimpleName());
        return endringsklasse;
    }

    @Override
    public Class<? extends Endring> mapWsapiObject(Endringsklasse source) {
        return Endring.class;
    }
}
