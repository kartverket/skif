package no.statkart.skif.store.persistence;


public class OracleArrayLongBubbleIdConverter extends OracleArrayBubbleIdConverter {
    public OracleArrayLongBubbleIdConverter() {
        super(ORACLE_NUMBER_LIST_TYPE);
    }

    protected OracleArrayLongBubbleIdConverter(String oracleArrayType) {
        super(oracleArrayType);
    }

}

