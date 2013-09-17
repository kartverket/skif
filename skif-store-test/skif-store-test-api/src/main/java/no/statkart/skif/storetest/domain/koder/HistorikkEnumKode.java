package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.store.BubbleObjectWithHistory;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKode;

import java.sql.Timestamp;

/**
 * En enkel kode med historikk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class HistorikkEnumKode extends StoreTestEnumKode implements BubbleObjectWithHistory {
    private static final long serialVersionUID = 1L;

    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;

    @Override
    public HistorikkEnumKodeId getId() {
        return (HistorikkEnumKodeId) super.getId();
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
