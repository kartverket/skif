package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Holder styr på innholdet av relasjon. Når relasjonen ikke er materialisert holder {@code RelationTracker} styr
 * på endringene som har blitt gjort på relasjonen. Når relasjonen er materialisert utføres endringen på selve
 * relasjonen.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationTracker {
    private boolean materialised;
    private Collection values;

    private static abstract class Operation {
        final protected Object value;

        protected Operation(Object value) {
            this.value = value;
        }

        protected abstract void applyTo(Set values);

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
        protected void applyTo(Set values) {
            values.add(value);
        }
    }

    private static class Removed extends Operation {
        private Removed(Object value) {
            super(value);
        }

        @Override
        protected void applyTo(Set values) {
            values.remove(value);
        }
    }

    public RelationTracker() {
        this(false, Lists.newArrayListWithCapacity(4));
    }

    public RelationTracker(boolean materialised, Collection values) {
        this.materialised = materialised;
        this.values = values;
    }

    public void add(Object object) {
        if (materialised) {
            values.add(object);
        } else {
            values.add(new Added(object));
        }
    }

    public void remove(Object object) {
        if (materialised) {
            values.remove(object);
        } else {
            values.add(new Removed(object));
        }
    }

    public final boolean isMaterialised() {
        return materialised;
    }

    private List<Operation> getValuesAsOperations() {
        checkState(!materialised);
        return (List<Operation>) values;
    }

    public Set getValuesAsIds() {
        checkState(materialised);
        return (Set) values;
    }

    public void materialiseInto(Set values) {
        for (Operation operation : getValuesAsOperations()) {
            operation.applyTo(values);
        }
    }

    public void materialiseInto(RelationTracker underlyingRelation) {
        if (materialised) {
            underlyingRelation.materialised = materialised;
            underlyingRelation.values = values;
        } else if (underlyingRelation.materialised) {
            materialiseInto(underlyingRelation.getValuesAsIds());
        } else {
            underlyingRelation.getValuesAsOperations().addAll(getValuesAsOperations());
        }
    }

    public void materialise(Set values) {
        materialiseInto(values);
        materialised=true;
        this.values =values;
    }
}
