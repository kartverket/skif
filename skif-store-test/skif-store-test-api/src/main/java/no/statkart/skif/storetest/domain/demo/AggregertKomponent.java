package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.BubbleComponent;
import no.statkart.skif.store.BubbleObject;
import java.io.Serializable;

/**
 * Komponent for {@link AggregertObjekt}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class AggregertKomponent implements BubbleComponent<AggregertObjekt>, Serializable {
    private String noe;
    private int annet;
    private AggregertObjekt aggregertObjekt;

    public String getNoe() {
        return noe;
    }

    public void setNoe(String noe) {
        this.noe = noe;
    }

    public int getAnnet() {
        return annet;
    }

    public void setAnnet(int annet) {
        this.annet = annet;
    }

    public void setAggregertObjekt(AggregertObjekt aggregertObjekt) {
        this.aggregertObjekt = aggregertObjekt;
    }

    public AggregertObjekt getAggregertObjekt() {
        return aggregertObjekt;
    }

    @Override
    public void setBubbleObject(AggregertObjekt aggregertObjekt) {
        this.aggregertObjekt = aggregertObjekt;

    }
}
