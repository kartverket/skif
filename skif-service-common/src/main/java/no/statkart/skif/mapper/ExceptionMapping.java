package no.statkart.skif.mapper;

/**
 * Definerer mapping2 mellom Domain Exceptions og Web Service API Exceptions.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public interface ExceptionMapping extends Mapping {

    /**
     * Fra internt til API ...
     * @return API object
     */
    public <T extends Throwable, S extends Throwable> T d2w(S source);

    /**
     * Fra API til internt ...
     * @return domain object
     */
    public <S extends Throwable, T extends Throwable> T w2d(S source);

}

