package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;


/**
 */
public class OracleArrayLongBubbleIdConverter extends OracleArrayBubbleIdConverter {
    public OracleArrayLongBubbleIdConverter() {
        super(ORACLE_NUMBER_LIST_TYPE);
    }

    protected OracleArrayLongBubbleIdConverter(String oracleArrayType) {
        super(oracleArrayType);
    }

}

