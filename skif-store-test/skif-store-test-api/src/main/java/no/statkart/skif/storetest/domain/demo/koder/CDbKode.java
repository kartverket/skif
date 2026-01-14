package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class CDbKode extends StoreTestDbKode {
    @Override
    public CDbKodeId<?> getId() {
        CDbKodeId<?> id = (CDbKodeId<?>) super.getId();
        if (id == null) {
            return null;
        }
        if (id.getClass() != CDbKodeId.class) {
            return id;
        }
        if (this instanceof C1DbKode) {
            return new C1DbKodeId(id.getValue(), id.getSnapshotVersion());
        }
        if (this instanceof C2DbKode) {
            return new C2DbKodeId(id.getValue(), id.getSnapshotVersion());
        }
        Class<?> entityClass = getClass();
        while ((entityClass.getName().contains("$$") || entityClass.getName().contains("HibernateProxy"))
            && entityClass.getSuperclass() != null) {
            entityClass = entityClass.getSuperclass();
        }
        String idClassName = entityClass.getName() + "Id";
        try {
            Class<?> idClass = Class.forName(idClassName);
            if (CDbKodeId.class.isAssignableFrom(idClass)) {
                return (CDbKodeId<?>) BubbleIds.createInstance(
                    (Class<? extends BubbleId<?>>) idClass,
                    id.getValue(),
                    id.getSnapshotVersion()
                );
            }
        } catch (ClassNotFoundException ignored) {
            // Fall through and return base id.
        }
        return id;
    }

    @Override
    public KodelisteId<?> getKodelisteId() {
        if (this instanceof C1DbKode) {
            return C1DbKodeId.KODELISTE_ID.asSnapshotVersion(getId());
        }
        if (this instanceof C2DbKode) {
            return C2DbKodeId.KODELISTE_ID.asSnapshotVersion(getId());
        }
        return super.getKodelisteId();
    }
}
