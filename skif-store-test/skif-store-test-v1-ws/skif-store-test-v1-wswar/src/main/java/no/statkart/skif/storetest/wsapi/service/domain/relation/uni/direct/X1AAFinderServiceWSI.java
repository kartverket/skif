package no.statkart.skif.storetest.wsapi.service.domain.relation.uni.direct;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1AAIdForX1CCManyIdMap;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1AAIdListForX1BBOneIdMap;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1BBOneIdList;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1CCManyIdList;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * @author Thomas Berg
 */
public interface X1AAFinderServiceWSI extends ServiceWSI {

    X1AAIdListForX1BBOneIdMap findInvSomeBBIds(@WebParam(name = "x1BBOneIds") X1BBOneIdList x1BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    X1AAIdForX1CCManyIdMap findInvSomeCCsId(@WebParam(name = "x1CCManyIds") X1CCManyIdList x1CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

}
