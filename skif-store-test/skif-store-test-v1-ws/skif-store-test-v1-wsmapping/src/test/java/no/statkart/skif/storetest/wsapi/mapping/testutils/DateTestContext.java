package no.statkart.skif.storetest.wsapi.mapping.testutils;

import no.statkart.skif.exception.NotImplementedException;
import org.testng.Assert;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Test klasse for testing av tidsangivelse og tidsforskjeller.
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class DateTestContext {

    public final TimeZone timeZone;

    public final GregorianCalendar calendar;
    public Date date;

    /**
     * Tidsforskjell mellom UTC og lokal tid
     */
    public final int offsetInMillis;
    /**
     * Tidsforskjell mellom UTC og lokal tid
     */
    public final int offsetInSecounds;
    /**
     * Tidsforskjell mellom UTC og lokal tid
     */
    public final int offsetInMinutes;
    /**
     * Tidsforskjell mellom UTC og lokal tid
     */
    public final int offsetInHours;


    public DateTestContext(TimeZone timeZone) {
        this.timeZone = timeZone;

        if (timeZone == TimeZone.getDefault()) {
            calendar = new GregorianCalendar();
        } else {
            calendar = new GregorianCalendar(timeZone);
        }

        offsetInMillis = timeZone.getRawOffset();
        offsetInSecounds = offsetInMillis / 1000;
        offsetInMinutes = offsetInSecounds / 60;
        offsetInHours = ( offsetInMinutes / 60);
    }


    public void setLocalDate(Date date) {
        calendar.clear();
        this.date = date;
        calendar.setTime(date);
    }

    public long timeInMillisUTC() {
        return calendar.getTimeInMillis();
    }

    public int localHours() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int timeZoneDifferenceInHours(DateTestContext other) {
        return offsetInHours * -1 + other.offsetInHours;
    }

    public int localMinutes() {
        return calendar.get(Calendar.MINUTE);
    }

    public int localSeconds() {
        return calendar.get(Calendar.SECOND);
    }

    public BigDecimal localFractionalSecond() {
        if (date instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) date;
            return BigDecimal.valueOf(timestamp.getNanos(), 9);
        } else {
            return BigDecimal.valueOf(calendar.get(Calendar.MILLISECOND), 3);
        }
    }


    public XMLGregorianCalendar buildXMLGregorianCalendar() throws DatatypeConfigurationException {

        final XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        if (date instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) date;
            xmlCalendar.setFractionalSecond(BigDecimal.valueOf(timestamp.getNanos(), 9));
        }

        return xmlCalendar;
    }

    /**
     * Verifiserer at kalender objekt har definert samme tidssone
     */
    public void assertSameTimeZoneFor(XMLGregorianCalendar actual, String message) {
        Assert.assertEquals(actual.getTimezone(), offsetInMinutes, String.format("timezone offsett in minutes (%s)", message));
        Assert.assertEquals(actual.toGregorianCalendar().getTimeZone().getRawOffset(), offsetInMillis, String.format("Time zone offset in millis (%s)", message));
        Assert.assertEquals(actual.toGregorianCalendar().getTime().getTimezoneOffset(), offsetInMinutes * (-1), String.format("Time zone offset relative to UTC (%s)", message));
    }

    public void assertSameTimeZoneFor(java.util.Date actual, String message) {
        Assert.assertEquals(actual.getTimezoneOffset(), offsetInMinutes * (-1), String.format("Time zone offset relative to UTC (%s)", message));
    }


    public enum FILTER {
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND,
    }

    /**
     * Tester at representasjon av tid har samme verdi på tidslinjen.
     * @param filters optional filtrering av felter som sjekkes. Se {@link FILTER}.
     */
    public void assertSameTimeFor(XMLGregorianCalendar actual, String message, FILTER... filters) {
        if (filters == null) {
            Assert.assertEquals(actual.toGregorianCalendar().getTime().getTime(), date.getTime(), String.format("Date for (%s)", message));
        } else {
            final GregorianCalendar actualGregorianCalendar = actual.toGregorianCalendar(timeZone, Locale.getDefault(), null);
            assertSameTimeFor(actualGregorianCalendar, message, filters);
        }
    }

    public void assertSameTimeFor(GregorianCalendar actualGregorianCalendar, String message, FILTER... filters) {
        for (FILTER filter : filters) {
            switch (filter) {
                case YEAR:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR), String.format("%s for (%s)", filter, message)); break;
                case MONTH:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.MONTH), calendar.get(Calendar.MONTH), String.format("%s for (%s)", filter, message)); break;
                case DAY:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH), String.format("%s for (%s)", filter, message)); break;
                case HOUR:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR_OF_DAY), String.format("%s for (%s)", filter, message)); break;
                case MINUTE:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.MINUTE), calendar.get(Calendar.MINUTE), String.format("%s for (%s)", filter, message)); break;
                case SECOND:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.SECOND), calendar.get(Calendar.SECOND), String.format("%s for (%s)", filter, message)); break;
                case MILLISECOND:
                    Assert.assertEquals(actualGregorianCalendar.get(Calendar.MILLISECOND), calendar.get(Calendar.MILLISECOND), String.format("%s for (%s)", filter, message)); break;
                default:
                    throw new NotImplementedException();

            }
        }
    }


    /**
     * Tester at representasjon av tid har samme verdi på tidslinjen.
     */
    public void assertSameTimeFor(java.sql.Time actual, String message) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.clear();
        gregorianCalendar.setTimeInMillis(actual.getTime());

        assertSameTimeFor(gregorianCalendar, message, FILTER.HOUR, FILTER.MINUTE, FILTER.SECOND);
    }

    /**
     * Tester at representasjon av tid har samme verdi på tidslinjen.
     */
    public void assertSameTimeFor(java.sql.Date actual, String message) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.clear();
        gregorianCalendar.setTimeInMillis(actual.getTime());

        assertSameTimeFor(gregorianCalendar, message, FILTER.YEAR, FILTER.MONTH, FILTER.DAY);
    }

    /**
     * Tester at representasjon av tid har samme verdi på tidslinjen.
     */
    public void assertSameTimeFor(java.sql.Timestamp actual, String message) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.clear();
        gregorianCalendar.setTimeInMillis(actual.getTime());

        assertSameTimeFor(gregorianCalendar, message);
    }


    /**
     * Tester at representasjon av tid har samme verdi på tidslinjen.
     */
    public void assertSameTimeFor(java.util.Date actual, String message) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.clear();
        gregorianCalendar.setTimeInMillis(actual.getTime());

        assertSameTimeFor(gregorianCalendar, message, FILTER.YEAR, FILTER.MONTH, FILTER.DAY);
    }

}
