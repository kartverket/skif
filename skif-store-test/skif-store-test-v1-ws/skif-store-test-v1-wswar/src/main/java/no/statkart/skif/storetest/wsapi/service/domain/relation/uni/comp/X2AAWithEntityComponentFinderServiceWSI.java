package no.statkart.skif.storetest.wsapi.service.domain.relation.uni.comp;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2AAWithEntityComponentIdForX2CCManyIdMap;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2AAWithEntityComponentIdListForX2BBOneIdMap;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2BBOneIdList;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2CCManyIdList;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * TODO: Denne service bruker feiler parameter typer. Legg inn riktige når de er modellert i basic.xsd
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface X2AAWithEntityComponentFinderServiceWSI extends ServiceWSI {

    X2AAWithEntityComponentIdListForX2BBOneIdMap findInvSomeBBIds(@WebParam(name = "x2BBOneIds") X2BBOneIdList x2BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    X2AAWithEntityComponentIdForX2CCManyIdMap findInvSomeCCsId(@WebParam(name = "x2CCManyIds") X2CCManyIdList x2CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

}
