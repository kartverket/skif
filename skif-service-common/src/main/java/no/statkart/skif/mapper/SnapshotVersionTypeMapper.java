package no.statkart.skif.mapper;

import com.google.common.base.Joiner;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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
public class SnapshotVersionTypeMapper extends AbstractTypeMapper<XMLGregorianCalendar, SnapshotVersion, Mapping> {
    public SnapshotVersionTypeMapper() {
        super(XMLGregorianCalendar.class, SnapshotVersion.class, Mapping.class);
    }

    @Override
    public XMLGregorianCalendar mapDomainObject(SnapshotVersion source) {
        GregorianCalendar pureGregorianCalendar = createPureGregorianCalendar(source.getTimestamp());
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(pureGregorianCalendar);
            xmlGregorianCalendar.setFractionalSecond(BigDecimal.valueOf(source.getTimestamp().getNanos(), 9));
            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public SnapshotVersion mapWsapiObject(XMLGregorianCalendar source) {
        validate(source);

        GregorianCalendar instance = new GregorianCalendar();
        instance.clear();
        instance.setTimeZone(source.getTimeZone(DatatypeConstants.FIELD_UNDEFINED));
        instance.set(source.getYear(), source.getMonth() - 1, source.getDay(), source.getHour(), source.getMinute(), source.getSecond());

        Timestamp timestamp = new Timestamp(instance.getTimeInMillis());

        if (source.getFractionalSecond() != null) {
            timestamp.setNanos(source.getFractionalSecond().scaleByPowerOfTen(9).intValue());
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
