package no.statkart.skif.mapper;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Forenkler opprettelse av map som skal sendes til {@link MappingResolver#overrideClassMappings(java.util.Map)}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class MappingOverrideBuilder {
    private final ImmutableMap.Builder<Class<?>, Class<?>> builder = ImmutableMap.builder();

    public MappingOverrideBuilder add(Class<?> from, Class<?> to) {
        builder.put(from, to);
        return this;
    }

    public MappingOverrideBuilder addBidirectional(Class<?> a, Class<?> b) {
        builder.put(a, b);
        builder.put(b, a);
        return this;
    }

    public Map<Class<?>, Class<?>> build() {
        return builder.build();
    }
}
