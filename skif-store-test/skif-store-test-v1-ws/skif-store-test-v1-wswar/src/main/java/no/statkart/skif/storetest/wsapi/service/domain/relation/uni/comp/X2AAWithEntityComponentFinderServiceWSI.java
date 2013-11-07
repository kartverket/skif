package no.statkart.skif.storetest.wsapi.service.domain.relation.uni.comp;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * TODO: Denne service bruker feiler parameter typer. Ligg inn riktige når de er modellert i basic.xsd
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface X2AAWithEntityComponentFinderServiceWSI extends ServiceWSI {
    public X2AAWithEntityComponentIdListForX2BBOneIdMap findInvSomeBBIds(@WebParam(name = "x2BBOneIds") X2BBOneIdList x2BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
    public X2AAWithEntityComponentIdForX2CCManyIdMap findInvSomeCCsId(@WebParam(name = "x2CCManyIds") X2CCManyIdList x2CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
}
