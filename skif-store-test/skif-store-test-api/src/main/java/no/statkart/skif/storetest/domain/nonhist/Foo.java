package no.statkart.skif.storetest.domain.nonhist;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Foo extends AbstractBubbleObject implements StoreTestBubble {

    private long nr;
    private String navn;

    protected long id;

    public Foo(long id) {
        this.id = id;
    }

    public Foo() {
    }

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

    @Override
    public String toString() {
        return "id:"+id+"; nr:"+nr+"; navn:"+navn;
    }
}
