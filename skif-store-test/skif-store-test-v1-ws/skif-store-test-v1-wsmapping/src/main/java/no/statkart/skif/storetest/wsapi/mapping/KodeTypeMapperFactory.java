package no.statkart.skif.storetest.wsapi.mapping;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.mapper.TypeMapper;
import no.statkart.skif.mapper.TypeMapperFactory;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKode;

/**
 * Lager {@link KodeTypeMapper} dynamisk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class KodeTypeMapperFactory implements TypeMapperFactory {

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode.class.isAssignableFrom(wsapiTypeToken.getRawType()) && StoreTestKode.class.isAssignableFrom(domainTypeToken.getRawType())) {
            //noinspection unchecked
            return new KodeTypeMapper(wsapiTypeToken.getRawType(), domainTypeToken.getRawType());
        }

        return null;
    }

    /**
     * @author Henrik Fredholm
     * @since 2.0
     */
    protected static class KodeTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode, DomainT extends StoreTestKode> extends StoreTestBubbleTypeMapper<WsapiT, DomainT> {
        public KodeTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
            super(wsapiClass, domainClass);
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = super.mapDomainObject(source);
            target.setKodelisteId(getMapping().d2w(source.getKodelisteId())); // Denne mappes kun ut, ikke inn
            target.setKodeverdi(getMapping().d2w(source.getKodeverdi()));
            target.setBeskrivelse(getMapping().d2w(source.getBeskrivelse(), no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.class));
            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            DomainT target = super.mapWsapiObject(source);
            target.setKodeverdi(getMapping().w2d(source.getKodeverdi()));
            target.setBeskrivelse(getMapping().w2d(source.getBeskrivelse(), LocalizedString.class));
//        target.setBeskrivelse(new LocalizedString(getMapping().w2d(source.getBeskrivelse(), LocalizedString.MAP_TYPE)));
            return target;
        }

        public static <WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode, DomainT extends StoreTestKode> TypeMapper<WsapiT, DomainT> create(Class<WsapiT> wsapiTClass, Class<DomainT> domainTClass) {
            return new KodeTypeMapper<WsapiT, DomainT>(wsapiTClass, domainTClass);
        }
    }
}