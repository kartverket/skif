package no.statkart.skif.store;

import java.sql.Timestamp;

/**
 * Interface for bobler med historikk.
 *
 * @author Tor Egil R. Strnad
 * @since 2.4.0
 */
public interface BubbleObjectWithHistory extends BubbleObject {
    public Timestamp getOppdateringsdato();
    public void setOppdateringsdato(Timestamp oppdateringsdato);

    public Timestamp getSluttdato();
    public void setSluttdato(Timestamp sluttdato);
}
