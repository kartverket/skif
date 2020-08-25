package no.statkart.skif.mockup;

import no.statkart.skif.store.AbstractBubbleId;

class AdresseId extends AbstractBubbleId<Adresse> {
    public AdresseId(Object value) {
        super(value);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
