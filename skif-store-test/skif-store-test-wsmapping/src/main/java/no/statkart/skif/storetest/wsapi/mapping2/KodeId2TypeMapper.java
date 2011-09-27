package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.store2.BubbleIds2;
import no.statkart.skif.storetest.domain2.TestKodeId2;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodeId2TypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodeId2, DomainT extends TestKodeId2> extends AbstractStore2TestTypeMapper<WsapiT,DomainT> {

    public KodeId2TypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setValue(source.getStringValue());
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DomainT target = BubbleIds2.createInstance(getDomainClass(), Long.parseLong(source.getValue()));
        return target;
    }
}