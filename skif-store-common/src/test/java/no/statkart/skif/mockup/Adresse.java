package no.statkart.skif.mockup;

import no.statkart.skif.store.AbstractBubbleObject;

import java.util.ArrayList;
import java.util.List;

class Adresse extends AbstractBubbleObject {
    KretsId kretsId;

    List<String> merknader = new ArrayList<>();

    public Adresse(AdresseId adresseId, KretsId kretsId) {
        setId(adresseId);
        this.kretsId = kretsId;
    }
}
