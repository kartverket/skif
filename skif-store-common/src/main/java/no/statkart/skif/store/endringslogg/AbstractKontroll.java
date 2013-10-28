package no.statkart.skif.store.endringslogg;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Baseklasse for kontroll beregninger.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public abstract class AbstractKontroll<I extends BubbleId<?>> implements Serializable{
    private static final long serialVersionUID = 1L;
}
