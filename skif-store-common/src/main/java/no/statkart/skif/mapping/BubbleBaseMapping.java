package no.statkart.skif.mapping;

import no.statkart.skif.mapper.BaseMapping;
import no.statkart.skif.mapper.Mapping;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleBaseMapping extends BaseMapping {
    public boolean isWsapiBubbleObject(Object object);
    public boolean isWsapiBubbleId(Object object);
    public void setWsapiId(Object wsapiBubbleObject, Object wsapiId);
    public Object getWsapiId(Object wsapiBubbleObject);
}
