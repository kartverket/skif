package no.statkart.skif.storetest.wsapi.exception.mapping;

import no.statkart.skif.exception.AttemptDeleteException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.exception.AttemptDeleteFaultInfo;

class AttemptDeleteFaultInfoTypeMapper extends AbstractTypeMapper<AttemptDeleteFaultInfo, AttemptDeleteException, StoreTestExceptionMapping> {
    public AttemptDeleteFaultInfoTypeMapper() {
        super(AttemptDeleteFaultInfo.class, AttemptDeleteException.class, StoreTestExceptionMapping.class);
    }

    @Override
    public AttemptDeleteFaultInfo mapDomainObject(AttemptDeleteException source) {
        StoreTestBubbleId bubbleId = getMapping().d2w(source.getBubbleId(), StoreTestBubbleId.class);

        AttemptDeleteFaultInfo faultInfo = new AttemptDeleteFaultInfo();
        faultInfo.setBubbleId(bubbleId);
        return faultInfo;
    }

    @Override
    public AttemptDeleteException mapWsapiObject(AttemptDeleteFaultInfo source) {
        BubbleId<?> bubbleId = getMapping().w2d(source.getBubbleId(), BubbleId.class);
        return new AttemptDeleteException(bubbleId);
    }
}
