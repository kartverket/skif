package no.statkart.skif.storetest2.domain.subtype;

/**
 * Subtype med et primitivfelt.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypeWithPrimitive extends SubTypedBubble {
    private int num;

    @Override
    public SubTypeWithPrimitiveId<?> getId() {
        return (SubTypeWithPrimitiveId<?>) super.getId();
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
