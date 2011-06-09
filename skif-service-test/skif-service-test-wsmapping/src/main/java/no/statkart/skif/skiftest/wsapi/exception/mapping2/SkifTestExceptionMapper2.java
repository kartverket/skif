package no.statkart.skif.skiftest.wsapi.exception.mapping2;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultObjectFactory;
import no.statkart.skif.mapper.IdentityExceptionTypeMapper;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleFaultInfo;

/**
 * Enkel exception mapper for SkifTest som bare kan mapper SimpleExcpetion over JAX-WS. I tillegg mappes alle runtime exceptions på
 * klienten via {@link no.statkart.skif.mapper.IdentityExceptionTypeMapper} slik at runtime exception fra JAX-WS kommer igjennom til klient.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public class SkifTestExceptionMapper2 extends AbstractMapper {

    public SkifTestExceptionMapper2() {
        this(new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public SkifTestExceptionMapper2(ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(SkifTestExceptionMapping2.class, wsapiObjectFactory, domainObjectFactory, true);
        addMapptersForExceptionTypes();
    }

    @Override
    public SkifTestExceptionMapping2 getMapping() {
        return (SkifTestExceptionMapping2) super.getMapping();
    }


    private void addMapptersForExceptionTypes() {
        addMapper(new SimpleExceptionTypeMapper(SimpleException.class, no.statkart.skif.skiftest.exception.SimpleException.class, SimpleFaultInfo.class));
        addMapperW2D(new IdentityExceptionTypeMapper(Error.class));
        addMapperW2D(new IdentityExceptionTypeMapper(RuntimeException.class));
    }
}