package no.statkart.skif.mapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test av {@link DateTypeMapper}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class DateTypeMapperTest {
    private final DateTypeMapper dateTypeMapper = new DateTypeMapper();

    // SKIF-23
    @Test
    public void test_0001_12_31() {
        Date date = java.sql.Date.valueOf("0001-12-31");

        XMLGregorianCalendar xmlGregorianCalendar = dateTypeMapper.mapDomainObject(date);
        Assert.assertEquals(xmlGregorianCalendar.getYear(), 1, "År " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getMonth(), 12, "Måned " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getDay(), 31, "Dag " + xmlGregorianCalendar);

        assertThat(dateTypeMapper.mapWsapiObject(xmlGregorianCalendar))
            .hasYear(1)
            .hasMonth(12)
            .hasDayOfMonth(31);
    }

    @Test
    public void test_2001_12_31() {
        Date date = java.sql.Date.valueOf("2001-12-31");

        XMLGregorianCalendar xmlGregorianCalendar = dateTypeMapper.mapDomainObject(date);
        Assert.assertEquals(xmlGregorianCalendar.getYear(), 2001, "År " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getMonth(), 12, "Måned " + xmlGregorianCalendar);
        Assert.assertEquals(xmlGregorianCalendar.getDay(), 31, "Dag " + xmlGregorianCalendar);

        assertThat(dateTypeMapper.mapWsapiObject(xmlGregorianCalendar))
            .hasYear(2001)
            .hasMonth(12)
            .hasDayOfMonth(31);
    }
}
