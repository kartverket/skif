package no.statkart.skif.exception;

import com.google.common.collect.ImmutableSet;
import no.statkart.skif.store.BubbleId;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

/**
 * Angir at noen objekt med gitte id-er ikke finnes. Det kan f.eks være fordi de har blitt slettet.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class ObjectsNotFoundException extends FinderException {
    private static final long serialVersionUID = 1L;

    private final Set<BubbleId<?>> idsNotFound;

    public ObjectsNotFoundException(Collection<BubbleId<?>> idsNotFound) {
        super(String.valueOf(idsNotFound));
        this.idsNotFound = ImmutableSet.copyOf(idsNotFound);
    }

    public ObjectsNotFoundException(Collection<BubbleId<?>> idsNotFound, Throwable cause) {
        super(String.valueOf(idsNotFound), cause);
        this.idsNotFound = ImmutableSet.copyOf(idsNotFound);
    }

    public ObjectsNotFoundException(Collection<BubbleId<?>> idsNotFound, Throwable cause, Logger logger) {
        super(String.valueOf(idsNotFound), cause, logger);
        this.idsNotFound = ImmutableSet.copyOf(idsNotFound);
    }

    public Set<BubbleId<?>> getIdsNotFound() {
        return idsNotFound;
    }
}
