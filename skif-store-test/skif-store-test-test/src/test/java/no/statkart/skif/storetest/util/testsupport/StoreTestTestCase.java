package no.statkart.skif.storetest.util.testsupport;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.config.*;
import no.statkart.skif.storetest.wsapi.StoreTestServiceContextMapper;
import no.statkart.skif.storetest.wsapi.exception.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;

/**
 * @author Henrik Fredholm
 */
public class StoreTestTestCase extends SkifTestCase {
    public StoreTestTestCase() {
        setModuleClass(StoreTestClientModule.class);
        setSingleVmServerModuleClass(StoreTestServerModule.class);
    }
}
