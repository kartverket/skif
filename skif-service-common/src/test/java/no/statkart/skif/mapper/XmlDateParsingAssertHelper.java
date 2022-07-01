package no.statkart.skif.mapper;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.function.Function;

class XmlDateParsingAssertHelper<T> {
    private final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    private final TypeMapper<T, ?> mapper;
    private final Function<XMLGregorianCalendar, T> wrapper;

    XmlDateParsingAssertHelper(TypeMapper<T, ?> mapper, Function<XMLGregorianCalendar, T> wrapper) throws DatatypeConfigurationException {
        this.mapper = mapper;
        this.wrapper = wrapper;
    }

    private void parseAndMap(String xmlValue) {
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(xmlValue);
        T wrapped = wrapper.apply(xmlGregorianCalendar);
        mapper.mapWsapiObject(wrapped);
    }

    AbstractThrowableAssert<?, ? extends Throwable> assertMappingException(String xmlValue) {
        return Assertions.assertThatCode(() -> parseAndMap(xmlValue))
                .as(xmlValue)
                .isInstanceOf(MappingException.class);
    }

    void assertParseException(String xmlValue) {
        Assertions.assertThatCode(() -> parseAndMap(xmlValue))
                .as(xmlValue)
                .isInstanceOf(IllegalArgumentException.class);
    }

    void assertSuccess(String xmlValue) {
        Assertions.assertThatCode(() -> parseAndMap(xmlValue))
                .as(xmlValue)
                .doesNotThrowAnyException();
    }
}