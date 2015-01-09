package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;

/**
 * Delegerer mapping til en annen mapping.
 *
 * @since 2.6.0
 */
public class DelegatingTypeMapperFactory implements TypeMapperFactory {
    private final Mapping delegate;

    public DelegatingTypeMapperFactory(Mapping delegate) {
        this.delegate = delegate;
    }

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(final TypeToken<WsapiT> wsapiTypeToken, final TypeToken<DomainT> domainTypeToken) {
        return new AbstractTypeMapper<WsapiT, DomainT, Mapping>((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), Mapping.class) {
            @Override
            public WsapiT mapDomainObject(DomainT source) {
                return (WsapiT) delegate.d2w(source, domainTypeToken.getType(), wsapiTypeToken.getType());
            }

            @Override
            public DomainT mapWsapiObject(WsapiT source) {
                return (DomainT) delegate.w2d(source, wsapiTypeToken.getType(), domainTypeToken.getType());
            }
        };
    }
}
