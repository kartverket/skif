package no.statkart.skif.storetest.domain.mockup;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.demo.FooEntityComponent;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Foo extends AbstractStoreTestBubble {

    private long nr;
    private String navn;
    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;
    private FooEntityComponent fooEntityComponent;
    public boolean sameVersion(Foo o) {
        if (!this.getId().getValue().equals(o.getId().getValue())) return false;
        if (!this.getOppdateringsdato().equals(o.getOppdateringsdato())) return false;
        if (!this.getSluttdato().equals(o.getSluttdato())) return false;
        return true;
    }

    @Override
    public FooId<?> getId() {
        return (FooId<?>) super.getId();
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public long getNr() {
        return nr;
    }

    public void setNr(long nr) {
        this.nr = nr;
    }

    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    public void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    public Timestamp getSluttdato() {
        return sluttdato;
    }

    public void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }

    public FooEntityComponent getFooEntityComponent() {
        return fooEntityComponent;
    }

    public void setFooEntityComponent(FooEntityComponent fooEntityComponent) {
        this.fooEntityComponent = fooEntityComponent;
    }
}
