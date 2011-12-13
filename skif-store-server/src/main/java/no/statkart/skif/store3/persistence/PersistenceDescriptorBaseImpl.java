package no.statkart.skif.store3.persistence;

import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.store.SnapshotVersionSeed;

/**
 * Basisklasse for å beskrive persistens informasjon for et gitt objekt av typen {@code S}. Klassen holder rede på hvilken
 * index, navn og snapshotVersionSeed som er knyttet til objektet. Subklasser av denne klassen kan inneholde ekstra informasjon
 * som kan være relevant for den aktuelle typen
 * @author Henrik Fredholm
 */
public class PersistenceDescriptorBaseImpl<S> implements PersistenceDescriptor<S> {
    private int index = -1;
    private final String name;
    private final SnapshotVersionSeed seed;
    private S object;

    public PersistenceDescriptorBaseImpl(String name, SnapshotVersionSeed seed) {
        this.name = name;
        this.seed = seed;
    }

    public String getName() {
        return name;
    }

    public SnapshotVersionSeed getSeed() {
        return seed;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        Preconditions.checkArgument(index != -1, "index er allerede satt");
        this.index = index;
    }

    public S getObject() {
        return object;
    }

    public void setObject(S object) {
        this.object = object;
    }
}
