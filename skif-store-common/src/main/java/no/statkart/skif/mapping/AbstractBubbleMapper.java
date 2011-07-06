package no.statkart.skif.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.ObjectFactory;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractBubbleMapper extends AbstractMapper implements BubbleBaseMapping {
    public AbstractBubbleMapper(Class<? extends BubbleMapping> mappingClass) {
        super(mappingClass);
    }

    public AbstractBubbleMapper(Class<? extends BubbleMapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory, boolean mergeMapping) {
        super(mappingClass, wsapiObjectFactory, domainObjectFactory, mergeMapping);
    }
}
