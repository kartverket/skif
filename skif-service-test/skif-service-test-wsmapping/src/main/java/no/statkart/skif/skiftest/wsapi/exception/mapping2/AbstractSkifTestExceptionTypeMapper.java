package no.statkart.skif.skiftest.wsapi.exception.mapping2;

import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;

/**
 * Abstract TypeMapper for exceptions
 *
 * @author Leif Lislegård
 * @since 0.6
 */
abstract class AbstractSkifTestExceptionTypeMapper<WsapiT extends Throwable, DomainT extends Throwable> extends AbstractTypeMapper<WsapiT, DomainT> {

    private SkifTestExceptionMapping2 mapping;

    protected AbstractSkifTestExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }


    public SkifTestExceptionMapping2 getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = (SkifTestExceptionMapping2) mapping;
    }
}
