package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.sql.Timestamp;
import java.util.List;

/**
 * Tester aggergering av objekter.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class AggregertObjekt extends AbstractBubbleObject implements StoreTestBubble {
    private String sistOppdatertAv;
    private Timestamp sistOppdatert;

    private String tekst;
    private List<AggregertKomponent> komponenter;

    @Override
    public AggregertObjektId<?> getId() {
        return (AggregertObjektId<?>) super.getId();
    }

    public String getSistOppdatertAv() {
        return sistOppdatertAv;
    }

    public void setSistOppdatertAv(String sistOppdatertAv) {
        this.sistOppdatertAv = sistOppdatertAv;
    }

    public Timestamp getSistOppdatert() {
        return sistOppdatert;
    }

    public void setSistOppdatert(Timestamp sistOppdatert) {
        this.sistOppdatert = sistOppdatert;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public List<AggregertKomponent> getKomponenter() {
        return komponenter;
    }

    public void setKomponenter(List<AggregertKomponent> komponenter) {
        this.komponenter = komponenter;
    }
}
