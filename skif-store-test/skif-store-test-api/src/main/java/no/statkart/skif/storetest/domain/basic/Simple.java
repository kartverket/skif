package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.demo.FooEntityComponent;
import no.statkart.skif.storetest.domain.mockup.FooId;

import java.sql.Timestamp;

/**
 * Boble uten historikk og som ikke har egne relasjoner til andre objekter
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class Simple extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private long nr;
    private String text;

    public Simple() {
    }

    public Simple(SimpleId<?> id) {
        super(id);
    }

    public Simple(SimpleId id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public SimpleId<?> getId() {
        return (SimpleId<?>) super.getId();
    }

    public long getNr() {
        return nr;
    }

    public void setNr(long nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
