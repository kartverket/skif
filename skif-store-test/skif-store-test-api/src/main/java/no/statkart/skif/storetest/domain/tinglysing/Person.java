package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class Person extends AbstractStoreTestBubble {

    private String ident;
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

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}
