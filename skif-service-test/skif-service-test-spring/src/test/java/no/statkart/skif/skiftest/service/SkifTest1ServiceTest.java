package no.statkart.skif.skiftest.service;

import no.statkart.skif.skiftest.config.SkifTestServerInjector;
import no.statkart.skif.skiftest.service.test1.Test1Service;
import no.statkart.skif.springtestutils.MockSkifServiceContextTestExecutionListener;
import no.statkart.skif.springtestutils.WithMockSkifServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@WithMockSkifServiceContext(injectorClass = SkifTestServerInjector.class)
@TestExecutionListeners( // Denne annotasjon kreves i tillegg når man bruker TestNG i stedet for JUnit
        value = {WithSecurityContextTestExecutionListener.class, MockSkifServiceContextTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class SkifTest1ServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    Test1Service test1Service;

    @Autowired
    SkifTestServerInjector injector;

    @Test
    @WithMockUser(username="testUser",roles={"Innsyn"})
    public void testStoreTest1Service() {
        assertEquals(test1Service.helloWorld("Foo"), "Hello1: Foo");
    }
}
