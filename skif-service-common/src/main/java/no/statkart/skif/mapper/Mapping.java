package no.statkart.skif.mapper;

import com.google.inject.TypeLiteral;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Collection;

/**
 * Definerer mapping2 mellom Domain Objects og Web Service API Objects. For hver objekt type må
 * følgende metoder defineres:
 *
 * <ul>
 * <li> WsapiT d2w(DomainT source)
 * <li> DomainT w2d(WsapiT source)
 * <li> WsapiTList d2w(Collection source, WsapiTList target)
 * <li> Collection d2w(Collection source, WsapiTList target)
 * </ul>
 * @author Henrik Fredholm
 */
public interface Mapping extends MappingBase {
    public <T> T d2w(Object source, Class<T> targetClass);
    public <T> T w2d(Object source, Class<T> targetClass);
    public Object d2w(Object source, Type targetType);
    public Object w2d(Object source, Type targetType);
    public <T> T d2w(Object source, TypeLiteral<T> targetType);
    public <T> T w2d(Object source, TypeLiteral<T> targetType);

    public String d2w(String source);
    public String w2d(String source);

    public Integer d2w(Integer source);
    public Integer w2d(Integer source);

    public Long d2w(Long source);
    public Long w2d(Long source);

    public Boolean d2w(Boolean source);
    public Boolean w2d(Boolean source);

    public XMLGregorianCalendar d2w(Date source);
    public Date w2d(XMLGregorianCalendar source);

    public BigDecimal d2w(BigDecimal source);
    public BigDecimal w2d(BigDecimal source);

}
