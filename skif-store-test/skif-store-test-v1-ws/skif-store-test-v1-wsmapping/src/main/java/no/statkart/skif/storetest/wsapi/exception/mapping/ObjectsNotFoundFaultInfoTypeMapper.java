package no.statkart.skif.storetest.wsapi.exception.mapping;

import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.ObjectsNotFoundException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.exception.ObjectsNotFoundFaultInfo;

import java.util.Collection;

public class ObjectsNotFoundFaultInfoTypeMapper extends AbstractTypeMapper<ObjectsNotFoundFaultInfo, ObjectsNotFoundException, StoreTestExceptionMapping> {
    public ObjectsNotFoundFaultInfoTypeMapper() {
        super(ObjectsNotFoundFaultInfo.class, ObjectsNotFoundException.class, StoreTestExceptionMapping.class);
    }

    @Override
    public ObjectsNotFoundFaultInfo mapDomainObject(ObjectsNotFoundException source) {
        ObjectsNotFoundFaultInfo faultInfo = new ObjectsNotFoundFaultInfo();

        faultInfo.setIdsNotFound(getMapping().d2w(source.getIdsNotFound(), StoreTestBubbleIdList.class));

        return faultInfo;
    }

    @Override
    public ObjectsNotFoundException mapWsapiObject(ObjectsNotFoundFaultInfo source) {
        if (source.getExceptionDetail().getClassName().equals(ObjectNotFoundException.class.getName()) && source.getIdsNotFound().getItem().size() == 1) {
            StoreTestBubbleId bubbleId = source.getIdsNotFound().getItem().get(0);
            return new ObjectNotFoundException(getMapping().w2d(bubbleId, BubbleId.class));
        } else {
            Collection<BubbleId<?>> bubbleIds = getMapping().w2d(source.getIdsNotFound(), new TypeLiteral<Collection<BubbleId<?>>>(){});
            return new ObjectsNotFoundException(bubbleIds);
        }
    }
}
