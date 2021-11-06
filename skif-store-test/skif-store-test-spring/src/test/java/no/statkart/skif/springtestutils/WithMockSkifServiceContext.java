package no.statkart.skif.springtestutils;

import no.statkart.skif.service.ServerInjector;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TestExecutionListener that wraps test methods in a SKIF ServiceRequestScope that contains a ServiceRequestContext
 *  * object similar to what SKIF Web Services creates
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(value = MockSkifServiceContextTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface WithMockSkifServiceContext {
    Class<? extends ServerInjector> injectorClass();
}


