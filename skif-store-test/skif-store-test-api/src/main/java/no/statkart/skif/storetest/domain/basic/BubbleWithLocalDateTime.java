package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import org.joda.time.LocalDateTime;

/**
 * Testboble for LocalDateTime.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class BubbleWithLocalDateTime extends AbstractStoreTestBubble {
    private LocalDateTime tidspunkt;

    @Override
    public BubbleWithLocalDateTimeId<?> getId() {
        return (BubbleWithLocalDateTimeId<?>) super.getId();
    }

    public LocalDateTime getTidspunkt() {
        return tidspunkt;
    }

    public void setTidspunkt(LocalDateTime tidspunkt) {
        this.tidspunkt = tidspunkt;
    }
}
