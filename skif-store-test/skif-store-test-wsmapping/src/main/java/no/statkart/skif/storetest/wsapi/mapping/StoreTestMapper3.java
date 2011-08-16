package no.statkart.skif.storetest.wsapi.mapping;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestMapper3 extends StoreTestMapper {
    public StoreTestMapper3() {
        super(StoreTestMapping3.class);
    }

    @Override
    public StoreTestMapping3 getMapping() {
        return (StoreTestMapping3) super.getMapping();

    }
}