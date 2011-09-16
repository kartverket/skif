package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestBubbleIdTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId, DomainT extends StoreTestBubbleId> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {

    public StoreTestBubbleIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setValue(source.getStringValue());
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DomainT target = getDomainClass().getConstructor(Long.class).newInstance(Long.parseLong(source.getValue()));
        return target;
    }
}