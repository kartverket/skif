package no.statkart.skif.skiftest.wsapi.exception.mapping;

import no.statkart.skif.skiftest.wsapi.exception.SystemException;
import no.statkart.skif.skiftest.wsapi.exception.SystemFaultInfo;

/**
 * Mapper som må instansieres for hver type exception som skal mappes ut.
 *
 * Denne mapperen legger til stacktrace.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class SystemExceptionTypeMapper<WsapiT extends SystemException, WsapiTInfo extends SystemFaultInfo, DomainT extends no.statkart.skif.exception.SystemException> extends ServiceExceptionTypeMapper<WsapiT, WsapiTInfo, DomainT> {

    public SystemExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<WsapiTInfo> wsapiFaultInfoClass) {
        super(wsapiClass, domainClass, wsapiFaultInfoClass);
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        //stack trace filled in base class..
    }
}
