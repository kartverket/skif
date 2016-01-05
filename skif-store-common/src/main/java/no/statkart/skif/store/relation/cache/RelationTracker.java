package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Lists;
import no.statkart.skif.store.BubbleId;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Object som holder styr på innholdet av relasjon. Når relasjonen ikke er materialisert holder {@code RelationTracker} styr
 * på endringene som har blitt gjort på relasjonen. Når relasjonen er materialisert utføres endringen på selve
 * relasjonen.
 *
 * <P>En relasjon kan kun hentes ut når den er materialisert og den kan kun materialiseres en gang. Bruksmønster for
 * uthenting av relasjon er å først sjekke om trackeren er materialisert og hvis den er det så hente ut relasjonen direkte
 * fra denne. Hvis trackeren ikke er materialisert så må relasjonen hentes på annen vis først. Deretter må trackeren
 * materialiseres med relasjonen slik at den kan applisere eventuelle endringer gjort i unit of work. Deretter kan
 * relasjonen hentes ut fra trackeren.
 *
 * <P>Trackeren kan håndtere både One og Many relasjoner. Når trackeren er materialiset så vil {@code holder} for One relasjoner
 * være en peker til en BubbleId eller null. For Many relasjoner vil {@code holder} peke på et Set.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationTracker {
    private boolean materialised;

    /**
     * Peker enten på List<Operation> hvis materialised er false og på relasjonsvalue hvis materialised er true. Relasjons
     * value kan enten være en enkelt verdi eller en Collection avhengig av relasjonens kardinalitet.
     */
    private Object holder;

    private static abstract class Operation {
        final protected Object value;

        protected Operation(Object value) {
            this.value = value;
        }

        protected abstract Object applyTo(Object relation);

        @Override
        public String toString() {
            return getClass().getName() +
                    "{" +
                    "value=" + value +
                    '}';
        }
    }

    private static class Added extends Operation {
        private Added(Object value) {
            super(value);
        }

        @Override
        protected Object applyTo(Object relation) {
            if (relation instanceof Collection) {
                ((Collection)relation).add(value);
            } else {
                relation=value;
            }
            return relation;
        }
    }

    private static class Removed extends Operation {
        private Removed(Object value) {
            super(value);
        }

        @Override
        protected Object applyTo(Object relation) {
            if (relation instanceof Collection) {
                ((Collection)relation).remove(value);
            } else {
                relation=null;
            }
            return relation;
        }
    }

    public RelationTracker() {
        this(false, Lists.newArrayListWithCapacity(4));
    }

    public RelationTracker(boolean materialised, Object relation) {
        this.materialised = materialised;
        this.holder = relation;
    }

    @SuppressWarnings("unchecked")
    public void add(Object object) {
        if (materialised) {
            if (holder instanceof Collection) {
                ((Collection) holder).add(object);
            } else {
                holder = object;
            }
        } else {
            ((List<Operation>) holder).add(new Added(object));
        }
    }

    @SuppressWarnings("unchecked")
    public void remove(Object object) {
        if (materialised) {
            if (holder instanceof Collection) {
                ((Collection) holder).remove(object);
            } else {
                holder = null;
            }
        } else {
            ((List<Operation>) holder).add(new Removed(object));
        }
    }

    public final boolean isMaterialised() {
        return materialised;
    }

    @SuppressWarnings("unchecked")
    private List<Operation> getValuesAsOperations() {
        checkState(!materialised, "Relation is already materialised");
        return (List<Operation>) holder;
    }

    public Object getRelation() {
        checkState(materialised, "Relation is not materialised");
        return holder;
    }

    @SuppressWarnings("unchecked")
    public Set getManyRelation() {
        Object relation = getRelation();
        checkState(relation instanceof Collection, "Relation is not a MANY relation");
        return (Set) relation;
    }

    @SuppressWarnings("unchecked")
    public Object getOneRelation() {
        Object relation = getRelation();
        checkState(!(relation instanceof Collection), "Relation is not a ONE relation");
        return relation;
    }

    public Object applyOperations(Object relation) {
        for (Operation operation : getValuesAsOperations()) {
            relation = operation.applyTo(relation);
        }
        return relation;
    }

    public void commitInto(RelationTracker underlyingTracker) {
        if (materialised) {
            underlyingTracker.materialised = materialised;
            underlyingTracker.holder = holder;
        } else if (underlyingTracker.materialised) {
            underlyingTracker.holder = applyOperations(underlyingTracker.holder);
        } else {
            underlyingTracker.getValuesAsOperations().addAll(getValuesAsOperations());
        }
    }

    public void materialise(Object relation) {
        this.holder = applyOperations(relation);
        materialised=true;
    }


    @Override
    public String toString() {
        return "RelationTracker{" +
                "materialised=" + materialised +
                ", holder=" + holder +
                '}';
    }
}
