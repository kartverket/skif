package no.statkart.skif.storetest2.config;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RunOnServerRemoteServiceModule;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.sequence.IdServiceImpl;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreSessionClient;
import no.statkart.skif.store.service.StoreService;

import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class StoreTest2ClientModule extends SkifModule {
    public StoreTest2ClientModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ClientModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        // TODO: Midlertidig kode inntil prosjektet får seg noen ws-moduler
        final Mapping mapping = (Mapping) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Mapping.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
//        final StoreTest2Mapping mapping = new StoreTest2Mapper().getMapping();
//        final StoreTest2ExceptionMapping exceptionMapping = new StoreTest2ExceptionMapper().getMapping();

        install(new RemoteServerModule(moduleConfiguration));
        install(new RunOnServerRemoteServiceModule(moduleConfiguration));
        install(new RemoteServiceModule(moduleConfiguration, new StoreTest2StoreServices().getServices(), mapping)
//                .setExceptionMapping(exceptionMapping)
//                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );
        install(new RemoteServiceModule(moduleConfiguration, new StoreTest2SequenceBlockAllocatorServices().getServices(), mapping)
//                .setExceptionMapping(exceptionMapping)
//                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );
        bind(SequenceBlockAllocatorService.class).to(no.statkart.skif.storetest2.service.id.SequenceBlockAllocatorService.class);
        bind(IdService.class).to(IdServiceImpl.class);

        bind(StoreService.class).to(no.statkart.skif.storetest2.service.store.StoreService.class);

        install(new RemoteServiceModule(moduleConfiguration, new StoreTest2TestServices().getServices(), mapping)
//                .setExceptionMapping(exceptionMapping)
//                .setServiceContextMapperClass(StoreTestServiceContextMapper.class)
        );
        bind(no.statkart.skif.service.test.TestdataService.class).to(no.statkart.skif.storetest2.service.test.TestdataService.class);

        // Legger til DBLockerService dersom man kjører i singleVm. Denne tjenesten finnes ikke som en webservice, og kan derfor ikke legges til ved kjøring av tester i client/server
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            install(new RemoteServiceModule(moduleConfiguration, new StoreTest2LocalServices().getServices(), mapping)); // Angir bare en mapping, siden det er irrelevant for en intern tjeneste
            bind(SkifUtil.typeLiteral(DBLockerService.class, Long.class)).to(no.statkart.skif.storetest2.service.locker.DBLockerService.class);
            bind(SkifUtil.typeLiteral(DBLockerInTransactionService.class, Long.class)).to(no.statkart.skif.storetest2.service.locker.DBLockerInTransactionService.class);
        }
    }

    @Provides
    @Singleton
    Store storeProvider(StoreService storeService, Injector injector) {
        StoreSessionClient storeSession = new StoreSessionClient(storeService);
        Store store = new StoreClient(storeSession, injector);
        injector.injectMembers(store);
        return store;
    }

}
