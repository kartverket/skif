package no.statkart.skif.store;

import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Angir en eksplisitt modellert invers relasjon som anvender {@link StoreRelationCache} for caching av verdien slik at
 * den ikke hentes flere ganger når cachen er enablet. I tillegg har klassen støtte for at serveren kan gi ut
 * relasjonen når det sendes til klient. For å få dette til må {@link #setRequested()}-metoden kalles
 * for å markere at relasjonen skal materialiseres i boblen som returneres til klient. Videre må
 * {@code StoreServer.materialiseRequestedRelations(bubbles)}-metoden kalles for bobler som returneres til klient.
 * Kall til {@link #setRequested()}} fører i seg selv ikke til at relasjonen lastes. Det sker først når
 * relasjonen brukes eller ved kall til {@code StoreServer.materialiseRequestedRelations(bubbles)}. Hvis
 * {@code StoreServer.materialiseRequestedRelations(bubbles) ikke har blitt kallt for et objekt som returneres til
 * klienten så vil relasjonen ikke bli med. Dette er uavhengig av om relasjonen er lastet eller ikke. På klienten
 * har kall til {@link #setRequested()}-metoden ingen effekt.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class InverseRelation<T> implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private InverseRelationState state = InverseRelationState.UNREQUESTED;
    private final AbstractBubbleObject owner;
    private final RelationName name;
    private T cached;

    public static <T> InverseRelation<T> create(AbstractBubbleObject owner, RelationName name) {
        return new InverseRelation<T>(owner, name);
    }

    @SuppressWarnings("UnusedDeclaration")// WS-Mapping
    private InverseRelation() {
        this.owner = null;
        this.name = null;
    }

    /**
     * Denne er protected slik at opprettelse må skje gjennom kall til {@link #create}
     */
    protected InverseRelation(AbstractBubbleObject owner, RelationName name) {
        this.owner = owner;
        this.name = name;
    }

    public RelationName getName() {
        return name;
    }

    @SuppressWarnings("UnusedDeclaration") // WS-Mapping
    public boolean isMaterialised() {
        return state.isMaterialised();
    }

    @SuppressWarnings("UnusedDeclaration") // WS-Mapping
    public void setMaterialised(boolean materialised) {
        this.state = (materialised) ? InverseRelationState.MATERIALISED : InverseRelationState.UNREQUESTED;
    }

    public boolean isRequested() {
        return state.isRequested();
    }

    public void setRequested() {
        if (!isMaterialised()) {
            this.state = InverseRelationState.REQUESTED;
        }
    }

    public void clearRequested() {
        this.state = InverseRelationState.UNREQUESTED;
        setCached(null);
    }


    @SuppressWarnings("UnusedDeclaration") // WS-Mapping
    public T getCached() {
        return cached;
    }

    @SuppressWarnings("UnusedDeclaration") // WS-Mapping
    public void setCached(T value) {
        this.cached = value;
    }

    @Deprecated
    public InverseRelationState getState() {
        return state;
    }

    public T get() {
        if (owner.store() == null) {
            checkState(isMaterialised(), "Bubble not registered in Store and cached relation value was not materialised");
            return cached;
        } else {
            StoreRelationCache relationCache = checkNotNull(owner.store().getInstance(StoreRelationCache.class));
            return (T) owner.unwrap(relationCache.getRelationFinder(name, owner.store).call(owner.idAsSet()));
        }
    }

    // TODO: package scope
    public void setCachedIfRequested() {
        if (isRequested()) {
            setCached(get());
            setMaterialised(true);
        }
    }

    public void setFrom(InverseRelation<T> from) {
        this.setMaterialised(from.isMaterialised());
        this.setCached(from.getCached());
    }
}
