package no.statkart.skif.storetest.domain.basic;

import com.google.common.collect.Sets;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import java.util.Set;

/**
 * Boble uten historikk med relasjon til boble Simple
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithValueObject extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private BeloepValueObject a;
    private BeloepValueObject b;
    private Set<BeloepValueObject> beloepSet = Sets.newHashSet();

    public BubbleWithValueObject() {
    }

    public BubbleWithValueObject(BubbleWithRelationId<?> id) {
        super(id);
    }

    @Override
    public BubbleWithValueObjectId<?> getId() {
        return (BubbleWithValueObjectId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BeloepValueObject getA() {
        return a;
    }

    public void setA(BeloepValueObject a) {
        this.a = a;
    }

    public BeloepValueObject getB() {
        return b;
    }

    public void setB(BeloepValueObject b) {
        this.b = b;
    }

    public Set<BeloepValueObject> getBeloepSet() {
        return beloepSet;
    }

    public void setBeloepSet(Set<BeloepValueObject> beloepSet) {
        this.beloepSet = beloepSet;
    }
}
