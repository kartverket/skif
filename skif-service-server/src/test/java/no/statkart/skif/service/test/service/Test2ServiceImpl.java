package no.statkart.skif.service.test.service;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class Test2ServiceImpl implements Test2Service {

    // Denne krever at injectoren har et aktivt ServiceRequestScope
    final ServiceRequestContext serviceRequestContext;

    @Inject
    public Test2ServiceImpl(ServiceRequestContext serviceRequestContext) {
        this.serviceRequestContext = serviceRequestContext;
    }

    @Override
    public String helloWorld(String s) {
        return "Hello2: " + s + " username: " + serviceRequestContext.getUserName();
    }
}
