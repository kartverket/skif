package no.statkart.skif.skiftest.wsapi.exception.mapping;

import no.statkart.skif.skiftest.wsapi.exception.ApplicationException;
import no.statkart.skif.skiftest.wsapi.exception.ApplicationFaultInfo;

/**
 *
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class ApplicationExceptionTypeMapper<WsapiT extends ApplicationException, WsapiTInfo extends ApplicationFaultInfo, DomainT extends no.statkart.skif.exception.ApplicationException> extends ServiceExceptionTypeMapper<WsapiT, WsapiTInfo, DomainT> {

    public ApplicationExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<WsapiTInfo> wsapiFaultInfoClass) {
        super(wsapiClass, domainClass, wsapiFaultInfoClass);
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);

        ApplicationFaultInfo faultInfo = source.getFaultInfo();
        if (faultInfo != null) {
            target.setFeilkode(faultInfo.getFeilkode());
            target.setFeilkodebeskrivelse(faultInfo.getFeilkodebeskrivelse());
        }
    }
}
