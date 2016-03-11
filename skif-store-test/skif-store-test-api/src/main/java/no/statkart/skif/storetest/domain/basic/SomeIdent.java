package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.ConcatenatedFieldsSerialization;

import java.io.Serializable;

/**
 * Eksempel klasse som støtter feltserialisering som en String
 */
public class SomeIdent implements ConcatenatedFieldsSerialization, Serializable {
    private long field1;
    private String field2;

    public SomeIdent() {
    }

    public SomeIdent(long field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public SomeIdent(ConcatenatedFields fields) {
        String[] values = fields.getFields();
        this.field1 = values[0] == null ? 0L : Long.parseLong(values[0]);
        this.field2 = values[1];

    }

    public long getField1() {
        return field1;
    }

    public void setField1(long field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Override
    public ConcatenatedFields toConcatinatedFields() {
        return ConcatenatedFields.create(field1,field2);
    }
}
