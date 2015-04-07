package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.persistence.hibernate.type.AnyConcatenatedFieldsType;
import no.statkart.skif.storetest.domain.basic.SomeIdent;

/**
 */
public class AnySomeIdentType extends AnyConcatenatedFieldsType<SomeIdent> {
    @Override
    public Class returnedClass() {
        return SomeIdent.class;
    }
}
