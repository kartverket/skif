package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import static no.statkart.skif.mapper.SQLTimestampTypeMapper.createPureGregorianCalendar;

/**
 * Mapper mellom {@link java.sql.Date} og {@link XMLGregorianCalendar}.
 * Typen i XML-skjema skal være {@code xs:date}.
 *
 * @author Leif Lislegård
 * @since 1.0 - ny grunnbok sprint 29
 */
public class SQLDateTypeMapper extends AbstractTypeMapper<XMLGregorianCalendar, java.sql.Date, Mapping> {
    private final boolean mandatoryTimeZone = true;

    public SQLDateTypeMapper() {
        super(XMLGregorianCalendar.class, java.sql.Date.class, Mapping.class);
    }


    @Override
    public XMLGregorianCalendar mapDomainObject(java.sql.Date source) {
        GregorianCalendar gregorianCalendar = createPureGregorianCalendar();
        gregorianCalendar.setTime(source);
        try {
            final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

            xmlGregorianCalendar.setFractionalSecond(null); //clears field

            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public java.sql.Date mapWsapiObject(XMLGregorianCalendar source) {
        validate(source);

        final GregorianCalendar gregorianCalendar = source.toGregorianCalendar();

        java.sql.Date target = new java.sql.Date(gregorianCalendar.getTimeInMillis());
//        Timestamp target = new Timestamp(gregorianCalendar.get(Calendar.YEAR)-1900, gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DATE), gregorianCalendar.get(Calendar.HOUR_OF_DAY), gregorianCalendar.get(Calendar.MINUTE), gregorianCalendar.get(Calendar.SECOND), gregorianCalendar.get(Calendar.MILLISECOND));

        return target;
    }


    public static XMLGregorianCalendar mapXMLGregorianCalendar(java.sql.Date date) {
        return new SQLDateTypeMapper().mapDomainObject(date);
    }

    protected void validate(XMLGregorianCalendar source) {
        if (mandatoryTimeZone && source.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new ImplementationException("Mandatory time zone not set");
        }
    }

}
