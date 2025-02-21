package no.statkart.skif.skiftest.wsapi.exception.simple.mapping;

import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.MappingResolver;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Convenience klasse for test-implementasjon.
 *
 * Alle metoder kaster {@link NotImplementedException}
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public abstract class AbstractSkifTestSimpleExceptionMapper implements ExceptionMapping {

    @Override
    public <T> T d2w(Object source, Class<T> targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public <T> T w2d(Object source, Class<T> targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public Object d2w(Object source, Type sourceClass, Type targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public Object w2d(Object source, Type sourceClass, Type targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public <T> T d2w(Object source, TypeLiteral<T> targetType) {
        throw new NotImplementedException("na");
    }

    @Override
    public <T> T w2d(Object source, TypeLiteral<T> targetType) {
        throw new NotImplementedException("na");
    }

    @Override
    public String d2w(String source) {
        throw new NotImplementedException("na");
    }

    @Override
    public String w2d(String source) {
        throw new NotImplementedException("na");
    }

    @Override
    public Integer d2w(Integer source) {
        throw new NotImplementedException("na");
    }

    @Override
    public Integer w2d(Integer source) {
        throw new NotImplementedException("na");
    }

    @Override
    public Long d2w(Long source) {
        throw new NotImplementedException("na");
    }

    @Override
    public Long w2d(Long source) {
        throw new NotImplementedException("na");
    }

    @Override
    public Boolean d2w(Boolean source) {
        throw new NotImplementedException("na");
    }

    @Override
    public Boolean w2d(Boolean source) {
        throw new NotImplementedException("na");
    }

    @Override
    public BigDecimal d2w(BigDecimal source) {
        throw new NotImplementedException("na");
    }

    @Override
    public BigDecimal w2d(BigDecimal source) {
        throw new NotImplementedException("na");
    }

    @Override
    public void registerTarget(Object source, Object target) {
    }

    @Override
    public MappingResolver getMappingResolver() {
        throw new NotImplementedException("na");
    }
}
