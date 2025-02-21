package no.statkart.skif.service;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Provider av call id-er, som er longs som stiger og er unike innenfor oppetiden til en tjener. Flere tråder deler
 * denne, så den må være trådsikker.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Singleton
public class CallIdProvider implements Provider<Long> {
    private final AtomicLong callIdGenerator = new AtomicLong(0L);

    @Override
    public Long get() {
        return callIdGenerator.incrementAndGet();
    }
}
