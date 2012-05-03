package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @since 2.1
 */
public class AndelIMatrikkelenhet extends AbstractStoreTestBubble {
    private NivaaIMatrikkelenhetId<?> nivaaIMatrikkelenhetId;
    private int teller;
    private int nevner;
    private PersonId<?> andelseierPersonId;
    private NivaaIMatrikkelenhetId<?> andelseierNivaaIMatrikkelenhetId;
    private String status;

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

    public NivaaIMatrikkelenhetId<?> getAndelseierNivaaIMatrikkelenhetId() {
        return andelseierNivaaIMatrikkelenhetId;
    }

    public void setAndelseierNivaaIMatrikkelenhetId(NivaaIMatrikkelenhetId<?> andelseierNivaaIMatrikkelenhetId) {
        this.andelseierNivaaIMatrikkelenhetId = andelseierNivaaIMatrikkelenhetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
