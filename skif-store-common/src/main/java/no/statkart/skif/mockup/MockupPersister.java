package no.statkart.skif.mockup;

import com.google.common.collect.Lists;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;

import java.util.*;

/**
 * Inneholder snapshots. Alle id-er skal være current.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
public class MockupPersister {
    private final Store store;
    private final TestNumber testNumber;

    SortedMap<SnapshotVersion, Map<BubbleId<?>, BubbleObject>> snapshots = new TreeMap<SnapshotVersion, Map<BubbleId<?>, BubbleObject>>();
    Map<BubbleId, SnapshotVersion> insertedAtSnapshot = new LinkedHashMap<BubbleId, SnapshotVersion>();
    Map<BubbleId, SnapshotVersion> lastSnapshotForBubble = new LinkedHashMap<BubbleId, SnapshotVersion>();
    Map<BubbleId, SnapshotVersion> deletedAtSnapshot = new LinkedHashMap<BubbleId, SnapshotVersion>();

    public MockupPersister(Store store, TestNumber testNumber) {
        this.store = store;
        this.testNumber = testNumber;
    }

    /**
     * Henter ut et snapshot. Det opprettes hvis det ikke allerede finnes. Det brukes en
     * LinkedHashMap for å bevare operasjons rekkefølgen.
     *
     * @param snapshotVersion tidspunkt for snapshot
     * @return snapshot
     */
    private Map<BubbleId<?>, BubbleObject> getSnapshot(SnapshotVersion snapshotVersion) {
        Map<BubbleId<?>, BubbleObject> snapshot = snapshots.get(snapshotVersion);
        if (snapshot == null) {
            snapshot = new LinkedHashMap<BubbleId<?>, BubbleObject>();
            snapshots.put(snapshotVersion, snapshot);
        }
        return snapshot;
    }

    /**
     * Sjekker at id-en er current slik den må være.
     *
     * @param bubbleId id som skal sjekkes
     */
    private static void checkIdIsCurrent(BubbleId bubbleId) {
        if (!SnapshotVersion.CURRENT.equals(bubbleId.getSnapshotVersion())) {
            throw new ImplementationException("SnapshotVersion not current");
        }
    }

    /**
     * Putter inn et objekt på tidspunkt gitt av <code>snapshotVersion</code>.
     * <p/>
     * Følgende krav gjelder:
     * <ul>
     * <li>Objektet kan ikke allerede ha blitt inserted på noe tidspunkt</li>
     * </ul>
     *
     * @param bubbleObject    objektet som skal settes inn
     * @param snapshotVersion tidspunktet objektet skal anses som opprettet på
     */
    public void insert(BubbleObject bubbleObject, SnapshotVersion snapshotVersion) {
        checkIdIsCurrent(bubbleObject.getId());
        if (lastSnapshotForBubble.containsKey(bubbleObject.getId())) {
            throw new ImplementationException("Object already mocked up: " + bubbleObject.getId());
        }

        Map<BubbleId<?>, BubbleObject> snapshot = getSnapshot(snapshotVersion);
        snapshot.put(bubbleObject.getId(), bubbleObject);
        insertedAtSnapshot.put(bubbleObject.getId(), snapshotVersion);
        lastSnapshotForBubble.put(bubbleObject.getId(), snapshotVersion);
        bubbleObject.register(store);
    }

    /**
     * Oppdaterer et objekt på tidspunkt gitt av <code>snapshotVersion</code>.
     * <p/>
     * Følgende krav gjelder:
     * <ul>
     * <li>Objektet må ha blitt inserted på et tidligere eller likt tidspunkt</li>
     * <li>Objektet kan ikke ha blitt updated på et senere tidspunkt</li>
     * <li>Objektet kan ikke ha blitt deleted på et tidligere eller likt tidspunkt</li>
     * </ul>
     *
     * @param bubbleObject    objektet som skal oppdateres
     * @param snapshotVersion tidspunktet objektet skal anses som oppdatert på
     */
    public void update(BubbleObject bubbleObject, SnapshotVersion snapshotVersion) {
        checkIdIsCurrent(bubbleObject.getId());
        SnapshotVersion lastSnapshot = lastSnapshotForBubble.get(bubbleObject.getId());
        if (lastSnapshot == null) {
            throw new ImplementationException("Object not mocked up: " + bubbleObject.getId());
        }
        if (lastSnapshot.compareTo(snapshotVersion) > 0) {
            throw new ImplementationException("Object has newer mock-up: " + bubbleObject.getId() + " " + snapshotVersion.getTimestampString());
        }
        SnapshotVersion deletionSnapshot = deletedAtSnapshot.get(bubbleObject.getId());
        if (deletionSnapshot != null && deletionSnapshot.compareTo(snapshotVersion) <= 0) {
            throw new ImplementationException("Object has been deleted: " + bubbleObject.getId() + " " + deletionSnapshot.getTimestampString());
        }

        Map<BubbleId<?>, BubbleObject> snapshot = getSnapshot(snapshotVersion);
        BubbleObject oldBubble = snapshot.put(bubbleObject.getId(), bubbleObject);
        if (oldBubble != bubbleObject) {
            bubbleObject.register(store);
        }
        lastSnapshotForBubble.put(bubbleObject.getId(), snapshotVersion);
    }

    /**
     * Sletter et objekt på tidspunkt gitt av <code>snapshotVersion</code>.
     * <p/>
     * Følgende krav gjelder:
     * <ul>
     * <li>Objektet må ha blitt inserted på et tidligere eller likt tidspunkt</li>
     * <li>Objektet kan ikke ha blitt updated på et senere tidspunkt</li>
     * <li>Objektet kan ikke ha blitt deleted på et tidligere eller likt tidspunkt</li>
     * </ul>
     *
     * @param bubbleObject    objektet som skal slettes
     * @param snapshotVersion tidspunktet objektet skal anses som slettet på
     */
    public void delete(BubbleObject bubbleObject, SnapshotVersion snapshotVersion) {
        checkIdIsCurrent(bubbleObject.getId());
        SnapshotVersion lastSnapshot = lastSnapshotForBubble.get(bubbleObject.getId());
        if (lastSnapshot == null) {
            throw new ImplementationException("Object not mocked up: " + bubbleObject.getId());
        }
        if (lastSnapshot.compareTo(snapshotVersion) > 0) {
            throw new ImplementationException("Object has newer mock-up: " + bubbleObject.getId() + " " + snapshotVersion.getTimestampString());
        }
        SnapshotVersion deletionSnapshot = deletedAtSnapshot.get(bubbleObject.getId());
        if (deletionSnapshot != null && deletionSnapshot.compareTo(snapshotVersion) <= 0) {
            throw new ImplementationException("Object has been deleted: " + bubbleObject.getId() + " " + deletionSnapshot.getTimestampString());
        }

        Map<BubbleId<?>, BubbleObject> snapshot = getSnapshot(snapshotVersion);
        BubbleObject oldBubble = snapshot.put(bubbleObject.getId(), bubbleObject);
        if (oldBubble != bubbleObject) {
            bubbleObject.register(store);
        }
        lastSnapshotForBubble.put(bubbleObject.getId(), snapshotVersion);
        deletedAtSnapshot.put(bubbleObject.getId(), snapshotVersion);
    }

    /**
     * Henter ut objektet slik det ville ha sett ut på gitt tidspunkt. OBS! Objektet returneres med current id-er.
     *
     * @param bubbleId        id til objektet som skal slås opp
     * @param snapshotVersion tidspunktet søket skal starte på og gå bakover i tid
     * @return objektet
     */
    public BubbleObject get(BubbleId bubbleId, SnapshotVersion snapshotVersion) {
        checkIdIsCurrent(bubbleId);
        SnapshotVersion lastSnapshot = lastSnapshotForBubble.get(bubbleId);
        if (lastSnapshot == null) {
            throw new ImplementationException("Object not mocked up: " + bubbleId);
        }
        SnapshotVersion deletionSnapshot = deletedAtSnapshot.get(bubbleId);
        if (deletionSnapshot != null && deletionSnapshot.compareTo(snapshotVersion) <= 0) {
            throw new ImplementationException("Object has been deleted: " + bubbleId + " " + deletionSnapshot.getTimestampString());
        }

        if (snapshots.containsKey(snapshotVersion)) {
            // Prøv først gjeldende snapshotversion
            Map<BubbleId<?>, BubbleObject> snapshot = snapshots.get(snapshotVersion);
            BubbleObject bubbleObject = snapshot.get(bubbleId);
            if (bubbleObject != null) {
                return bubbleObject;
            }
        }
        for (SortedMap<SnapshotVersion, Map<BubbleId<?>, BubbleObject>> submap = snapshots.headMap(snapshotVersion); !submap.isEmpty(); submap = submap.headMap(submap.lastKey())) {
            Map<BubbleId<?>, BubbleObject> snapshot = submap.get(submap.lastKey());
            BubbleObject bubbleObject = snapshot.get(bubbleId);
            if (bubbleObject != null) {
                return bubbleObject;
            }
        }

        throw new ImplementationException("Object not found: " + bubbleId + " " + snapshotVersion.getTimestampString());
    }

    /**
     * Henter ut inserts, updates og deletes for gitt SnapshotVersion. Gitt snapshot må ha blitt laget.
     * <p/>
     * Objektene i transfer er kopier, slik at de ikke lenger er knyttet opp til denne Store og dermed kan puttes inn
     * i en ordentlig Store.
     * <p/>
     * Pga effektivitet bør objektene ikke kopieres. Dersom de skal legges direkte inn i store kan transfer lage en kopi
     * om nødvendig
     *
     * @param snapshotVersion tidspunkt som skal hentes ut
     * @return transfer med inserts, updates og deletes
     */
    public MockupTransfer getTransfer(SnapshotVersion snapshotVersion) {
        Map<BubbleId<?>, BubbleObject> snapshot = snapshots.get(snapshotVersion);
        if (snapshot == null) {
            throw new ImplementationException("Snapshot does not exists: " + snapshotVersion.getTimestampString());
        }

        List<BubbleObject> insertedObjects = Lists.newArrayList();
        List<BubbleObject> updatedObjects = Lists.newArrayList();
        List<BubbleObject> deletedObjects = Lists.newArrayList();
        for (BubbleObject bubbleObject : snapshot.values()) {
            if (snapshotVersion.equals(insertedAtSnapshot.get(bubbleObject.getId()))) {
                insertedObjects.add(bubbleObject);
            } else if (snapshotVersion.equals(deletedAtSnapshot.get(bubbleObject.getId()))) {
                deletedObjects.add(bubbleObject);
            } else {
                updatedObjects.add(bubbleObject);
            }
        }
        return new MockupTransfer(insertedObjects, updatedObjects, deletedObjects, testNumber);
    }

    /**
     * Henter ut inserts, updates og deletes for gitte objekter i gitt SnapshotVersion. Gitt snapshot må ha blitt laget.
     * Dersom noen av objektene ikke har blitt endret i gitt snapshot, så blir de ikke med i transfer.
     * <p/>
     * Objektene i transferen vil forsatt være knyttet opp mot MockupStore og må derfor kopieres før de puttes inn i
     * ordentlig Store. Dette skjer normalt automatisk.
     * <p/>
     *
     * @param ids             id-er til objekter som skal hentes ut
     * @param snapshotVersion tidspunkt som skal hentes ut
     * @return transfer med inserts, updates og deletes
     */
    public MockupTransfer getTransferForIds(Set<? extends BubbleId> ids, SnapshotVersion snapshotVersion) {
        Map<BubbleId<?>, BubbleObject> snapshot = snapshots.get(snapshotVersion);
        if (snapshot == null) {
            throw new ImplementationException("Snapshot does not exists: " + snapshotVersion.getTimestampString());
        }

        List<BubbleObject> insertedObjects = Lists.newArrayList();
        List<BubbleObject> updatedObjects = Lists.newArrayList();
        List<BubbleObject> deletedObjects = Lists.newArrayList();
        for (BubbleId id : ids) {
            BubbleObject bubbleObject = snapshot.get(id);
            if (bubbleObject != null) {
                if (snapshotVersion.equals(insertedAtSnapshot.get(bubbleObject.getId()))) {
                    insertedObjects.add(bubbleObject);
                } else if (snapshotVersion.equals(deletedAtSnapshot.get(bubbleObject.getId()))) {
                    deletedObjects.add(bubbleObject);
                } else {
                    updatedObjects.add(bubbleObject);
                }
            }
        }
        return new MockupTransfer(insertedObjects, updatedObjects, deletedObjects, testNumber);
    }

    /**
     * Henter ut transfers for alle definerte snapshots opp til og inkludert gitte SnapshotVersion.
     *
     * @param beforeSnapshotVersion øvre grense for snapshots
     * @return transfers i sortert rekkefølge, eldste først
     */
    public SortedMap<SnapshotVersion, MockupTransfer> getTransfersBefore(SnapshotVersion beforeSnapshotVersion) {
        TreeMap<SnapshotVersion, MockupTransfer> transfers = new TreeMap<SnapshotVersion, MockupTransfer>();

        for (SnapshotVersion snapshotVersion : snapshots.keySet()) {
            if (snapshotVersion.compareTo(beforeSnapshotVersion) <= 0) {
                transfers.put(snapshotVersion, getTransfer(snapshotVersion));
            }
        }

        return transfers;
    }
}
