package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Baseklasse for mappere som mapper mellom en form for java.util.Date og en wrappet {@link javax.xml.datatype.XMLGregorianCalendar}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public abstract class AbstractJavaDateTypeMapper<WsapiT, DomainT> extends AbstractWrappedDateTypeMapper<WsapiT, DomainT> {

    public AbstractJavaDateTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, String wrappedPropertyName) {
        super(wsapiClass, domainClass, wrappedPropertyName);
    }

    // Gir en kalender som er gregoriansk hele veien, uten noe skifte til juliansk
    protected static GregorianCalendar createPureGregorianCalendar(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTime(date);
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        return calendar;
    }

}
