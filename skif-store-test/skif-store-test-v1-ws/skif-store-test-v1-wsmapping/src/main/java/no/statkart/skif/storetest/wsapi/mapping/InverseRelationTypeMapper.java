package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.store.localization.LocalizedString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class InverseRelationTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.InverseRelation, DomainT extends InverseRelation> extends AbstractStoreTestTypeMapper<WsapiT, DomainT> {
    private Method wsapiGetterForCachedValue;
    private Method wsapiSetterForCachedValue;
    public InverseRelationTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
        try {
            wsapiGetterForCachedValue = wsapiClass.getMethod("getCachedValue");
        } catch (NoSuchMethodException e) {
            throw new ImplementationException("Could not find method 'getCachedValue()' in " + wsapiClass);
        }
        try {
            wsapiSetterForCachedValue = wsapiClass.getMethod("setCachedValue",wsapiGetterForCachedValue.getReturnType());
        } catch (NoSuchMethodException e) {
            throw new ImplementationException("Could not find method 'setCachedValue()' in " + wsapiClass);
        }
    }

    private Type getCachedValueType() {
        return wsapiGetterForCachedValue.getReturnType();
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setMaterialised(source.isMaterialised());
        if (source.isMaterialised()) {
            setCachedValue(target, getMapping().d2w(source.getCached(), getCachedValueType()));
        }
        return target;
    }


    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = createDomainT();
        target.setMaterialised(source.isMaterialised());
        if (source.isMaterialised()) {
            target.setCached(getCachedValue(source));
        }
        return target;
    }

    private void setCachedValue(WsapiT target, Object value) {
        try {
            wsapiSetterForCachedValue.invoke(target, value);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }

    private Object getCachedValue(WsapiT source) {
        try {
            return wsapiSetterForCachedValue.invoke(source);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }
}