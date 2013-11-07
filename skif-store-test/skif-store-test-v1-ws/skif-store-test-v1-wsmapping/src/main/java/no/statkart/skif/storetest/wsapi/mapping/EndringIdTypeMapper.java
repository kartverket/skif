package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.EndringId;

import java.lang.reflect.InvocationTargetException;

/**
 * Mapper mellom {@code Wsapi:Long} og {@code DomainT:EndringId}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class EndringIdTypeMapper extends AbstractStoreTestTypeMapper<Long, EndringId> {

    protected EndringIdTypeMapper() {
        super(Long.class, EndringId.class);
    }

    @Override
    public Long mapDomainObject(EndringId source) {
        return source.getValue();
    }

    @Override
    public EndringId mapWsapiObject(Long source) {
        return new EndringId(source);
    }
}
