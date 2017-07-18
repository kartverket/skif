package no.statkart.skif.mapping;

import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.store.InverseRelation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Mapper {@link InverseRelation} til korresponderende JAXB-klasse.
 *
 * @param <WsapiT>    Klassen i WS-API
 * @param <DomainT>   InverseRelations-type, f.eks <code>InverseRelation&lt;Set&lt;FooId&gt;&gt;</code>
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class InverseRelationTypeMapper<WsapiT, DomainT extends InverseRelation> extends AbstractTypeMapper<WsapiT, InverseRelation, Mapping> {
    private final PropertyDescriptor materialisedProperty;
    private final PropertyDescriptor valueProperty;
    private final TypeLiteral<?> internalType;
    private final Constructor<InverseRelation> domainConstructor;

    /**
     * @param wsapiClass       klassen i WS-API-domenet
     * @param domainType       InverseRelations-type, f.eks <code>new TypeLiteral&lt;InverseRelation&lt;Set&lt;FooId&gt;&gt;&gt;(){}</code>
     */
    public InverseRelationTypeMapper(Class<WsapiT> wsapiClass, TypeLiteral<DomainT> domainType) {
        super(wsapiClass, InverseRelation.class, Mapping.class);

        try {
            domainConstructor = InverseRelation.class.getDeclaredConstructor();
            domainConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new MappingException("Unable to find constructor for " + domainType.getRawType());
        }

        try {
            internalType = domainType.getReturnType(domainType.getRawType().getMethod("get"));
        } catch (NoSuchMethodException e) {
            throw new MappingException("Unable to find InverseRelation.get() on " + domainType.getRawType());
        }

        try {
            materialisedProperty = new PropertyDescriptor("materialised", wsapiClass);
            valueProperty = new PropertyDescriptor("cachedValue", wsapiClass);
        } catch (IntrospectionException e) {
            throw new MappingException("Unable to find accessors on " + wsapiClass.getName(), e);
        }
    }

    @Override
    public WsapiT mapDomainObject(InverseRelation source) {
        WsapiT target = createWsapiT();

        try {
            materialisedProperty.getWriteMethod().invoke(target, source.isMaterialised());
            valueProperty.getWriteMethod().invoke(target, getMapping().d2w(source.getCached(), valueProperty.getPropertyType()));
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not set fields on " + target.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not set fields on " + target.getClass(), e.getTargetException());
        }

        return target;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InverseRelation mapWsapiObject(WsapiT source) {
        final InverseRelation target;
        try {
            target = domainConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Could not create new instance of " + getDomainClass(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not create new instance of " + getDomainClass(), e.getTargetException());
        }

        try {
            Boolean materialised = (Boolean) materialisedProperty.getReadMethod().invoke(source);
            Object value = valueProperty.getReadMethod().invoke(source);

            target.setMaterialised(materialised);
            target.setCached(getMapping().w2d(value, internalType));
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not read fields on " + source.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not read fields on " + source.getClass(), e.getTargetException());
        }

        return target;
    }
}
