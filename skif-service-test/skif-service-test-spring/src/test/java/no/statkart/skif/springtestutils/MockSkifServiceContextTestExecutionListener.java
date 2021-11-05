package no.statkart.skif.springtestutils;

import no.statkart.skif.service.ServerInjector;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.scope.ServiceRequestScope;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Annotation that specifies that test methods should be wrapped in a SKIF ServiceRequestScope that contains a
 * ServiceRequestContext object similar to what SKIF Web Services creates.
 */
public class MockSkifServiceContextTestExecutionListener extends AbstractTestExecutionListener implements Ordered {

    @Override
    public void beforeTestExecution(TestContext testContext) {
        ServiceRequestScope scope = getServiceRequestScope(testContext);
        scope.enter();
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                SecurityContextHolder.getContext().getAuthentication(),
                0);
        scope.seed(ServiceRequestContext.class, serviceRequestContext);
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        ServiceRequestScope scope = getServiceRequestScope(testContext);
        scope.exit();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    private ServiceRequestScope getServiceRequestScope(TestContext testContext) {
        WithMockSkifServiceContext annotation = testContext.getTestClass().getAnnotation(WithMockSkifServiceContext.class);
        ServerInjector serverInjector = testContext.getApplicationContext().getBean(annotation.injectorClass());
        ServiceRequestScope scope = serverInjector.getInjector().getInstance(ServiceRequestScope.class);
        return scope;
    }

}
