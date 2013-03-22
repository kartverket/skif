package no.statkart.skif.skiftest.wsapi.exception.simple.mapping;

import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.MapperInfo;
import no.statkart.skif.mapper.ObjectFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Conveniece klasse for test-implementasjon.
 *
 * Alle metoder kaster {@link NotImplementedException}
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public abstract class AbstractSkifTestExceptionMapper2 implements ExceptionMapping {
    @Override
    public Object d2w(Object source, Type targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public Object w2d(Object source, Type targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public Object[] d2w(Object[] source, Type[] webServiceParameterTypes) {
        throw new NotImplementedException("na");
    }

    @Override
    public Object[] w2d(Object[] source, Type[] domainServiceParameterTypes) {
        throw new NotImplementedException("na");
    }

    public <T> T d2w(Object source) {
        throw new NotImplementedException("na");
    }

    public <T> T w2d(Object source) {
        throw new NotImplementedException("na");
    }


    public <T> T d2w(Object source, Class<T> targetClass) {
        throw new NotImplementedException("na");
    }

    public <T> T w2d(Object source, Class<T> targetClass) {
        throw new NotImplementedException("na");
    }

    @Override
    public <T> T w2d(Object source, Class<T> targetClass, MapperInfo mapperInfo) {
        throw new NotImplementedException("na");
    }


    public Object[] d2w(Object[] source, Class<?>[] webServiceParameterTypes) {
        throw new NotImplementedException("na");
    }

    public Object[] w2d(Object[] source, Class<?>[] domainServiceParameterTypes) {
        throw new NotImplementedException("na");
    }

    public String d2w(String source) {
        throw new NotImplementedException("na");
    }

    public String w2d(String source) {
        throw new NotImplementedException("na");
    }

    public Integer d2w(Integer source) {
        throw new NotImplementedException("na");
    }

    public Integer w2d(Integer source) {
        throw new NotImplementedException("na");
    }

    public Long d2w(Long source) {
        throw new NotImplementedException("na");
    }

    public Long w2d(Long source) {
        throw new NotImplementedException("na");
    }

    public Boolean d2w(Boolean source) {
        throw new NotImplementedException("na");
    }

    public Boolean w2d(Boolean source) {
        throw new NotImplementedException("na");
    }

    public XMLGregorianCalendar d2w(Date source) {
        throw new NotImplementedException("na");
    }

    public Date w2d(XMLGregorianCalendar source) {
        throw new NotImplementedException("na");
    }

    public BigDecimal d2w(BigDecimal source) {
        throw new NotImplementedException("na");
    }

    public BigDecimal w2d(BigDecimal source) {
        throw new NotImplementedException("na");
    }

    public <T extends Object> T d2w(Object source, T target) {
        throw new NotImplementedException("na");
    }

    public <T extends Object> T w2d(Object source, T target) {
        throw new NotImplementedException("na");
    }

    public ObjectFactory getDomainObjectFactory() {
        throw new NotImplementedException("na");
    }

    public ObjectFactory getWsapiObjectFactory() {
        throw new NotImplementedException("na");
    }
}
