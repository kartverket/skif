package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * 
 */
public class AndelIMatrikkelenhet extends AbstractStoreTestBubble {
    private int teller;
    private int nevner;
    private boolean aktiv = true;
    private NivaaIMatrikkelenhetId<?> nivaaIMatrikkelenhetId;
    private PersonId<?> andelseierPersonId;
    private MatrikkelenhetId<?> andelseierMatrikkelenhetId;

    public NivaaIMatrikkelenhetId<?> getNivaaIMatrikkelenhetId() {
        return nivaaIMatrikkelenhetId;
    }

    public void setNivaaIMatrikkelenhetId(NivaaIMatrikkelenhetId<?> nivaaIMatrikkelenhetId) {
        this.nivaaIMatrikkelenhetId = nivaaIMatrikkelenhetId;
    }

    public int getTeller() {
        return teller;
    }

    public void setTeller(int teller) {
        this.teller = teller;
    }

    public int getNevner() {
        return nevner;
    }

    public void setNevner(int nevner) {
        this.nevner = nevner;
    }

    public PersonId<?> getAndelseierPersonId() {
        return andelseierPersonId;
    }

    public void setAndelseierPersonId(PersonId<?> andelseierPersonId) {
        this.andelseierPersonId = andelseierPersonId;
    }

    public MatrikkelenhetId<?> getAndelseierMatrikkelenhetId() {
        return andelseierMatrikkelenhetId;
    }

    public void setAndelseierMatrikkelenhetId(MatrikkelenhetId<?> andelseierMatrikkelenhetId) {
        this.andelseierMatrikkelenhetId = andelseierMatrikkelenhetId;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }
}
