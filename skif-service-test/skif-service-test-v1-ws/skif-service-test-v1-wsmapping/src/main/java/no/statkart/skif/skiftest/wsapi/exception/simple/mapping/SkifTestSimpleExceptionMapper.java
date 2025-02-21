package no.statkart.skif.skiftest.wsapi.exception.simple.mapping;

import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.simple.SimpleFaultInfo;

/**
 * Enkel exception mapper for SkifTest som bare kan mapper SimpleExcpetion over JAX-WS. I tillegg mappes alle runtime exceptions på
 * klienten via {@link no.statkart.skif.mapper.IdentityExceptionTypeMapper} slik at runtime exception fra JAX-WS kommer igjennom til klient.
 *
 * <P></P>Her benyttes et eget hirarki av exceptions og ikke skif's stuktur.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestSimpleExceptionMapper extends AbstractSkifTestSimpleExceptionMapper {

    public ExceptionMapping getMapping() {
        return this;
    }

    @Override
    public Throwable d2w(Throwable source) {
        if (source instanceof no.statkart.skif.skiftest.exception.SimpleException) {
            //noinspection unchecked,ThrowableResultOfMethodCallIgnored
            return buildExternalSimpleException((no.statkart.skif.skiftest.exception.SimpleException) source);
        }

        //feilmelding på kjent format (benyttes i tester)
        throw new MappingException(String.format("TypeMapper[%s] could not map from %s to %s", this.getClass().getName(), source.getClass().getName(), Throwable.class.getName()));
    }

    private SimpleException buildExternalSimpleException(no.statkart.skif.skiftest.exception.SimpleException source) {
        SimpleFaultInfo faultInfo = new SimpleFaultInfo();
        faultInfo.setInfoField(source.getInfoField());
        return new SimpleException(source.getLocalizedMessage(), faultInfo);
    }


    @Override
    public Throwable w2d(Throwable source) {
        if (source instanceof SimpleException) {
            //noinspection ThrowableResultOfMethodCallIgnored,unchecked
            return buildInternalSimpleException((SimpleException) source);
        }

        if (source instanceof Error) {
            return source;
        } else if (source instanceof RuntimeException) {
            return source;
        }

        //feilmelding på kjent format (benyttes i tester)
        throw new MappingException(String.format("TypeMapper[%s] could not map from %s to %s", this.getClass().getName(), source.getClass().getName(), Throwable.class.getName()));
    }

    private no.statkart.skif.skiftest.exception.SimpleException buildInternalSimpleException(SimpleException source) {
        String message = source.getLocalizedMessage();
        String infoField = source.getFaultInfo().getInfoField();
        return new no.statkart.skif.skiftest.exception.SimpleException(message, infoField);
    }

}