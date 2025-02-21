package no.statkart.skif.storetest.wsapi.mapping.testmapping;

/**
 * Klasse for testing mapping for angivelse av tid og dato over WebServices
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class DateTimeDemoImpl1 {

    public java.sql.Timestamp dateAndTime;
    public java.sql.Time time;
    public java.sql.Date date;


    public java.sql.Timestamp getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(java.sql.Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }


    public java.sql.Time getTime() {
        return time;
    }

    public void setTime(java.sql.Time time) {
        this.time = time;
    }


}
