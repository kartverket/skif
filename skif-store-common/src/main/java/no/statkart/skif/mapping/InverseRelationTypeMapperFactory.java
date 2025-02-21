package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.TypeMapper;
import no.statkart.skif.mapper.TypeMapperFactory;
import no.statkart.skif.store.InverseRelation;

/**
 * Oppretter {@link InverseRelationTypeMapper}-e for mappinger som ser ut til å være slikt.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class InverseRelationTypeMapperFactory implements TypeMapperFactory {
    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (InverseRelation.class.isAssignableFrom(domainTypeToken.getRawType())) {
            //noinspection unchecked
            return new InverseRelationTypeMapper(wsapiTypeToken.getRawType(), TypeLiteral.get(domainTypeToken.getType()));
        }
        return null;
    }
}
