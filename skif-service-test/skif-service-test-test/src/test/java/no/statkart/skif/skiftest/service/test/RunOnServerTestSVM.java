package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import com.google.inject.Injector;
import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 */
@Test
public class RunOnServerTestSVM extends SkifTestCase {
    public RunOnServerTestSVM() {
        setSingleVm(true);
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    @Test
    public void testRunOnServerVariant1() {
        BeanManagedTransactionRunOnServerService runOnServerService = injector.getInstance(BeanManagedTransactionRunOnServerService.class);
        Object result = runOnServerService.run(new RunOnServerMethod() {
            @Inject
            AService serviceA;
            @Inject
            ServiceRequestContext serviceRequestContext;

            public Object run() {
                String result = serviceA.m1(Arrays.asList("BService.m2"));
                assertTrue(serviceRequestContext.isBeanManagedTransaction());
                assertTrue(serviceRequestContext.inTx());
                assertEquals(result, "AService.m1 BService.m2");

                return result;
            }
        });
    }

    @Test
    public void testRunOnServerVariant2() {
        Object serverResult = new RunOnServer(injector).runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            AService serviceA;
            @Inject
            ServiceRequestContext serviceRequestContext;

            public Object run() {
                String result = serviceA.m1(Arrays.asList("BService.m2"));
                assertTrue(serviceRequestContext.isBeanManagedTransaction());
                assertTrue(serviceRequestContext.inTx());
                assertEquals(result, "AService.m1 BService.m2");

                return result;
            }
        });
    }


    static class RunOnServer {
        private final Injector injector;

        public RunOnServer(Injector injector) {
            this.injector = injector;
        }

        public Object runInBeanManagedTransaction(RunOnServerMethod method) {
            BeanManagedTransactionRunOnServerService runOnServerService = injector.getInstance(BeanManagedTransactionRunOnServerService.class);
            return runOnServerService.run(method);
        }
    }


}
