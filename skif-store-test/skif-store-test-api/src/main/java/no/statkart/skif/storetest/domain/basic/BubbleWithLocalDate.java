package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;
import org.joda.time.LocalDate;

/**
 * Testboble for LocalDate.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class BubbleWithLocalDate extends AbstractStoreTestBubble {
    private LocalDate dato;

    @Override
    public BubbleWithLocalDateId<?> getId() {
        return (BubbleWithLocalDateId<?>) super.getId();
    }

    public LocalDate getDato() {
        return dato;
    }

    public void setDato(LocalDate dato) {
        this.dato = dato;
    }
}
