package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.endringslogg.Endringstype;

/**
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class EndringstypeTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype,Endringstype> {

    public EndringstypeTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype.class,Endringstype.class);
    }

    @Override
    public no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype mapDomainObject(Endringstype source) {
        return no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype.fromValue(source.toString());
    }

    @Override
    public Endringstype mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype source) {
        return Endringstype.valueOf(source.value());
    }
}
