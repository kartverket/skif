package no.statkart.skif.storetest.wsapi.mapping;


import no.statkart.skif.store.endringslogg.ReturnerBobler;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class ReturnerBoblerTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler,ReturnerBobler> {

    public ReturnerBoblerTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler.class,ReturnerBobler.class);
    }

    @Override
    public no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler mapDomainObject(ReturnerBobler source) {
        return no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler.fromValue(source.toString());
    }

    @Override
    public ReturnerBobler mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler source) {
        return ReturnerBobler.valueOf(source.value());
    }
}
