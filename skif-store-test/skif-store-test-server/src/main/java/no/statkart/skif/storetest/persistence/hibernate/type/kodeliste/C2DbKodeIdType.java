package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;

public class C2DbKodeIdType extends CDbKodeIdType {
    @Override
    public Class<? extends C2DbKodeId> returnedClass() {
        return C2DbKodeId.class;
    }
}
