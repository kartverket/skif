package no.statkart.skif.domain;

import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.*;

public class EqualsByFields {
    private final List<HandlerEntry> handlers = new ArrayList<>();

    public EqualsByFields() {
        addHandler(List.class, new ListHandler());
        addHandler(Set.class, new SetHandler());
        addHandler(Map.class, new MapHandler());
        addHandler(Map.Entry.class, new MapEntryHandler());
        addHandler(Multimap.class, new MultimapHandler());
    }

    public <O> void addHandler(Class<O> clazz, EqualityHandler<? super O> handler) {
        handlers.add(new HandlerEntry(clazz, handler));
    }

    public boolean isEqualByFields(@Nullable Object o1, @Nullable Object o2) {
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
}
