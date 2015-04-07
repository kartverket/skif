package no.statkart.skif.store.persistence;


/**
 */
public class OracleArrayStringBubbleIdConverter extends OracleArrayBubbleIdConverter {
    public OracleArrayStringBubbleIdConverter() {
        super(ORACLE_STRING_LIST_TYPE);
    }

    protected OracleArrayStringBubbleIdConverter(String oracleArrayType) {
        super(oracleArrayType);
    }

}

