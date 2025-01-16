package no.statkart.skif.mapper;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

public class JavaLocalDateTypeMapperTest {
    private JavaLocalDateTypeMapper<WrappedLocalDate> mapper;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    @BeforeMethod
    public void setUp() throws Exception {
        mapper = new JavaLocalDateTypeMapper<>(WrappedLocalDate.class);
        JAXBContext context = JAXBContext.newInstance(WrappedLocalDate.class);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
    }

    @Test
    void moderneDato() throws JAXBException {
        LocalDate opprinneligDato = LocalDate.of(2022, 6, 13);

        WrappedLocalDate mappedDato = mapper.mapDomainObject(opprinneligDato);

        WrappedLocalDate unmarshalledDato = viaXml(mappedDato);

        LocalDate dato = mapper.mapWsapiObject(unmarshalledDato);

        Assertions.assertThat(dato).as("rundturmappet").isEqualTo(opprinneligDato);
        Assertions.assertThat(mappedDato.getDate().toXMLFormat()).as("xml-representasjon").isEqualTo("2022-06-13");
    }

    @Test
    void gammelDato() throws JAXBException {
        LocalDate opprinneligDato = LocalDate.of(1814, 5, 17);

        WrappedLocalDate mappedDato = mapper.mapDomainObject(opprinneligDato);

        WrappedLocalDate unmarshalledDato = viaXml(mappedDato);

        LocalDate dato = mapper.mapWsapiObject(unmarshalledDato);

        Assertions.assertThat(dato).as("rundturmappet").isEqualTo(opprinneligDato);
        Assertions.assertThat(mappedDato.getDate().toXMLFormat()).as("xml-representasjon").isEqualTo("1814-05-17");
    }

    @Test
    void urgammelDato() throws JAXBException {
        LocalDate opprinneligDato = LocalDate.of(1, 1, 1);

        WrappedLocalDate mappedDato = mapper.mapDomainObject(opprinneligDato);

        WrappedLocalDate unmarshalledDato = viaXml(mappedDato);

        LocalDate dato = mapper.mapWsapiObject(unmarshalledDato);

        Assertions.assertThat(dato).as("rundturmappet").isEqualTo(opprinneligDato);
        Assertions.assertThat(mappedDato.getDate().toXMLFormat()).as("xml-representasjon").isEqualTo("0001-01-01");
    }

    /**
     * Sjekker at ingen ulovlige formateringer slipper gjennom, enten fordi parsing feiler eller fordi vi validerer.
     */
    @Test
    public void testValidering() throws DatatypeConfigurationException {
        XmlDateParsingAssertHelper<?> helper = new XmlDateParsingAssertHelper<>(mapper, WrappedLocalDate::new);

        helper.assertMappingException("2010").hasMessage("Can't map LocalDate: Month is not specified. Day is not specified.");
        helper.assertMappingException("2010-01").hasMessage("Can't map LocalDate: Day is not specified.");
        helper.assertSuccess("2010-01-01");
        helper.assertParseException("2010-01-01T00");
        helper.assertParseException("2010-01-01T00:00");
        helper.assertMappingException("2010-01-01T00:00:00").hasMessage("Can't map LocalDate: LocalDate can't have time.");
        helper.assertMappingException("2010-01-01T00:00:00+01:00").hasMessage("Can't map LocalDate: LocalDate can't have time zone. LocalDate can't have time.");
        helper.assertParseException("2010-01T00:00:00+01:00");
        helper.assertParseException("2010T00:00:00+01:00");
        helper.assertMappingException("00:00:00+01:00").hasMessage("Can't map LocalDate: LocalDate can't have time zone. Year is not specified. Month is not specified. Day is not specified. LocalDate can't have time.");
        helper.assertMappingException("00:00:00").hasMessage("Can't map LocalDate: Year is not specified. Month is not specified. Day is not specified. LocalDate can't have time.");
        helper.assertParseException("00:00");
    }

    WrappedLocalDate viaXml(WrappedLocalDate input) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(input, baos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return (WrappedLocalDate) unmarshaller.unmarshal(bais);
    }

    @XmlRootElement(name = "wrapper", namespace = "http://skif.statkart.no/javalocaldatetypemapper/")
    public static class WrappedLocalDate {
        private XMLGregorianCalendar date;

        @SuppressWarnings("unused")
        public WrappedLocalDate() {
        }

        private WrappedLocalDate(XMLGregorianCalendar date) {
            this.date = date;
        }

        @SuppressWarnings("unused")
        public XMLGregorianCalendar getDate() {
            return date;
        }

        public void setDate(XMLGregorianCalendar date) {
            this.date = date;
        }
    }
}
