package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.mapping.testmapping.DateTimeDemoImpl2;
import no.statkart.skif.storetest.wsapi.mapping.testutils.DateTestContext;
import no.statkart.skif.storetest.wsapi.mapping.testutils.DateTimeDemoTestCase;
import no.statkart.skif.storetest.wsapi.service.testmapping.DateTimeDemo;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.TimeZone;

/**
 * Testing av mapping for {@link no.statkart.skif.storetest.wsapi.mapping.testmapping.DateTimeDemoImpl2}
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class DateTimeDemo2Test extends DateTimeDemoTestCase {


    @Test
    public void testTimestampMapping_CURRENT() throws DatatypeConfigurationException {
        final DateTestContext defaultTimeContext = new DateTestContext(TimeZone.getDefault());

        //generate test data
        defaultTimeContext.setLocalDate(SnapshotVersion.CURRENT.getTimestamp());

        //create wsObjectForMappingtest
        DateTimeDemo wsDateTimeDemo = new DateTimeDemo();
        wsDateTimeDemo.setDateAndTime(defaultTimeContext.buildXMLGregorianCalendar());
        wsDateTimeDemo.setDate(defaultTimeContext.buildXMLGregorianCalendar());
        wsDateTimeDemo.setTime(defaultTimeContext.buildXMLGregorianCalendar());

        defaultTimeContext.assertSameTimeZoneFor(wsDateTimeDemo.getTime(), "test-objekt for WS");
        defaultTimeContext.assertSameTimeZoneFor(wsDateTimeDemo.getDate(), "test-objekt for WS");
        defaultTimeContext.assertSameTimeZoneFor(wsDateTimeDemo.getDateAndTime(), "test-objekt for WS");

        //mapping
        final Mapping map = mapper.getMapping();
        final DateTimeDemoImpl2 mappedDateTimeImpl = map.w2d(wsDateTimeDemo, DateTimeDemoImpl2.class);

        defaultTimeContext.assertSameTimeZoneFor(mappedDateTimeImpl.getTime(), "mappet objekt");
        defaultTimeContext.assertSameTimeZoneFor(mappedDateTimeImpl.getDate(), "mappet objekt");
        defaultTimeContext.assertSameTimeZoneFor(mappedDateTimeImpl.getDateAndTime(), "mappet objekt");

        defaultTimeContext.assertSameTimeFor(mappedDateTimeImpl.getTime(), "Time for mappet objekt");
        defaultTimeContext.assertSameTimeFor(mappedDateTimeImpl.getDate(), "Date for mappet objekt");
        defaultTimeContext.assertSameTimeFor(mappedDateTimeImpl.getDateAndTime(), "DateAndTime for mappet objekt");

        Assert.assertEquals(mappedDateTimeImpl.getTime().getClass(), java.util.Date.class, "Forventet klasse");
        Assert.assertEquals(mappedDateTimeImpl.getDate().getClass(), java.util.Date.class, "Forventet klasse");
        Assert.assertEquals(mappedDateTimeImpl.getDateAndTime().getClass(), java.util.Date.class, "Forventet klasse");


        //tilbake-mapping
        final DateTimeDemo wsDoubleMappedDateTimeDemo = map.d2w(mappedDateTimeImpl, DateTimeDemo.class);

        defaultTimeContext.assertSameTimeZoneFor(wsDoubleMappedDateTimeDemo.getTime(), "dobbelt mappet objekt");
        defaultTimeContext.assertSameTimeZoneFor(wsDoubleMappedDateTimeDemo.getDate(), "dobbelt mappet objekt");
        defaultTimeContext.assertSameTimeZoneFor(wsDoubleMappedDateTimeDemo.getDateAndTime(), "dobbelt mappet objekt");


        defaultTimeContext.assertSameTimeFor(wsDoubleMappedDateTimeDemo.getTime(), "Time for dobbelt mappet objekt");
        defaultTimeContext.assertSameTimeFor(wsDoubleMappedDateTimeDemo.getDate(), "Date for dobbelt mappet objekt");
        defaultTimeContext.assertSameTimeFor(wsDoubleMappedDateTimeDemo.getDateAndTime(), "DateAndTime for dobbelt mappet objekt");

    }


    protected AbstractMapper<Mapping> buildMapping() {
        return mapperForTypeMappers(new AbstractTypeMapper<DateTimeDemo, DateTimeDemoImpl2, Mapping>(DateTimeDemo.class, DateTimeDemoImpl2.class, Mapping.class) {
            public DateTimeDemo mapDomainObject(DateTimeDemoImpl2 source) {
                final DateTimeDemo target = new DateTimeDemo();
                target.setTime(getMapping().d2w(source.getTime()));
                target.setDate(getMapping().d2w(source.getDate()));
                target.setDateAndTime(getMapping().d2w(source.getDateAndTime()));
                return target;
            }

            @Override
            public DateTimeDemoImpl2 mapWsapiObject(DateTimeDemo source) {
                final DateTimeDemoImpl2 target = new DateTimeDemoImpl2();
                target.setTime(getMapping().w2d(source.getTime(), java.util.Date.class));
                target.setDate(getMapping().w2d(source.getTime(), java.util.Date.class));
                target.setDateAndTime(getMapping().w2d(source.getDateAndTime(), java.util.Date.class));
                return target;
            }

        });
    }


}
