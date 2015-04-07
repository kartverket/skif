package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;

/**
 */
public class OracleArrayBubbleIdConverter  extends OracleArrayConverter<BubbleId<?>> {

    protected OracleArrayBubbleIdConverter(String oracleArrayType) {
        super(oracleArrayType);
    }

    @Override
    protected Object toValue(BubbleId<?> object) {
        return object.getValue();
    }

    @Override
    protected Object[] toObjectArray(Connection sqlConnection, Collection<? extends BubbleId<?>> objects) {
        Object[] list = new Object[objects.size()];
        int i = 0;
        SnapshotVersion snapshotVersion = null;
        for (Iterator<? extends BubbleId> iterator = objects.iterator(); iterator.hasNext(); i++) {
            BubbleId bubbleId = iterator.next();
            if (snapshotVersion == null) {
                snapshotVersion = bubbleId.getSnapshotVersion();
            } else {
                if (snapshotVersion != bubbleId.getSnapshotVersion()) {
                    throw new IllegalStateException(String.format("Collection contains multiple SnapshotVersions, expected %s for id %s", snapshotVersion, bubbleId));
                }
            }
            list[i] = toValue(bubbleId);
        }
        return list;
    }

}


