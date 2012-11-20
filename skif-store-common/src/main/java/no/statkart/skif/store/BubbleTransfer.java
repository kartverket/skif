package no.statkart.skif.store;

import java.io.Serializable;
import java.util.*;

/**
 * Et data transfer objekt for å overføre et sett med {@code BubbleObject} objekter fra
 * server til klient som en samlet enhet.
 * <p/>
 * {@code BubbleTransfer} klassen er abstrakt og må subklasses for hver brukstilfelle.
 * Subklasser må definere en konstruktør som er ansvarlig for å legge inn alle objekter
 * som behøves i overførslen. Dette gjøres ved i konstruktøren å kalle
 * {@link #add(BubbleObject)} and {@link #add(Collection) add(BubbleObjects)} metodene.<p>
 * <p/>
 * Subklasser av <code>BubbleTransfer</code> bør definere tilgangsmetoder for å hent ut transfer objektets
 * hoved BubbleId'er. BubbleTransfer subklasser bør aldrig tilbyde tilgangsmetoder for å hente ut
 * {@code BubbleObject} objekter direkte. I stedet bør BubbleTransfer objektet registreres i {@code Store} via
 * {@link Store#register} metoden. Dette er for ikke å omgå {@code Store} sin håndtering av låste objekter,
 * slik at man ender opp med å jobbe med riktig {@code BubbleObject} dersom det finnes en nyere kopi i {@code Store}.
 * <p/>
 * <strong>Eksempel 1</strong>
 * <pre>
 * // Medtode som returnerer mange objekter av forskjellig type fra server
 * MyBubbleTransfer transfer = someServerMethod(...);
 * // Registrer alle objekter in i Store. Objekter som allerede finnes i Store og som er i ferd med å bli endret vil bli kastet
 * Store.register(transfer);
 * // Access the objects via Store:
 * MyObject myObject = store.get(transfer.getMyId());
 * </pre><p>
 *
 * <strong>Eksempel 2</strong>
 *{@code BubbleTransfer} objektet har blitt designet for å kunne håndtere tilfelle hvor {@code Store} allerede inneholder
 * noen av objektene som mottas i transfer objekter. Objekter i {@code Store} som er låst erstattes ikke.
 * <pre>
 * MyObject obj1 = store.lock(id1);
 * obj1.setName(...); *
 * MyBubbleTransfer transfer = someServerMethod(...);
 * Store.register(transfer);
 * // Hent objektet via store
 * MyObject myObject = store.get(id1);
 * assert(obj1==myObject)
 * </pre><p>
 *
 * @author Aksel Hilde
 * @author Henrik Fredholm
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class BubbleTransfer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map objects = new LinkedHashMap(256);
    private Set lockedIds = new HashSet();

    public BubbleTransfer() {
    }

    public BubbleTransfer(Map objects) {
        this.objects = objects;
    }

    public BubbleTransfer(Map objects, Set lockedIds) {
        this.lockedIds = lockedIds;
        this.objects = objects;
    }

    /**
     * Returns true if the transfer object is empty.
     *
     * @return true if the transfer is empty, otherwise false.
     */
    public final boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * Get all bubble objects in this transfer.
     *
     * @return a set of all <code>BubbleObject</code>s in this transfer
     */
    public final Map getObjects() {
        return Collections.unmodifiableMap(objects);
    }

    /**
     * Get the id of all locked objects in this transfer.
     *
     * @return a set og <code>BubbleId</code>s
     */
    public final Set getLockedIds() {
        return Collections.unmodifiableSet(lockedIds);
    }

    /**
     * Get an object from this transfer.
     *
     * @param id bubble id for the object
     * @return a bubble object
     */
    public final BubbleObject getObject(BubbleId id) {
        return (BubbleObject) objects.get(id);
    }

    /**
     * Legg til objekt som med sikkerhet ikke er blitt låst.
     */
    protected final void addUnlocked(BubbleObject bubbleObject) {
        objects.put(bubbleObject.getId(), bubbleObject);
    }

    /**
     * Legg til et objekt som kanskje er blitt låst. Om objektet er låst eller ikke sjekkes mot Spif.
     */
    protected final void add(BubbleObject bubbleObject) {
        objects.put(bubbleObject.getId(), bubbleObject);
        if (bubbleObject.store().isLocked(bubbleObject.getId())) {
            lockedIds.add(bubbleObject.getId());
        }
    }

    /**
     * Legg til en samling objekter som med sikkerhet ikke er blitt låst.
     */
    protected final void addUnlocked(Collection<BubbleObject> bubbleObjects) {
        for (BubbleObject bubbleObject : bubbleObjects) {
            objects.put(bubbleObject.getId(), bubbleObject);
        }
    }

    /**
     * Legg til et objekt som kanskje er blitt låst. Om objektet er låst eller ikke sjekkes mot Spif.
     */
    protected final void add(Collection bubbleObjects) {
        for (Iterator it = bubbleObjects.iterator(); it.hasNext(); ) {
            BubbleObject bubbleRoot = (BubbleObject) it.next();
            objects.put(bubbleRoot.getId(), bubbleRoot);
            Store store = bubbleRoot.store();
            if (store != null && store.isLocked(bubbleRoot.getId())) {
                lockedIds.add(bubbleRoot.getId());
            }
        }
    }
}
