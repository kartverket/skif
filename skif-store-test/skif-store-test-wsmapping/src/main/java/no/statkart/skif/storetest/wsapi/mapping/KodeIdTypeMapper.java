package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.storetest.domain.TestKodeId;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodeIdTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId, DomainT extends TestKodeId> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {

    public KodeIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setValue(source.getStringValue());
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DomainT target = BubbleIds.createInstance(getDomainClass(), Long.parseLong(source.getValue()));
        return target;
    }
}