package no.statkart.skif.mapper;

import no.statkart.skif.exception.SkifException;

/**
 * Definerer mapping2 mellom Domain Exceptions og Web Service API Exceptions.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public interface ExceptionMapping extends Mapping {

    public <T extends Exception, S extends SkifException> T d2w(S source, Class<T> targetClass);
    public <S extends Exception, T extends SkifException> T w2d(S source, Class<T> targetClass);

}

