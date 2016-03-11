package no.statkart.skif.wsversioning.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.wsversioning.domain.VegId;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementasjon av {@link VegService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class VegServiceImpl implements VegService {
    private final Provider<Map<BubbleId<?>, BubbleObject>> bubbleProvider;

    @Inject
    public VegServiceImpl(@Named("bubbles") Provider<Map<BubbleId<?>, BubbleObject>> bubbleProvider) {
        this.bubbleProvider = bubbleProvider;
    }

    @Override
    public Set<VegId<?>> findAlleVeger() {
        Set<VegId<?>> vegIds = new LinkedHashSet<VegId<?>>();
        for (BubbleId<?> bubbleId : bubbleProvider.get().keySet()) {
            if (bubbleId instanceof VegId) {
                vegIds.add((VegId<?>) bubbleId);
            }
        }
        return vegIds;
    }
}
