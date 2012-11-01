package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.demo.Foo;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class FooTypeMapper extends StoreTestBubbleTypeMapper<no.statkart.skif.storetest.wsapi.domain.demo.Foo, Foo> {

    public FooTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.demo.Foo.class, Foo.class);
    }

    @Override
    public void mapDomainObject(Foo source, no.statkart.skif.storetest.wsapi.domain.demo.Foo target) {
        super.mapDomainObject(source, target);
        
        target.setOppdateringsdato(map.d2w(source.getOppdateringsdato()));
        target.setSluttdato(map.d2w(source.getSluttdato()));
        target.setNavn(map.d2w(source.getNavn()));
        target.setNr(map.d2w(source.getNr()));
    }

    @Override
    public void mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.demo.Foo source, Foo target) {
        super.mapWsapiObject(source, target);

        target.setOppdateringsdato(map.w2d(source.getOppdateringsdato()));
        target.setSluttdato(map.w2d(source.getSluttdato()));
        target.setNavn(map.w2d(source.getNavn()));
        target.setNr(map.w2d(source.getNr()));
    }
}
