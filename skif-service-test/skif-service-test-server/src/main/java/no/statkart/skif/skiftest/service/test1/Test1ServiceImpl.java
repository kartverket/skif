package no.statkart.skif.skiftest.service.test1;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Test1ServiceImpl implements Test1Service {

    @Inject
    Provider<ServiceContext> serviceContextProvider;

    @Inject
    Provider<SkifTestServiceContext> skifTestServiceContextProvider;

    @Override
    public String helloWorld(String message) {
        return "Hello1: " + message;
    }

    @Override
    public String helloVersion(String message) {
        ServiceContext serviceContext = serviceContextProvider.get();
        SkifTestServiceContext skifTestServiceContext = skifTestServiceContextProvider.get();
        assert serviceContext.equals(skifTestServiceContext);

        return "Hello1: " + message + ". Version: " + skifTestServiceContextProvider.get().getSystemVersion();
    }
}
