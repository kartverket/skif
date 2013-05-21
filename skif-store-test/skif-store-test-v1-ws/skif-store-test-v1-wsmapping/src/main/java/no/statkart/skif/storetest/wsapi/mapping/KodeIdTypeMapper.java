package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodeId;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodeIdTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId, DomainT extends StoreTestKodeId> extends AbstractStoreTestTypeMapper<WsapiT, DomainT> {

    public KodeIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setValue(source.getStringValue());
        target.setSnapshotVersion(getMapping().d2w(source.getSnapshotVersion()));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target;
        Class valueType = BubbleIds.getValueType(getDomainClass());
        if (valueType == Long.class) {
            target = BubbleIds.createInstance(getDomainClass(), Long.valueOf(getMapping().w2d(source.getValue())), getMapping().w2d(source.getSnapshotVersion()));
        } else {
            target = BubbleIds.createInstance(getDomainClass(), getMapping().w2d(source.getValue()), getMapping().w2d(source.getSnapshotVersion()));
        }
        return target;
    }
}