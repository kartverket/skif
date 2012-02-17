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
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setValue(source.getStringValue());
        target.setSnapshotVersion(map.d2w(source.getSnapshotVersion()));
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DomainT target;
        Class valueType = BubbleIds.getValueType(getDomainClass());
        if (valueType == Long.class) {
            target = BubbleIds.createInstance(getDomainClass(), Long.valueOf(map.w2d(source.getValue())), map.w2d(source.getSnapshotVersion()));
        } else {
            target = BubbleIds.createInstance(getDomainClass(), map.w2d(source.getValue()), map.w2d(source.getSnapshotVersion()));
        }
        return target;
    }
}