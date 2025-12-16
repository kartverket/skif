package no.statkart.skif.store.persistence.hibernate.type;

import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.ConcatenatedFieldsSerialization;

import java.io.Serializable;

public class AnyConcatenatedFieldsEmbeddable {

    //TODO: Update to record with hibernate 6.2

    private String a_value;

    private String b_className;

    public AnyConcatenatedFieldsEmbeddable() {
    }


    public String getA_value() {
        return a_value;
    }

    public void setA_value(String a_value) {
        this.a_value = a_value;
    }

    public String getB_className() {
        return b_className;
    }

    public void setB_className(String b_className) {
        this.b_className = b_className;
    }
}
