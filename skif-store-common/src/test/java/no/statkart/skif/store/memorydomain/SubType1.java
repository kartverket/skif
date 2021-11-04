package no.statkart.skif.store.memorydomain;

public class SubType1 extends BaseType {
    private static final long serialVersionUID = 1;

    public SubType1(SubType1Id id) {
        setId(id);
    }

    @Override
    public SubType1Id getId() {
        return (SubType1Id) super.getId();
    }
}
