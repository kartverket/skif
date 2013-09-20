package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.store.BubbleObjectWithHistory;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;

import java.sql.Timestamp;

/**
 * Baseklasse for historisk databasekoder som skal ligge i samme tabell.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public abstract class HistoriskDbKode extends StoreTestDbKode implements BubbleObjectWithHistory {
    private static final long serialVersionUID = 1L;

    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;

    @Override
    public HistoriskDbKodeId<?> getId() {
        return (HistoriskDbKodeId<?>) super.getId();
    }

    @Override
    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    @Override
    public void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    @Override
    public Timestamp getSluttdato() {
        return sluttdato;
    }

    @Override
    public void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }
}
