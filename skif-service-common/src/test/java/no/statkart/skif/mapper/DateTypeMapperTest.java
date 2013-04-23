package no.statkart.skif.mapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;

/**
 * Test av {@link DateTypeMapper}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test(enabled = true)
public class DateTypeMapperTest {
    private final DateTypeMapper dateTypeMapper = new DateTypeMapper();

    // SKIF-23
    public void test_0001_12_31() {
        Date date = new Date(1 - 1900, 11, 31);
        Assert.assertEquals(date.toString(), "0001-12-31");

        XMLGregorianCalendar xmlGregorianCalendar = dateTypeMapper.mapDomainObject(date);
        Assert.assertEquals(xmlGregorianCalendar.getYear(), 1, "År " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getMonth(), 12, "Måned " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getDay(), 31, "Dag " + xmlGregorianCalendar);

        java.util.Date date2 = dateTypeMapper.mapWsapiObject(xmlGregorianCalendar);
        Assert.assertEquals(date2.getYear() + 1900, 1, "År " + date2);
        Assert.assertEquals(date2.getMonth(), 11, "Måned " + date2);
        Assert.assertEquals(date2.getDate(), 31, "Dag " + date2);
    }

    public void test_2001_12_31() {
        Date date = new Date(2001 - 1900, 11, 31);
        Assert.assertEquals(date.toString(), "2001-12-31");

        XMLGregorianCalendar xmlGregorianCalendar = dateTypeMapper.mapDomainObject(date);
        Assert.assertEquals(xmlGregorianCalendar.getYear(), 2001, "År " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getMonth(), 12, "Måned " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getDay(), 31, "Dag " + xmlGregorianCalendar);

        java.util.Date date2 = dateTypeMapper.mapWsapiObject(xmlGregorianCalendar);
        Assert.assertEquals(date2.getYear() + 1900, 2001, "År " + date2);
        Assert.assertEquals(date2.getMonth(), 11, "Måned " + date2);
        Assert.assertEquals(date2.getDate(), 31, "Dag " + date2);
    }
}
