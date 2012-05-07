package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @author rorchr
 */
public class Embete extends AbstractStoreTestBubble {

    private String embetenummer;

    @Override
    public EmbeteId<?> getId() {
        return (EmbeteId<?>) super.getId();
    }

    public String getEmbetenummer() {
        return embetenummer;
    }

    public void setEmbetenummer(String embetenummer) {
        this.embetenummer = embetenummer;
    }
}
