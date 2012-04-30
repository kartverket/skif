package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

public class Kommune extends AbstractStoreTestBubble {
    private String kommunenummer;
    private String navn;

    @Override
    public KommuneId<?> getId() {
        return (KommuneId<?>) super.getId();
    }

    public String getKommunenummer() {
        return kommunenummer;
    }

    public void setKommunenummer(String kommunenummer) {
        this.kommunenummer = kommunenummer;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}
