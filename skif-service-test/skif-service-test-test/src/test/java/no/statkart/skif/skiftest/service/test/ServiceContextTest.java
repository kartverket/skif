package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;
import no.statkart.skif.skiftest.service.test1.Test1Service;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test
public class ServiceContextTest extends SkifTestCase {

    @Inject
    Test1Service test1Service;

    @Inject
    Provider<SkifTestServiceContext> skifTestServiceContextProvider;

    @Inject
    Provider<ServiceContext> serviceContextProvider;

    public ServiceContextTest() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    public void testPropertiesBlirMed(){

        String defaultVersionText = test1Service.helloVersion("ogaboga");

        Assert.assertTrue(defaultVersionText.contains("1.0"));
        SkifTestServiceContext skifTestServiceContext = skifTestServiceContextProvider.get();
        ServiceContext serviceContext = serviceContextProvider.get();

        Assert.assertEquals(serviceContext, skifTestServiceContext);

        skifTestServiceContext.setSystemVersion("2.4");
        String tofireVersionText = test1Service.helloVersion("ogaboga");
        Assert.assertTrue(tofireVersionText.contains("2.4"));

    }


}
