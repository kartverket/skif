package no.statkart.skif.storetest.wsapi.mapping.testmapping;

import java.util.Date;

/**
 * Klasse for testing mapping for angivelse av tid og dato over WebServices
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class DateTimeDemoImpl2 {

    public java.util.Date dateAndTime;
    public java.util.Date time;
    public java.util.Date date;


    public java.util.Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(java.util.Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
