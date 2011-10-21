package no.statkart.skif.storetest.wsapi.exception.simple.mapping;

import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.mapper.ExceptionMapping;
import no.statkart.skif.mapper.ObjectFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Collection;

/**
 * Conveniece klasse for test-implementasjon.
 *
 * Alle metoder kaster {@link NotImplementedException}
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public abstract class AbstractStoreTestExceptionMapper2 implements ExceptionMapping {

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

    public <T extends Collection> T d2w(Collection source, T target) {
        throw new NotImplementedException("na");
    }

    public <T extends Collection> T w2d(Collection source, T target) {
        throw new NotImplementedException("na");
    }

    public ObjectFactory getDomainObjectFactory() {
        throw new NotImplementedException("na");
    }

    public ObjectFactory getWsapiObjectFactory() {
        throw new NotImplementedException("na");
    }
}
