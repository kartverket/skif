package no.statkart.skif.skiftest.wsapi.mapping;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class SkifTestMapper3 extends SkifTestMapper {
    public SkifTestMapper3() {
        super(SkifTestMapping3.class);
    }

    @Override
    public SkifTestMapping3 getMapping() {
        return (SkifTestMapping3) super.getMapping();

    }
}