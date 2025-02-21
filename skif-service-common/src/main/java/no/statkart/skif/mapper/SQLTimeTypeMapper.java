package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Time;
import java.util.GregorianCalendar;
import static no.statkart.skif.mapper.SQLTimestampTypeMapper.createPureGregorianCalendar;

/**
 * Mapper mellom {@link java.sql.Time} og {@link XMLGregorianCalendar}.
 * Typen i XML-skjema skal være {@code xs:time}.
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class SQLTimeTypeMapper extends AbstractTypeMapper<XMLGregorianCalendar, java.sql.Time, Mapping> {
    @SuppressWarnings("FieldCanBeLocal")
    private boolean mandatoryTimeZone = true;

    public SQLTimeTypeMapper() {
        super(XMLGregorianCalendar.class, java.sql.Time.class, Mapping.class);
    }


    @Override
    public XMLGregorianCalendar mapDomainObject(Time source) {
        GregorianCalendar gregorianCalendar = createPureGregorianCalendar();

        gregorianCalendar.setTimeInMillis(source.getTime());

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public Time mapWsapiObject(XMLGregorianCalendar source) {
        validate(source);

        return new Time(source.toGregorianCalendar().getTimeInMillis());
    }

    public static XMLGregorianCalendar mapXMLGregorianCalendar(java.sql.Time time) {
        return new SQLTimeTypeMapper().mapDomainObject(time);
    }

    protected void validate(XMLGregorianCalendar source) {
        if (mandatoryTimeZone && source.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new ImplementationException("Mandatory time zone not set");
        }
    }

}
