package no.statkart.skif.skiftest.wsapi.exception.impl.mapping;

import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.impl.SimpleFaultInfo;

/**
 * Spesialmapper for {@link SimpleException} (og eventuelle subtyper).
 */
class SimpleFaultInfoTypeMapper extends SkifTestFaultInfoTypeMapper<SimpleFaultInfo, SimpleException> {
    public SimpleFaultInfoTypeMapper() {
        super(SimpleFaultInfo.class, SimpleException.class);
    }
}
