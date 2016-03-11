package no.statkart.skif.mapper;


/**
 * Slipper exception av angitt type uendret igjennom.
 *
 * @param <T> exception type som skal slippes tvert igjennom
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class IdentityExceptionTypeMapper<T extends Throwable> extends AbstractTypeMapper<T, T, Mapping> {

    public IdentityExceptionTypeMapper(Class<T> exceptionClass) {
        super(exceptionClass, exceptionClass, Mapping.class);
    }

    @Override
    public T mapDomainObject(T source) {
        return source;
    }

    @Override
    public T mapWsapiObject(T source) {
        return source;
    }
}
