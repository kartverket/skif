package no.statkart.skif.domain;

import com.google.common.collect.Multimap;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EqualsByFields {
    private final List<HandlerEntry> handlers = new ArrayList<>();

    // For å unngå rekursivitet
    private final Set<Pair> processed = new HashSet<>();

    public EqualsByFields() {
        addHandler(List.class, new ListHandler());
        addHandler(Set.class, new SetHandler());
        addHandler(Map.class, new MapHandler());
        addHandler(Map.Entry.class, new MapEntryHandler());
        addHandler(Multimap.class, new MultimapHandler());
    }

    public <O> void addHandler(Class<? super O> clazz, EqualityHandler<O> handler) {
        handlers.add(new HandlerEntry(clazz, handler));
    }

    public boolean isEqualByFields(@Nullable Object o1, @Nullable Object o2) {
        Pair pair = new Pair(o1, o2);
        if (!processed.add(pair)) {
            // Det kan hende koden som la paret inn ikke har kjørt sammenligningen enda, men den kommer til å gjøre det,
            // med mindre noe annet feiler først.
            return true;
        }

        try {
            if (o1 instanceof EqualityByFields) {
                EqualityByFields ebf1 = (EqualityByFields) o1;
                return ebf1.equalsByFields(o2, this);
            }

            for (HandlerEntry handlerEntry : handlers) {
                if (handlerEntry.getClazz().isInstance(o1)) {
                    EqualityHandler handler = handlerEntry.getHandler();
                    return useHandler(handler, o1, o2);
                }
            }

            return Objects.equals(o1, o2);
        } finally {
            // Må fjerne denne igjen slik at vi ikke returnerer true senere selv om det vi fant ut var false
            processed.remove(pair);
        }
    }

    /**
     * Trukket ut kun for å begrense hva som blir dekket av suppress-annotasjonen.
     */
    @SuppressWarnings("unchecked")
    private boolean useHandler(EqualityHandler handler, Object o1, Object o2) {
        return handler.checkEquals(o1, o2, this);
    }

    private static class HandlerEntry {
        private final Class clazz;
        private final EqualityHandler handler;

        private HandlerEntry(Class clazz, EqualityHandler handler) {
            this.clazz = clazz;
            this.handler = handler;
        }

        public Class getClazz() {
            return clazz;
        }

        public EqualityHandler getHandler() {
            return handler;
        }
    }

    /**
     * Klasse som representerer et par med objekter. Likhet er på identiten til de to objektene.
     */
    private static class Pair {
        @Nullable private final Object o1;
        @Nullable private final Object o2;

        private Pair(@Nullable Object o1, @Nullable Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            // Det er med vilje at denne sjekker på identitet
            return o1 == pair.o1 && o2 == pair.o2;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(o1) + System.identityHashCode(o2);
        }
    }
}
