package no.statkart.skif.skiftest.wsapi.exception.mapping;

import no.statkart.skif.exception.*;
import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.skiftest.wsapi.exception.*;
import no.statkart.skif.skiftest.wsapi.exception.ApplicationException;
import no.statkart.skif.skiftest.wsapi.exception.FinderException;
import no.statkart.skif.skiftest.wsapi.exception.ImplementationException;
import no.statkart.skif.skiftest.wsapi.exception.OperationalException;
import no.statkart.skif.skiftest.wsapi.exception.SystemException;
import no.statkart.skif.skiftest.wsapi.exception.ValidationException;

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
        addMapper(new ServiceExceptionTypeMapper(ServiceException.class,ServiceFaultInfo.class, no.statkart.skif.exception.SkifException.class));

        addMapper(new SystemExceptionTypeMapper(SystemException.class, SystemFaultInfo.class, no.statkart.skif.exception.SystemException.class));
        addMapper(new SystemExceptionTypeMapper(ImplementationException.class, ImplementationFaultInfo.class, no.statkart.skif.exception.ImplementationException.class));
        addMapper(new SystemExceptionTypeMapper(OperationalException.class, OperationalFaultInfo.class, no.statkart.skif.exception.OperationalException.class));

        addMapper(new ApplicationExceptionTypeMapper(ApplicationException.class, ApplicationFaultInfo.class, no.statkart.skif.exception.ApplicationException.class));
        addMapper(new ApplicationExceptionTypeMapper(FinderException.class,  FinderFaultInfo.class, no.statkart.skif.exception.FinderException.class));
        addMapper(new ApplicationExceptionTypeMapper(ValidationException.class, ValidationFaultInfo.class, no.statkart.skif.exception.ValidationException.class));
        addMapperW2D(new IdentityExceptionTypeMapper(Error.class));
        addMapperW2D(new IdentityExceptionTypeMapper(RuntimeException.class));

    }
}