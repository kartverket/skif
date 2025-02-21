package no.statkart.skif.storetest.filter;

import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreSessionWriteListener;
import no.statkart.skif.storetest.domain.demo.AggregertObjekt;

import java.sql.Timestamp;

/**
 * Legger på metadata på AggregertObjekt.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class AggregertObjektFilter implements StoreSessionWriteListener {
    @Override
    public <T extends BubbleObject> T onInsert(T storeBubbleObject, T persistentBubbleObject) {
        if (storeBubbleObject instanceof AggregertObjekt) {
            setSistOppdatertAv((AggregertObjekt) storeBubbleObject);
        }
        return storeBubbleObject;
    }

    @Override
    public <T extends BubbleObject> T onUpdate(T storeBubbleObject, T persistentBubbleObject) {
        if (storeBubbleObject instanceof AggregertObjekt) {
            setSistOppdatertAv((AggregertObjekt) storeBubbleObject);
        }
        return storeBubbleObject;
    }

    @Override
    public <T extends BubbleObject> T onDelete(T storeBubbleObject, T persistentBubbleObject) {
        return storeBubbleObject;
    }

    private void setSistOppdatertAv(AggregertObjekt aggregertObjekt) {
        aggregertObjekt.setSistOppdatertAv("foobar");
        aggregertObjekt.setSistOppdatert(new Timestamp(System.currentTimeMillis()));
    }
}
