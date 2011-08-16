package no.statkart.skif.storetest.wsapi.exception.simple.mapping;

import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.storetest.wsapi.exception.SimpleException;
import no.statkart.skif.storetest.wsapi.exception.simple.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;

/**
 * Enkel exception mapper for storetest som bare kan mapper SimpleExcpetion over JAX-WS. I tillegg mappes alle runtime exceptions på
 * klienten via {@link no.statkart.skif.mapper.IdentityExceptionTypeMapper} slik at runtime exception fra JAX-WS kommer igjennom til klient.
 *
 *
 * Her benyttes et eget hirarki av exceptions og ikke skif's stuktur.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public class StoreTestExceptionMapper2 extends AbstractStoreTestExceptionMapper2 {

    public ExceptionMapping getMapping() {
        return this;
    }

    @Override
    public <T extends Throwable, S extends Throwable> T d2w(S source) {
        if (source instanceof no.statkart.skif.storetest.exception.SimpleException) {
            return (T) buildExternalSimpleException((no.statkart.skif.storetest.exception.SimpleException) source);
        }
        if (source instanceof Error) {
            return (T) source;
        } else if (source instanceof RuntimeException) {
            return (T) source;
        }

        //feilmelding på kjent format (benyttes i tester)
        throw new MappingException(String.format("TypeMapper[%s] has no mapper for for class: %s", this.getClass().getName(), source.getClass().getName()));
    }

    private SimpleException buildExternalSimpleException(no.statkart.skif.storetest.exception.SimpleException source) {
        SimpleFaultInfo faultInfo = new SimpleFaultInfo();
        faultInfo.setInfoField(source.getInfoField());
        return new SimpleException(source.getLocalizedMessage(), faultInfo);
    }


    @Override
    public <S extends Throwable, T extends Throwable> T w2d(S source) {
        if (source instanceof SimpleException) {
            return (T) buildInternalSimpleException((SimpleException) source);
        }

        //feilmelding på kjent format (benyttes i tester)
        throw new MappingException(String.format("TypeMapper[%s] has no mapper for for class: %s", this.getClass().getName(), source.getClass().getName()));
    }

    private no.statkart.skif.storetest.exception.SimpleException buildInternalSimpleException(SimpleException source) {
        String message = source.getLocalizedMessage();
        String infoField = source.getFaultInfo().getInfoField();
        return new no.statkart.skif.storetest.exception.SimpleException(message, infoField);
    }

}