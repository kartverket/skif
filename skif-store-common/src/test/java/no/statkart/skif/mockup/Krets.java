package no.statkart.skif.mockup;

import no.statkart.skif.store.AbstractBubbleObject;

import javax.annotation.Nullable;

class Krets extends AbstractBubbleObject {
    @Nullable
    FlateId flateId;

    String navn;

    public Krets(KretsId kretsId, FlateId flateId) {
        setId(kretsId);
        this.flateId = flateId;
    }
}
