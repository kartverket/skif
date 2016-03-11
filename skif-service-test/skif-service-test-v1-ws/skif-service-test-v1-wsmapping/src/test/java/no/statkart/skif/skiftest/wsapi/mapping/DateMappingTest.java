package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;

/**
 * Tester mapping av wrappede datotyper.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test
public class DateMappingTest {
    public void test() {
        DateMappingTestMapping mapping = new DateMappingTestMapper().getMapping();

        long now = System.currentTimeMillis();

        DatesAndTimes domainObject = new DatesAndTimes();
        domainObject.setTimestamp(new Timestamp(now));
        domainObject.setLocalDateTime(new LocalDateTime(now));
        domainObject.setLocalDate(new LocalDate(now));
        domainObject.setLocalTime(new LocalTime(now));

        no.statkart.skif.skiftest.wsapi.domain.DatesAndTimes wsObject = mapping.d2w(domainObject);

        Assert.assertNotNull(wsObject);
        Assert.assertNotNull(wsObject.getTimestamp());
        Assert.assertNotNull(wsObject.getTimestamp().getTimestamp());
        Assert.assertNotNull(wsObject.getLocalDateTime());
        Assert.assertNotNull(wsObject.getLocalDateTime().getDateTime());
        Assert.assertNotNull(wsObject.getLocalDate());
        Assert.assertNotNull(wsObject.getLocalDate().getDate());
        Assert.assertNotNull(wsObject.getLocalTime());
        Assert.assertNotNull(wsObject.getLocalTime().getTime());

        DatesAndTimes mappedObject = mapping.w2d(wsObject);

        Assert.assertEquals(mappedObject.getTimestamp(), domainObject.getTimestamp());
        Assert.assertEquals(mappedObject.getLocalDate(), domainObject.getLocalDate());
        Assert.assertEquals(mappedObject.getLocalTime(), domainObject.getLocalTime());
        Assert.assertEquals(mappedObject.getLocalDateTime(), domainObject.getLocalDateTime());
    }

    public static class DatesAndTimes {
        private Timestamp timestamp;
        private LocalDate localDate;
        private LocalTime localTime;
        private LocalDateTime localDateTime;

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }
    }

    private interface DateMappingTestMapping extends Mapping {
        no.statkart.skif.skiftest.wsapi.domain.DatesAndTimes d2w(DatesAndTimes source);
        DatesAndTimes w2d(no.statkart.skif.skiftest.wsapi.domain.DatesAndTimes source);
    }

    private static class DateMappingTestMapper extends AbstractMapper<DateMappingTestMapping> {
        public DateMappingTestMapper() {
            super(DateMappingTestMapping.class);

            addMapper(new DefaultTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.DatesAndTimes.class, DatesAndTimes.class, DateMappingTestMapping.class));
            addMapper(TimestampTypeMapper.create(no.statkart.skif.skiftest.wsapi.domain.Timestamp.class));
            addMapper(LocalDateTimeTypeMapper.create(no.statkart.skif.skiftest.wsapi.domain.LocalDateTime.class));
            addMapper(LocalDateTypeMapper.create(no.statkart.skif.skiftest.wsapi.domain.LocalDate.class));
            addMapper(LocalTimeTypeMapper.create(no.statkart.skif.skiftest.wsapi.domain.LocalTime.class));
        }
    }
}
