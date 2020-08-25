package no.statkart.skif.mockup;

import no.statkart.skif.store.AbstractBubbleObject;

class Adresse extends AbstractBubbleObject {
    KretsId kretsId;

    public Adresse(AdresseId adresseId, KretsId kretsId) {
        setId(adresseId);
        this.kretsId = kretsId;
    }
}
