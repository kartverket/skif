package no.statkart.skif.skiftest.wsapi.exception.mapping;

import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;


/**
 * Abstract TypeMapper for exceptions
 *
 * @author Leif Lislegård
 * @since 0.6
 */
abstract class AbstractServiceExceptionTypeMapper<WsapiT extends ServiceException, DomainT extends SkifException> extends AbstractTypeMapper<WsapiT, DomainT> {

    private SkifTestExceptionMapping mapping;

    protected AbstractServiceExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }


    public SkifTestExceptionMapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = (SkifTestExceptionMapping) mapping;
    }
}
