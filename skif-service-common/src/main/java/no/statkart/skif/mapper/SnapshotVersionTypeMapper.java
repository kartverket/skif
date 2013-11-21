package no.statkart.skif.mapper;

import com.google.common.base.Joiner;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Mapper mellom {@link SnapshotVersion} og {@link javax.xml.datatype.XMLGregorianCalendar}.
 * Typen i XML-skjema skal være <code>xs:dateTime</code>.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SnapshotVersionTypeMapper<WsapiT> extends AbstractTypeMapper<WsapiT, SnapshotVersion, Mapping> {
    private final PropertyDescriptor timestampProperty;

    public SnapshotVersionTypeMapper(Class<WsapiT> wsSnapshotVersionClass) {
        super(wsSnapshotVersionClass, SnapshotVersion.class, Mapping.class);

        try {
            timestampProperty = new PropertyDescriptor("timestamp", wsSnapshotVersionClass);
        } catch (IntrospectionException e) {
            throw new ImplementationException("Accessors for timestamp property not found", e);
        }
    }

    @Override
    public WsapiT mapDomainObject(SnapshotVersion source) {
        GregorianCalendar pureGregorianCalendar = createPureGregorianCalendar(source.getTimestamp());
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(pureGregorianCalendar);
            xmlGregorianCalendar.setFractionalSecond(BigDecimal.valueOf(source.getTimestamp().getNanos(), 9));

            WsapiT target = createWsapiT();

            try {
                timestampProperty.getWriteMethod().invoke(target, xmlGregorianCalendar);
            } catch (IllegalAccessException e) {
                throw new MappingException("Could not set timestamp", e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Could not set timestamp", e);
            }

            return target;
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public SnapshotVersion mapWsapiObject(WsapiT source) {
        final XMLGregorianCalendar xmlGregorianCalendar;

        try {
            xmlGregorianCalendar = (XMLGregorianCalendar) timestampProperty.getReadMethod().invoke(source);
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not get timestamp", e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not get timestamp", e);
        } catch (ClassCastException e) {
            throw new MappingException("Could not get timestamp", e);
        }

        validate(xmlGregorianCalendar);

        GregorianCalendar instance = new GregorianCalendar();
        instance.clear();
        instance.setTimeZone(xmlGregorianCalendar.getTimeZone(DatatypeConstants.FIELD_UNDEFINED));
        instance.set(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth() - 1, xmlGregorianCalendar.getDay(), xmlGregorianCalendar.getHour(), xmlGregorianCalendar.getMinute(), xmlGregorianCalendar.getSecond());

        Timestamp timestamp = new Timestamp(instance.getTimeInMillis());

        if (xmlGregorianCalendar.getFractionalSecond() != null) {
            timestamp.setNanos(xmlGregorianCalendar.getFractionalSecond().scaleByPowerOfTen(9).intValue());
        }

        return SnapshotVersion.createInstance(timestamp);
    }

    /**
     * Validerer at alle nødvendig felter er angitt. Kun tidssone og subsekunder er valgfrie.
     *
     * @param timestamp XML-dato som skal valideres som timestamp
     */
    private static void validate(XMLGregorianCalendar timestamp) {
        List<String> errorMsgs = new ArrayList<String>();
        if (timestamp.getEon() != null && timestamp.getEon().longValue() != 0)
            errorMsgs.add("SnapshotVersion can't span eons.");
        if (timestamp.getYear() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Year is not specified.");
        if (timestamp.getMonth() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Month is not specified.");
        if (timestamp.getDay() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Day is not specified.");
        if (timestamp.getHour() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Hour is not specified.");
        if (timestamp.getMinute() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Minute is not specified.");
        if (timestamp.getSecond() == DatatypeConstants.FIELD_UNDEFINED) errorMsgs.add("Second is not specified.");

        if (!errorMsgs.isEmpty()) {
            String joined = Joiner.on(' ').join(errorMsgs);
            throw new MappingException("Can't map SnapshotVersion: " + joined);
        }
    }

    // Gir en kalender som er gregoriansk hele veien, uten noe skifte til juliansk
    private static GregorianCalendar createPureGregorianCalendar(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTime(date);
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        return calendar;
    }

}
