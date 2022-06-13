package no.statkart.skif.mapper;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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

    @Test
    void parsingAvFeilformaterteDatoer() {
        Assertions.assertThatCode(() -> {
                    WrappedLocalDate localDate = parseXmlRepresentation("1814-05-17T00:00:00");
                    mapper.mapWsapiObject(localDate);
                })
                .as("Med klokkeslett")
                .isInstanceOf(MappingException.class)
                .hasMessage("Can't map LocalDate: LocalDate can't have hours. LocalDate can't have minutes. LocalDate can't have seconds.");
        Assertions.assertThatCode(() -> {
                    WrappedLocalDate localDate = parseXmlRepresentation("1814-05-17+01:00");
                    mapper.mapWsapiObject(localDate);
                })
                .as("Med tidssone")
                .isInstanceOf(MappingException.class)
                .hasMessage("Can't map LocalDate: LocalDate can't have time zone.");
        Assertions.assertThatCode(() -> {
                    WrappedLocalDate localDate = parseXmlRepresentation("1814-05");
                    mapper.mapWsapiObject(localDate);
                })
                .as("Uten dag")
                .isInstanceOf(MappingException.class)
                .hasMessage("Can't map LocalDate: Day is not specified.");
        Assertions.assertThatCode(() -> {
                    WrappedLocalDate localDate = parseXmlRepresentation("12:00:00");
                    mapper.mapWsapiObject(localDate);
                })
                .as("Bare klokkeslett")
                .isInstanceOf(MappingException.class)
                .hasMessage("Can't map LocalDate: Year is not specified. Month is not specified. Day is not specified. LocalDate can't have hours. LocalDate can't have minutes. LocalDate can't have seconds.");
    }

    WrappedLocalDate viaXml(WrappedLocalDate input) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(input, baos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return (WrappedLocalDate) unmarshaller.unmarshal(bais);
    }

    /**
     * Parser bare selve xs:date-strengen, ikke XML-tagger og slikt.
     */
    WrappedLocalDate parseXmlRepresentation(String xmlRepresentation) throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(xmlRepresentation);
        WrappedLocalDate wrappedLocalDate = new WrappedLocalDate();
        wrappedLocalDate.setDate(xmlGregorianCalendar);
        return wrappedLocalDate;
    }

    @XmlRootElement(name = "wrapper", namespace = "http://skif.statkart.no/javalocaldatetypemapper/")
    public static class WrappedLocalDate {
        private XMLGregorianCalendar date;

        @SuppressWarnings("unused")
        public WrappedLocalDate() {
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
