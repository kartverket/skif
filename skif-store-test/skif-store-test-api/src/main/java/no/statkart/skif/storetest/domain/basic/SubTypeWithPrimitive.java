package no.statkart.skif.storetest.domain.basic;

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
        SubTypedBubbleId<?> id = (SubTypedBubbleId<?>) super.getId();
        if (id == null) {
            return null;
        }
        if (id instanceof SubTypeWithPrimitiveId) {
            return (SubTypeWithPrimitiveId<?>) id;
        }
        return new SubTypeWithPrimitiveId<>(id.getValue(), id.getSnapshotVersion());
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
