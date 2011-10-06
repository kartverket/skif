package no.statkart.skif.storetest.domain;

import com.sun.org.apache.xpath.internal.operations.And;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Foo extends AbstractBubbleObject implements StoreTestBubble {

    private long nr;
    private String navn;
    private Timestamp beginLifespanVersion;
    private Timestamp endLifespanVersion;

    public boolean sameVersion(Foo o) {
        if (!this.getId().getValue().equals(o.getId().getValue())) return false;
        if (!this.getBeginLifespanVersion().equals(o.getBeginLifespanVersion())) return false;
        if (!this.getEndLifespanVersion().equals(o.getEndLifespanVersion())) return false;
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

    public Timestamp getBeginLifespanVersion() {
        return beginLifespanVersion;
    }

    public void setBeginLifespanVersion(Timestamp beginLifespanVersion) {
        this.beginLifespanVersion = beginLifespanVersion;
    }

    public Timestamp getEndLifespanVersion() {
        return endLifespanVersion;
    }

    public void setEndLifespanVersion(Timestamp endLifespanVersion) {
        this.endLifespanVersion = endLifespanVersion;
    }
}
