package no.statkart.skif.wsversioning.wsapi.v1.exception.mapping;

import no.statkart.skif.exception.AttemptDeleteException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleId;
import no.statkart.skif.wsversioning.wsapi.v1.exception.AttemptDeleteFaultInfo;

class AttemptDeleteFaultInfoTypeMapper extends AbstractTypeMapper<AttemptDeleteFaultInfo, AttemptDeleteException, WSVersioningExceptionMapping> {
    public AttemptDeleteFaultInfoTypeMapper() {
        super(AttemptDeleteFaultInfo.class, AttemptDeleteException.class, WSVersioningExceptionMapping.class);
    }

    @Override
    public AttemptDeleteFaultInfo mapDomainObject(AttemptDeleteException source) {
        WSVersioningBubbleId bubbleId = getMapping().d2w(source.getBubbleId(), WSVersioningBubbleId.class);

        AttemptDeleteFaultInfo faultInfo = new AttemptDeleteFaultInfo();
        faultInfo.setBubbleId(bubbleId);
        return faultInfo;
    }

    @Override
    public AttemptDeleteException mapWsapiObject(AttemptDeleteFaultInfo source) {
        BubbleId<?> bubbleId = getMapping().w2d(source.getBubbleId(), BubbleId.class);
        return new AttemptDeleteException(bubbleId, null);
    }
}
