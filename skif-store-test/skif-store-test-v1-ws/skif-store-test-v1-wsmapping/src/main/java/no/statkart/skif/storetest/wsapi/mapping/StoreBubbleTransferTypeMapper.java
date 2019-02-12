package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.9
 */
public class StoreBubbleTransferTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer,StoreBubbleTransfer> {
    public StoreBubbleTransferTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer.class, StoreBubbleTransfer.class);
    }

    @Override
    public no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer mapDomainObject(StoreBubbleTransfer source) {
        no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer target = createWsapiT();
        target.setBubbleObjects(getMapping().d2w(source.getBubbleObjects().values(), StoreTestBubbleList.class));
        target.setLockedIds(getMapping().d2w(source.getLockedIds(), StoreTestBubbleIdList.class));
        return target;
    }

    @Override
    public StoreBubbleTransfer mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer source) {
        return new StoreBubbleTransfer(null, getMapping().w2d(source.getBubbleObjects(),List.class ), getMapping().w2d(source.getLockedIds(), List.class));
    }

}