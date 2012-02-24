package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.BubbleComponent;
import no.statkart.skif.store.BubbleObject;

/**
 * Komponent for {@link AggregertObjekt}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class AggregertKomponent implements BubbleComponent{
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

    @Override
    public void setBubbleObject(BubbleObject bubbleObject) {
        setAggregertObjekt((AggregertObjekt) bubbleObject);
    }

    private void setAggregertObjekt(AggregertObjekt aggregertObjekt) {
        this.aggregertObjekt = aggregertObjekt;
    }

    public AggregertObjekt getAggregertObjekt() {
        return aggregertObjekt;
    }
}
