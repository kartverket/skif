package no.statkart.skif.mapper;

/**
 * Definerer mapping2 mellom Domain Exceptions og Web Service API Exceptions.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public interface ExceptionMapping extends Mapping {

    /**
     * Fra internt til API ...
     * @return API object
     */
    Throwable d2w(Throwable source);

    /**
     * Fra API til internt ...
     * @return domain object
     */
    Throwable w2d(Throwable source);

}

