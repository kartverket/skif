package no.statkart.skif.mapping;

import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.store.KodelisteTransfer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class KodelisteTransferTypeMapper<WsapiT> extends AbstractTypeMapper<WsapiT, KodelisteTransfer, Mapping> {
    private final PropertyDescriptor kodelisteIdsProperty;
    private final PropertyDescriptor objectsProperty;

    public KodelisteTransferTypeMapper(Class<WsapiT> wsapiClass) {
        super(wsapiClass, KodelisteTransfer.class, Mapping.class);

        try {
            kodelisteIdsProperty = new PropertyDescriptor("kodelisteIds", wsapiClass);
            objectsProperty = new PropertyDescriptor("objects", wsapiClass);
        } catch (IntrospectionException e) {
            throw new MappingException("Unable to find accessors on " + wsapiClass.getName(), e);
        }
    }

    @Override
    public WsapiT mapDomainObject(KodelisteTransfer source) {
        WsapiT target = createWsapiT();
        try {
            kodelisteIdsProperty.getWriteMethod().invoke(target, getMapping().d2w(source.getKodelisteIds(), kodelisteIdsProperty.getPropertyType()));
            objectsProperty.getWriteMethod().invoke(target, getMapping().d2w(source.getObjects().values(), objectsProperty.getPropertyType()));
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not set fields on " + target.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not set fields on " + target.getClass(), e);
        }
        return target;
    }

    @Override
    public KodelisteTransfer mapWsapiObject(WsapiT source) {
        Object wsKodelisteIds, wsObjects;

        try {
            wsKodelisteIds = kodelisteIdsProperty.getReadMethod().invoke(source);
            wsObjects = objectsProperty.getReadMethod().invoke(source);
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not read fields on " + source.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not read fields on " + source.getClass(), e);
        }

        ArrayList kodelistIds = getMapping().w2d(wsKodelisteIds, ArrayList.class);
        ArrayList objects = getMapping().w2d(wsObjects, ArrayList.class);
        return new KodelisteTransfer(kodelistIds, objects);
    }
}