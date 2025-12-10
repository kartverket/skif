package no.statkart.skif.store.persistence.hibernate.type;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AnyConcatenatedFieldsEmbeddable implements Serializable {

    //TODO: Update to record with hibernate 6.2

    private String value;

    private String className;

    public AnyConcatenatedFieldsEmbeddable() {
    }


    public String getValue() {
        return value;
    }

    public void setValue(String a_value) {
        this.value = a_value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String b_className) {
        this.className = b_className;
    }
}
