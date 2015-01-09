package no.statkart.skif.skiftest.wsapi.exception.impl.mapping;

import no.statkart.skif.exception.SkifException;
import no.statkart.skif.skiftest.wsapi.exception.impl.ServiceFaultInfo;

/**
 * Generell mapper av {@link SkifException}.
 */
class ServiceFaultInfoTypeMapper extends SkifTestFaultInfoTypeMapper<ServiceFaultInfo, SkifException> {
    public ServiceFaultInfoTypeMapper() {
        super(ServiceFaultInfo.class, SkifException.class);
    }
}
