package no.statkart.skif.mockup;

import no.statkart.skif.store.AbstractBubbleId;

class FlateId extends AbstractBubbleId<Flate> {
    public FlateId(Object value) {
        super(value);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
