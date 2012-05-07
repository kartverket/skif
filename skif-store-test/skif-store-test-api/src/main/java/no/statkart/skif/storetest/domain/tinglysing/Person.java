package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @author rorchr
 */
public class Person extends AbstractStoreTestBubble {

    private String ident;
    private Personidenttype identtype;
    private String navn;

    @Override
    public PersonId<?> getId() {
        return (PersonId<?>) super.getId();
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public Personidenttype getIdenttype() {
        return identtype;
    }

    public void setIdenttype(Personidenttype identtype) {
        this.identtype = identtype;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}
