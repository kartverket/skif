package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

/**
 * Mapper mellom {@link java.sql.Timestamp} og {@link XMLGregorianCalendar}.
 * Typen i XML-skjema skal være {@code xs:dateTime}.
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class SQLTimestampTypeMapper extends AbstractTypeMapper<XMLGregorianCalendar, java.sql.Timestamp, Mapping> {
    @SuppressWarnings("FieldCanBeLocal")
    private boolean mandatoryTimeZone = true;

    public SQLTimestampTypeMapper() {
        super(XMLGregorianCalendar.class, java.sql.Timestamp.class, Mapping.class);
    }

    @Override
    public XMLGregorianCalendar mapDomainObject(java.sql.Timestamp source) {
        GregorianCalendar gregorianCalendar = createPureGregorianCalendar();
        gregorianCalendar.setTimeInMillis(source.getTime());

        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            xmlGregorianCalendar.setFractionalSecond(BigDecimal.valueOf(source.getNanos(), 9));
            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public java.sql.Timestamp mapWsapiObject(XMLGregorianCalendar source) {
        validate(source);

        GregorianCalendar instance = new GregorianCalendar();
        instance.clear();
        instance.setTimeZone(source.getTimeZone(DatatypeConstants.FIELD_UNDEFINED));
        instance.set(source.getYear(), source.getMonth() - 1, source.getDay(), source.getHour(), source.getMinute(), source.getSecond());

        java.sql.Timestamp target = new java.sql.Timestamp(instance.getTimeInMillis());

        if (source.getFractionalSecond() != null) {
            target.setNanos(source.getFractionalSecond().scaleByPowerOfTen(9).intValue());
        }

        return target;
    }

    protected void validate(XMLGregorianCalendar source) {
        if (mandatoryTimeZone && source.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new ImplementationException("Mandatory time zone not set");
        }
    }


    public static XMLGregorianCalendar mapXMLGregorianCalendar(java.sql.Timestamp timestamp) {
        return new SQLTimestampTypeMapper().mapDomainObject(timestamp);
    }

    // Gir en kalender som er gregoriansk hele veien, uten noe skifte til juliansk
    static GregorianCalendar createPureGregorianCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setGregorianChange(new java.util.Date(Long.MIN_VALUE));
        return calendar;
    }


}
