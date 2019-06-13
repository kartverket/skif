package no.statkart.skif.mapper.test;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Custom type for holding timestamp values. This has a custom name for this field.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Timestamp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="myTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Timestamp", propOrder = {
        "myTimestamp"
})
public class Timestamp {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar myTimestamp;

    /**
     * Gets the value of the timestamp property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getMyTimestamp() {
        return myTimestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setMyTimestamp(XMLGregorianCalendar value) {
        this.myTimestamp = value;
    }

}