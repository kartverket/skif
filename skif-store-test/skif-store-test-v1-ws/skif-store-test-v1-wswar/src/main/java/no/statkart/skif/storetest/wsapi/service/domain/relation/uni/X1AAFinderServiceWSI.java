package no.statkart.skif.storetest.wsapi.service.domain.relation.uni;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.X1AAIdForX1CCManyIdMap;
import no.statkart.skif.storetest.wsapi.domain.basic.X1AAIdSetForX1BBOneIdMap;
import no.statkart.skif.storetest.wsapi.domain.basic.X1BBOneIdCollection;
import no.statkart.skif.storetest.wsapi.domain.basic.X1CCManyIdCollection;

import javax.jws.WebParam;

/**
 * @author Thomas Berg
 */
public interface X1AAFinderServiceWSI extends ServiceWSI {
    public X1AAIdSetForX1BBOneIdMap findInvSomeBBIds(@WebParam(name = "x1BBOneIds") X1BBOneIdCollection x1BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);
    public X1AAIdForX1CCManyIdMap findInvSomeCCsId(@WebParam(name = "x1CCManyIds") X1CCManyIdCollection x1CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);
}
