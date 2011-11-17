package no.statkart.skif.mapper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class DateTypeMapper extends AbstractTypeMapper<XMLGregorianCalendar, Date> {

    public DateTypeMapper() {
        super(XMLGregorianCalendar.class, Date.class);
    }

    @Override
    public Mapping getMapping() {
        return null;
    }

    @Override
    public void setMapping(Mapping mapping) {
    }

    @Override
    public void mapDomainObject(Date source, XMLGregorianCalendar target) {
        super.mapDomainObject(source, target);

    }

    @Override
    public void mapWsapiObject(XMLGregorianCalendar source, Date target) {
        super.mapWsapiObject(source, target);
    }

    // Gir en kalender som er gregoriansk hele veien, uten noe skifte til juliansk
    private GregorianCalendar createPureGregorianCalendar(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTime(date);
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        return calendar;
    }

    @Override
    protected XMLGregorianCalendar getInitialWsapiObject(Date source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final GregorianCalendar gregorianCalendar = createPureGregorianCalendar(source);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Date getInitialDomainObject(XMLGregorianCalendar source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GregorianCalendar instance = new GregorianCalendar();
        instance.clear();
        instance.set(source.getYear() > Integer.MIN_VALUE ? source.getYear() : 0, source.getMonth() > Integer.MIN_VALUE ? source.getMonth() - 1 : 0, source.getDay() > Integer.MIN_VALUE ? source.getDay() : 0, source.getHour() > Integer.MIN_VALUE ? source.getHour() : 0, source.getMinute() > Integer.MIN_VALUE ? source.getMinute() : 0, source.getSecond() > Integer.MIN_VALUE ? source.getSecond() : 0);
        instance.set(Calendar.MILLISECOND, source.getMillisecond() > Integer.MIN_VALUE ? source.getMillisecond() : 0);

        return new Date(instance.getTimeInMillis());
    }
}