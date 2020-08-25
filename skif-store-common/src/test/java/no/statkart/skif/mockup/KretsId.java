package no.statkart.skif.mockup;

import no.statkart.skif.store.AbstractBubbleId;

class KretsId extends AbstractBubbleId<Krets> {
    public KretsId(Object value) {
        super(value);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
