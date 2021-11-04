package no.statkart.skif.store.memorydomain;

public class SubType2 extends BaseType {
    private static final long serialVersionUID = 1;

    public SubType2(SubType2Id id) {
        setId(id);
    }

    @Override
    public SubType2Id getId() {
        return (SubType2Id) super.getId();
    }
}
