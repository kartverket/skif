package no.statkart.skif.skiftest.wsapi.exception.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.skiftest.wsapi.exception.*;

/**
 * Exception mapper for SkifTest som mapper skifs standard exception hierarki over JAX-WS. Mapperen marshaller
 * nested exceptions slik at exceptionen opprettes med de samme nesteded exceptions som den hadde på serveren.
 * En untagelse er interne server exception klasser. Disse mappes til klassen
 * {@link no.statkart.skif.exception.ServerException}
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public class SkifTestExceptionMapper extends AbstractExceptionMapper {

    public SkifTestExceptionMapper() {
        this(new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public SkifTestExceptionMapper(ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(SkifTestExceptionMapping.class, wsapiObjectFactory, domainObjectFactory, true, true);
        addMapptersForExceptionTypes();
    }

    @Override
    public SkifTestExceptionMapping getMapping() {
        return (SkifTestExceptionMapping) super.getMapping();
    }

    private void addMapptersForExceptionTypes() {
        addMapper(new ServiceExceptionTypeMapper(ServiceException.class, no.statkart.skif.exception.SkifException.class, ServiceFaultInfo.class));

        addMapper(new SystemExceptionTypeMapper(SystemException.class, no.statkart.skif.exception.SystemException.class, SystemFaultInfo.class));
        addMapper(new SystemExceptionTypeMapper(ImplementationException.class, no.statkart.skif.exception.ImplementationException.class, ImplementationFaultInfo.class));
        addMapper(new SystemExceptionTypeMapper(OperationalException.class, no.statkart.skif.exception.OperationalException.class, OperationalFaultInfo.class));

        addMapper(new ApplicationExceptionTypeMapper(ApplicationException.class, no.statkart.skif.exception.ApplicationException.class, ApplicationFaultInfo.class));
        addMapper(new ApplicationExceptionTypeMapper(FinderException.class, no.statkart.skif.exception.FinderException.class, FinderFaultInfo.class));
        addMapper(new ApplicationExceptionTypeMapper(ValidationException.class, no.statkart.skif.exception.ValidationException.class, ValidationFaultInfo.class));
        addMapperW2D(new IdentityExceptionTypeMapper(Error.class));
        addMapperW2D(new IdentityExceptionTypeMapper(RuntimeException.class));

    }
}