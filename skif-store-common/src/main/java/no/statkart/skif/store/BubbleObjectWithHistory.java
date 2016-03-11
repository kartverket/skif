package no.statkart.skif.store;

import java.sql.Timestamp;

/**
 * Interface for bobler med historikk.
 *
 * @author Tor Egil R. Strnad
 * @since 2.4.0
 */
public interface BubbleObjectWithHistory extends BubbleObject {

    Timestamp getOppdateringsdato();
    void setOppdateringsdato(Timestamp oppdateringsdato);

    Timestamp getSluttdato();
    void setSluttdato(Timestamp sluttdato);

}
