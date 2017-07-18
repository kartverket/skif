package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Baseklasse for mappere som mapper mellom en form for dato og/eller klokkeslett og en wrappet {@link javax.xml.datatype.XMLGregorianCalendar}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public abstract class AbstractWrappedDateTypeMapper<WsapiT, DomainT> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
    protected final PropertyDescriptor wrappedProperty;
    protected final DatatypeFactory datatypeFactory;

    public AbstractWrappedDateTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, String wrappedPropertyName) {
        super(wsapiClass, domainClass, Mapping.class);

        try {
            wrappedProperty = new PropertyDescriptor(wrappedPropertyName, wsapiClass);
        } catch (IntrospectionException e) {
            throw new ImplementationException("Accessors for " + wrappedPropertyName + " property not found", e);
        }

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    protected WsapiT wrap(XMLGregorianCalendar xmlGregorianCalendar) {
        WsapiT target = createWsapiT();

        try {
            wrappedProperty.getWriteMethod().invoke(target, xmlGregorianCalendar);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException("Could not set " + wrappedProperty.getName(), e);
        }
        return target;
    }

    protected XMLGregorianCalendar unwrap(WsapiT source) {
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = (XMLGregorianCalendar) wrappedProperty.getReadMethod().invoke(source);
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
            throw new MappingException("Could not get " + wrappedProperty.getName(), e);
        }
        return xmlGregorianCalendar;
    }
}
