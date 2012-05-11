package no.statkart.skif.storetest.domain.multikobling.util;

import com.google.common.collect.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * En datastruktur som inneholder et sett av koblinger til objekter av type {@code V} sortert på roller av type {@code R},
 * hvor koblingen er en subtype {@code Kobling<R,V>} som opprettes av {@code koblingFactory}.
 *
 * Klasser, {@code E}, som definere felter av denne type kan se på som eier av koblingene inneholdt i datastrukturen. På
 * databasenivå mappes datastruktur til en link tabell med tre nøkler: id for {@code E}, rolle, og id for
 * {@code V}.
 *
 * I nåværende implementasjon inneholder datastrukturen både et {@code Set} objekt og et {@code SetMultimap}. Grunnen
 * til dette er at det pt ikke finnes noen hibernate implementasjon for persistering av {@code SetMultimap} direkte.
 *
 * TODO NB: Dersom hiberante kaller {@link #setKoblinger(java.util.Set) } ofte kan det oppstå performance problemer. Vi bør sjekke om dette skjer
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class HashKoblingMultimap<R, V> extends ForwardingSetMultimap<R, V> implements Serializable {
    private Set<Kobling<R, V>> koblinger = new HashSet<Kobling<R, V>>();
    private final SetMultimap<R, V> delegate = HashMultimap.create();
    private final KoblingFactory<R, V> koblingFactory;

    public HashKoblingMultimap(KoblingFactory<R, V> koblingFactory) {
        this.koblingFactory = koblingFactory;
    }

    public static <R, V> HashKoblingMultimap<R, V> create(KoblingFactory<R, V> koblingFactory) {
        return new HashKoblingMultimap<R, V>(koblingFactory);
    }

    @Override
    protected SetMultimap<R, V> delegate() {
        return delegate;
    }

    public Set<Kobling<R, V>> getKoblinger() {
        return koblinger;
    }

    public void setKoblinger(Set<Kobling<R, V>> koblinger) {
        this.koblinger = koblinger;
        delegate.clear();
        for (Kobling<R, V> k : koblinger) {
            delegate.put(k.rolle, k.getValue());
        }
    }

    @Override
    public void clear() {
        delegate.clear();
        koblinger.clear();
    }

    @Override
    public Set<V> get(@Nullable R key) {
        return new LazyKoblingSet(key);
    }

    @Override
    public Set<V> replaceValues(R key, Iterable<? extends V> values) {
        final Set<V> tids = delegate.replaceValues(key, values);
        for (V tid : tids) {
            koblinger.remove(koblingFactory.create((R)key, tid));
        }
        return tids;
    }

    @Override
    public Set<V> removeAll(@Nullable Object key) {
        final Set<V> tids = delegate.removeAll(key);
        for (V tid : tids) {
            koblinger.remove(koblingFactory.create((R)key, tid));
        }
        return tids;
    }

    @Override
    public boolean put(R key, V value) {
        final boolean changed = delegate.put(key, value);
        koblinger.add(koblingFactory.create(key,value));
        return changed;
    }

    @Override
    public boolean putAll(R key, Iterable<? extends V> values) {
        final boolean changed = delegate.putAll(key, values);
        if (changed) {
            for (V value : values) {
                koblinger.add(koblingFactory.create(key, value));
            }
        }
        return changed;
    }

    @Override
    public boolean putAll(Multimap<? extends R, ? extends V> multimap) {
        final boolean changed = delegate.putAll(multimap);
        if (changed) {
            for (Map.Entry<? extends R, ? extends V> entry : multimap.entries()) {
                koblinger.add(koblingFactory.create(entry.getKey(), entry.getValue()));
            }
        }
        return changed;
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        final boolean changed = delegate.remove(key, value);
        if (changed) {
            koblinger.remove(koblingFactory.create((R) key, (V) value));
        }
        return changed;
    }


    public class LazyKoblingSet extends ForwardingSet<V> implements Serializable {
        private final R rolle;
        private transient Set<V> delegate;

        public LazyKoblingSet(R rolle) {
            this.rolle = rolle;
        }

        @Override
        protected Set<V> delegate() {
            if (delegate == null) {
                delegate = HashKoblingMultimap.this.delegate.get(rolle);
            }
            return delegate;
        }


        @Override
        public boolean add(V element) {
            return HashKoblingMultimap.this.put(rolle,element);
        }

        @Override
        public boolean addAll(Collection<? extends V> tids) {
            return standardAddAll(tids);
        }

        @Override
        public void clear() {
            HashKoblingMultimap.this.clear();
        }

        @Override
        public boolean remove(Object element) {
            return HashKoblingMultimap.this.remove(rolle, element);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return standardRemoveAll(collection); // todo check at remove blir kallt
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return standardRetainAll(collection); // todo check at denne virker
        }

        @Override
        public String toString() {
            if (delegate == null) {
                return "[" + rolle + ": <lazy loaded>]";
            } else {
                return standardToString();
            }
        }
    }
}
